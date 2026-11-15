package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.AttendanceDao
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.ScheduleDao
import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.dao.TutorDao
import edu.lespri.app.data.local.entity.LessonSessionEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.LedgerEntryType
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.LessonSessionDetail
import edu.lespri.app.domain.model.ScheduleRule
import edu.lespri.app.domain.model.SessionStatus
import edu.lespri.app.domain.model.StudentAttendanceItem
import edu.lespri.app.domain.repository.ScheduleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori jadwal dengan mesin generator sesi berulang otomatis
 * dan penanganan pembatalan sesi cascade.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val database: LespriDatabase,
    private val scheduleDao: ScheduleDao,
    private val classDao: ClassDao,
    private val tutorDao: TutorDao,
    private val attendanceDao: AttendanceDao,
    private val studentDao: StudentDao,
    private val financeDao: FinanceDao
) : ScheduleRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun getAgendaSessions(startDate: String, endDate: String): Flow<List<LessonSession>> {
        return scheduleDao.getSessionsInRange(startDate, endDate).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getSessionDetail(sessionId: String): Flow<LessonSessionDetail?> {
        return scheduleDao.getSessionById(sessionId).flatMapLatest { sessionEntity ->
            if (sessionEntity == null) {
                flowOf(null)
            } else {
                val session = sessionEntity.toDomain()
                combine(
                    classDao.getClassById(session.classId),
                    tutorDao.getTutorById(session.tutorId ?: ""),
                    attendanceDao.getAttendanceForSession(sessionId),
                    classDao.getActiveEnrollmentsForClass(session.classId)
                ) { classEntity, tutorEntity, attendanceRecords, activeEnrollments ->
                    val className = classEntity?.name ?: "Kelas"
                    val tutorName = tutorEntity?.name

                    // Petakan setiap siswa terdaftar dengan status presensinya
                    val attendanceMap = attendanceRecords.associateBy { it.enrollmentId }

                    val studentItems = activeEnrollments.map { enrollment ->
                        val student = studentDao.getStudentById(enrollment.studentId).firstOrNull()?.toDomain()
                        val record = attendanceMap[enrollment.id]

                        val parsedStatus = record?.let {
                            runCatching { AttendanceStatus.valueOf(it.status) }.getOrDefault(AttendanceStatus.UNRECORDED)
                        } ?: AttendanceStatus.UNRECORDED

                        val remainingSessions = if (enrollment.billingType == BillingType.PACKAGE.name) {
                            financeDao.getRemainingSessionsSync(enrollment.id)
                        } else null

                        StudentAttendanceItem(
                            enrollmentId = enrollment.id,
                            studentId = enrollment.studentId,
                            studentName = student?.name ?: "Siswa",
                            status = parsedStatus,
                            note = record?.note,
                            billingType = runCatching { BillingType.valueOf(enrollment.billingType) }.getOrDefault(BillingType.MONTHLY),
                            remainingPackageSessions = remainingSessions
                        )
                    }

                    LessonSessionDetail(
                        session = session,
                        className = className,
                        tutorName = tutorName,
                        attendanceRecords = studentItems
                    )
                }
            }
        }
    }

    override suspend fun createAdHocSession(session: LessonSession): Result<Unit> {
        return runCatching {
            scheduleDao.insertAdHocSession(session.toEntity())
        }
    }

    override suspend fun updateSession(session: LessonSession): Result<Unit> {
        return runCatching {
            val existing = scheduleDao.getSessionByIdSync(session.id)
                ?: throw IllegalArgumentException("Sesi tidak ditemukan")
            if (existing.status == SessionStatus.CANCELLED.name) {
                throw IllegalStateException("Sesi yang sudah dibatalkan tidak dapat diubah")
            }
            scheduleDao.updateSession(session.toEntity())
        }
    }

    override suspend fun saveScheduleRule(rule: ScheduleRule): Result<Unit> {
        return runCatching {
            if (rule.weekday !in 1..7) {
                throw IllegalArgumentException("Hari pengulangan tidak valid")
            }
            scheduleDao.upsertScheduleRule(rule.toEntity())
        }
    }

    override suspend fun generateRecurringSessions(upToDate: String): Result<Int> {
        return runCatching {
            val rules = scheduleDao.getAllActiveRules()
            if (rules.isEmpty()) return@runCatching 0

            val today = dateFormat.format(Date())
            val candidateSessions = mutableListOf<LessonSessionEntity>()

            val upToCal = Calendar.getInstance().apply { time = dateFormat.parse(upToDate) ?: Date() }

            for (rule in rules) {
                val startCal = Calendar.getInstance().apply {
                    val effFrom = dateFormat.parse(rule.effectiveFrom) ?: Date()
                    val todayDate = dateFormat.parse(today) ?: Date()
                    time = if (effFrom.after(todayDate)) effFrom else todayDate
                }

                val endCal = Calendar.getInstance().apply {
                    val effUntil = rule.effectiveUntil?.let { dateFormat.parse(it) }
                    time = if (effUntil != null && effUntil.before(upToCal.time)) effUntil else upToCal.time
                }

                while (!startCal.after(endCal)) {
                    // Konversi Calendar DAY_OF_WEEK ke ISO (1=Monday ... 7=Sunday)
                    val calDow = startCal.get(Calendar.DAY_OF_WEEK)
                    val isoDow = if (calDow == Calendar.SUNDAY) 7 else calDow - 1

                    if (isoDow == rule.weekday) {
                        val sessionDate = dateFormat.format(startCal.time)
                        candidateSessions.add(
                            LessonSessionEntity(
                                id = UUID.randomUUID().toString(),
                                classId = rule.classId,
                                tutorId = rule.tutorId,
                                sessionDate = sessionDate,
                                startTime = rule.startTime,
                                durationMinutes = rule.durationMinutes,
                                status = SessionStatus.SCHEDULED.name,
                                sourceRuleId = rule.id
                            )
                        )
                    }
                    startCal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (candidateSessions.isEmpty()) return@runCatching 0

            val insertedRowIds = scheduleDao.insertSessions(candidateSessions)
            insertedRowIds.count { it > 0 }
        }
    }

    override suspend fun cancelSession(sessionId: String): Result<Unit> {
        return runCatching {
            database.withTransaction {
                // 1. Ambil seluruh record presensi untuk sesi ini
                val existingAttendance = attendanceDao.getAttendanceForSessionSync(sessionId)

                // 2. Cascade reversal: kembalikan saldo paket untuk siswa yang tadinya PRESENT
                for (record in existingAttendance) {
                    if (record.status == AttendanceStatus.PRESENT.name) {
                        val enrollment = classDao.getEnrollmentById(record.enrollmentId)
                        if (enrollment != null && enrollment.billingType == BillingType.PACKAGE.name) {
                            val reversalEntry = PackageLedgerEntity(
                                id = UUID.randomUUID().toString(),
                                enrollmentId = enrollment.id,
                                sessionId = sessionId,
                                deltaSessions = 1, // Reversal: +1 sesi
                                reason = LedgerEntryType.ATTENDANCE_REVERSAL.name,
                                createdAt = System.currentTimeMillis()
                            )
                            financeDao.insertLedgerEntry(reversalEntry)
                        }
                    }
                }

                // 3. Batalkan tagihan per sesi yang belum dibayar untuk sesi ini
                for (record in existingAttendance) {
                    val enrollment = classDao.getEnrollmentById(record.enrollmentId)
                    if (enrollment != null && enrollment.billingType == BillingType.PER_SESSION.name) {
                        val charges = financeDao.getChargesForEnrollmentSync(enrollment.id)
                        val chargeToCancel = charges.firstOrNull {
                            it.periodStart == sessionId && it.status == ChargeStatus.UNPAID.name
                        }
                        if (chargeToCancel != null) {
                            financeDao.updateCharge(chargeToCancel.copy(status = ChargeStatus.CANCELLED.name))
                        }
                    }
                }

                // 4. Batalkan sesi secara resmi
                scheduleDao.cancelSession(sessionId)
            }
        }
    }
}
