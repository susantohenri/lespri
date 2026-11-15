package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.AttendanceDao
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.AttendanceRecord
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeType
import edu.lespri.app.domain.model.LedgerEntryType
import edu.lespri.app.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori presensi dengan transaksi atomik anti-race-condition
 * untuk pemotongan (deduction) dan pembalikan (reversal) saldo paket sesi.
 */
@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val database: LespriDatabase,
    private val attendanceDao: AttendanceDao,
    private val classDao: ClassDao,
    private val financeDao: FinanceDao
) : AttendanceRepository {

    override fun getSessionAttendance(sessionId: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceForSession(sessionId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun submitSessionAttendance(
        sessionId: String,
        records: List<AttendanceRecord>
    ): Result<Unit> {
        return runCatching {
            database.withTransaction {
                val now = System.currentTimeMillis()

                for (record in records) {
                    val existingRecord = attendanceDao.getAttendanceRecord(sessionId, record.enrollmentId)
                    val oldStatus = existingRecord?.status ?: AttendanceStatus.UNRECORDED.name
                    val newStatus = record.status.name

                    // Hanya proses ledger jika terjadi perubahan status ke/dari PRESENT
                    if (oldStatus != newStatus) {
                        val enrollment = classDao.getEnrollmentById(record.enrollmentId)
                        if (enrollment != null) {
                            when (enrollment.billingType) {
                                BillingType.PACKAGE.name -> {
                                    if (oldStatus != AttendanceStatus.PRESENT.name && newStatus == AttendanceStatus.PRESENT.name) {
                                        // Pemotongan saldo: delta = -1
                                        val deduction = PackageLedgerEntity(
                                            id = UUID.randomUUID().toString(),
                                            enrollmentId = enrollment.id,
                                            sessionId = sessionId,
                                            deltaSessions = -1,
                                            reason = LedgerEntryType.ATTENDANCE_DEDUCTION.name,
                                            createdAt = now
                                        )
                                        financeDao.insertLedgerEntry(deduction)
                                    } else if (oldStatus == AttendanceStatus.PRESENT.name && newStatus != AttendanceStatus.PRESENT.name) {
                                        // Pembalikan saldo: delta = +1
                                        val reversal = PackageLedgerEntity(
                                            id = UUID.randomUUID().toString(),
                                            enrollmentId = enrollment.id,
                                            sessionId = sessionId,
                                            deltaSessions = 1,
                                            reason = LedgerEntryType.ATTENDANCE_REVERSAL.name,
                                            createdAt = now
                                        )
                                        financeDao.insertLedgerEntry(reversal)
                                    }
                                }

                                BillingType.PER_SESSION.name -> {
                                    if (oldStatus != AttendanceStatus.PRESENT.name && newStatus == AttendanceStatus.PRESENT.name) {
                                        // Generate tagihan per sesi jika siswa PRESENT
                                        val charge = ChargeEntity(
                                            id = UUID.randomUUID().toString(),
                                            enrollmentId = enrollment.id,
                                            chargeType = ChargeType.OTHER.name,
                                            periodStart = sessionId, // Tautkan ke ID sesi
                                            amount = enrollment.feeAmount,
                                            status = ChargeStatus.UNPAID.name,
                                            createdAt = now
                                        )
                                        financeDao.insertCharge(charge)
                                    } else if (oldStatus == AttendanceStatus.PRESENT.name && newStatus != AttendanceStatus.PRESENT.name) {
                                        // Batalkan tagihan per sesi yang belum dibayar terkait sesi ini
                                        val existingCharges = financeDao.getChargesForEnrollmentSync(enrollment.id)
                                        val sessionCharge = existingCharges.firstOrNull {
                                            it.periodStart == sessionId && it.status == ChargeStatus.UNPAID.name
                                        }
                                        if (sessionCharge != null) {
                                            financeDao.updateCharge(sessionCharge.copy(status = ChargeStatus.CANCELLED.name))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Batch upsert seluruh catatan presensi yang telah diperbarui
                attendanceDao.upsertAttendanceBatch(records.map { it.toEntity() })
            }
        }
    }

    override fun countUnrecordedSessions(todayIso: String): Flow<Int> {
        return attendanceDao.countUnrecordedSessions(todayIso)
    }
}
