package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.Charge
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeType
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.model.FinancialSummary
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori keuangan dengan proteksi overpayment dan kalkulasi saldo dinamis.
 */
@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val database: LespriDatabase,
    private val financeDao: FinanceDao,
    private val classDao: ClassDao,
    private val studentDao: StudentDao
) : FinanceRepository {

    override fun getFinancialSummary(): Flow<FinancialSummary> {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        return combine(
            financeDao.getTotalOutstandingDebt(),
            financeDao.getMonthlyRevenue(currentMonth),
            financeDao.getAllActiveCharges()
        ) { totalDebt, monthlyIncome, allCharges ->
            var unpaidCount = 0
            var overdueCount = 0

            for (charge in allCharges) {
                if (charge.status == ChargeStatus.UNPAID.name || charge.status == ChargeStatus.PARTIALLY_PAID.name) {
                    unpaidCount++
                    if (charge.dueDate != null && charge.dueDate < todayIso) {
                        overdueCount++
                    }
                }
            }

            FinancialSummary(
                totalOutstandingDebt = totalDebt,
                currentMonthIncome = monthlyIncome,
                unpaidChargesCount = unpaidCount,
                overdueChargesCount = overdueCount
            )
        }
    }

    override fun getCharges(statusFilter: ChargeStatus?): Flow<List<ChargeWithBalance>> {
        val sourceFlow = if (statusFilter != null) {
            financeDao.getChargesByStatus(statusFilter.name)
        } else {
            financeDao.getAllActiveCharges()
        }

        return sourceFlow.map { chargeEntities ->
            chargeEntities.map { entity ->
                val charge = entity.toDomain()
                val totalPaid = financeDao.getTotalPaidForCharge(entity.id)
                val remaining = (entity.amount - totalPaid).coerceAtLeast(0L)

                val enrollment = classDao.getEnrollmentById(entity.enrollmentId)
                val student = enrollment?.let { studentDao.getStudentById(it.studentId).firstOrNull()?.toDomain() }
                val classGroup = enrollment?.let { classDao.getClassById(it.classId).firstOrNull()?.toDomain() }

                ChargeWithBalance(
                    charge = charge,
                    totalPaid = totalPaid,
                    remainingBalance = remaining,
                    studentName = student?.name ?: "Siswa",
                    className = classGroup?.name ?: "Kelas"
                )
            }
        }
    }

    internal var transactionRunner: (suspend (suspend () -> Any?) -> Any?)? = null

    internal suspend fun <R> runInTransaction(block: suspend () -> R): R {
        val runner = transactionRunner
        return if (runner != null) {
            @Suppress("UNCHECKED_CAST")
            runner { block() } as R
        } else {
            database.withTransaction { block() }
        }
    }

    override suspend fun createCharge(charge: Charge): Result<Unit> {
        return runCatching {
            financeDao.insertCharge(charge.toEntity())
        }
    }

    override suspend fun recordPayment(payment: Payment): Result<Unit> {
        return runCatching {
            runInTransaction {
                val targetCharge = financeDao.getChargeByIdSync(payment.chargeId)
                    ?: throw IllegalArgumentException("Tagihan tidak ditemukan: ID ${payment.chargeId}")

                val totalAlreadyPaid = financeDao.getTotalPaidForCharge(payment.chargeId)
                val remainingBalance = targetCharge.amount - totalAlreadyPaid

                if (payment.amount <= 0L) {
                    throw IllegalArgumentException("Nominal pembayaran harus lebih besar dari Rp 0")
                }

                // Invarian Kritis: Cegah pembayaran berlebih (Overpayment Prevention)
                if (payment.amount > remainingBalance) {
                    throw IllegalArgumentException(
                        "Nominal pembayaran (Rp ${payment.amount}) melebihi sisa tagihan (Rp $remainingBalance)"
                    )
                }

                // 1. Simpan record pembayaran
                financeDao.insertPayment(payment.toEntity())

                // 2. Perbarui status tagihan secara atomik
                val newTotalPaid = totalAlreadyPaid + payment.amount
                val newStatus = if (newTotalPaid >= targetCharge.amount) {
                    ChargeStatus.PAID.name
                } else {
                    ChargeStatus.PARTIALLY_PAID.name
                }

                financeDao.updateCharge(targetCharge.copy(status = newStatus))
            }
        }
    }

    override fun getRemainingPackageSessions(enrollmentId: String): Flow<Int> {
        return financeDao.getRemainingSessions(enrollmentId)
    }

    override suspend fun generateMonthlyCharges(targetMonth: String): Result<Int> {
        return runCatching {
            runInTransaction {
                val allClasses = classDao.getClassesByStatus("ACTIVE").firstOrNull() ?: emptyList()
                var generatedCount = 0
                val now = System.currentTimeMillis()

                for (cls in allClasses) {
                    val enrollments = classDao.getActiveEnrollmentsForClass(cls.id).firstOrNull() ?: emptyList()

                    for (enrollment in enrollments) {
                        if (enrollment.billingType == BillingType.MONTHLY.name && enrollment.feeAmount > 0L) {
                            val existingCharges = financeDao.getChargesForEnrollment(enrollment.id).firstOrNull() ?: emptyList()
                            val alreadyBilled = existingCharges.any {
                                it.chargeType == ChargeType.MONTHLY_FEE.name &&
                                    it.periodStart?.startsWith(targetMonth) == true
                            }

                            if (!alreadyBilled) {
                                val dueDayFormatted = enrollment.dueDay?.let { day ->
                                    val safeDay = day.coerceIn(1, 28)
                                    "$targetMonth-${String.format(Locale.US, "%02d", safeDay)}"
                                }

                                val charge = ChargeEntity(
                                    id = UUID.randomUUID().toString(),
                                    enrollmentId = enrollment.id,
                                    chargeType = ChargeType.MONTHLY_FEE.name,
                                    periodStart = "$targetMonth-01",
                                    amount = enrollment.feeAmount,
                                    dueDate = dueDayFormatted,
                                    status = ChargeStatus.UNPAID.name,
                                    createdAt = now
                                )
                                financeDao.insertCharge(charge)
                                generatedCount++
                            }
                        }
                    }
                }
                generatedCount
            }
        }
    }

    override fun countLowBalanceEnrollments(threshold: Int): Flow<Int> {
        return financeDao.countLowBalanceEnrollments(threshold)
    }

    override suspend fun getOutstandingForStudent(studentId: String): Long {
        return financeDao.getOutstandingForStudent(studentId)
    }

    override suspend fun getOutstandingForEnrollment(enrollmentId: String): Long {
        return financeDao.getOutstandingForEnrollment(enrollmentId)
    }
}
