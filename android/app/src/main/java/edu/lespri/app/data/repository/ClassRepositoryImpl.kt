package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.ScheduleDao
import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.dao.TutorDao
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeType
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassGroupDetail
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.EnrolledStudentItem
import edu.lespri.app.domain.model.Enrollment
import edu.lespri.app.domain.model.EnrollmentStatus
import edu.lespri.app.domain.model.LedgerEntryType
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.repository.ClassRepository
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
 * Implementasi repositori kelas dengan penjaminan invarian pendaftaran dan soft-delete cascade.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ClassRepositoryImpl @Inject constructor(
    private val database: LespriDatabase,
    private val classDao: ClassDao,
    private val tutorDao: TutorDao,
    private val studentDao: StudentDao,
    private val scheduleDao: ScheduleDao,
    private val financeDao: FinanceDao
) : ClassRepository {

    override fun getClasses(): Flow<List<ClassGroup>> = getClassesByStatus("ACTIVE")

    override fun getClassesByStatus(status: String): Flow<List<ClassGroup>> {
        return classDao.getClassesByStatus(status).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getClassDetail(id: String): Flow<ClassGroupDetail?> {
        return classDao.getClassById(id).flatMapLatest { classEntity ->
            if (classEntity == null) {
                flowOf(null)
            } else {
                val classGroup = classEntity.toDomain()
                val today = getTodayIsoDate()
                val thirtyDaysLater = getFutureIsoDate(30)

                combine(
                    tutorDao.getTutorById(classEntity.tutorId ?: ""),
                    classDao.getActiveEnrollmentsForClass(id),
                    scheduleDao.getSessionsForClassInRange(id, today, thirtyDaysLater)
                ) { tutorEntity, enrollments, sessions ->
                    val assignedTutor = tutorEntity?.toDomain()
                    val upcoming = sessions.map { it.toDomain() }

                    val studentItems = enrollments.map { enrollmentEntity ->
                        val student = studentDao.getStudentById(enrollmentEntity.studentId).firstOrNull()?.toDomain()
                        val remaining = if (enrollmentEntity.billingType == BillingType.PACKAGE.name) {
                            financeDao.getRemainingSessionsSync(enrollmentEntity.id)
                        } else null

                        // Hitung sisa tagihan yang belum lunas
                        val charges = financeDao.getChargesForEnrollment(enrollmentEntity.id).firstOrNull() ?: emptyList()
                        var debt = 0L
                        for (c in charges) {
                            if (c.status != ChargeStatus.CANCELLED.name && c.status != ChargeStatus.PAID.name) {
                                val paid = financeDao.getTotalPaidForCharge(c.id)
                                debt += (c.amount - paid).coerceAtLeast(0L)
                            }
                        }

                        EnrolledStudentItem(
                            enrollmentId = enrollmentEntity.id,
                            studentId = enrollmentEntity.studentId,
                            studentName = student?.name ?: "Siswa",
                            billingType = runCatching { BillingType.valueOf(enrollmentEntity.billingType) }.getOrDefault(BillingType.MONTHLY),
                            feeAmount = enrollmentEntity.feeAmount,
                            remainingPackageSessions = remaining,
                            currentDebt = debt
                        )
                    }

                    ClassGroupDetail(
                        classGroup = classGroup,
                        assignedTutor = assignedTutor,
                        enrolledStudents = studentItems,
                        upcomingSessions = upcoming
                    )
                }
            }
        }
    }

    override suspend fun saveClass(classGroup: ClassGroup): Result<Unit> {
        val trimmedName = classGroup.name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Nama kelas wajib diisi"))
        }
        return runCatching {
            database.withTransaction {
                // Invarian: kelas privat hanya boleh memiliki maksimal satu siswa aktif.
                if (classGroup.classType == ClassType.PRIVATE) {
                    val activeEnrollments = classDao.countActiveEnrollmentsForClass(classGroup.id)
                    if (activeEnrollments > 1) {
                        throw IllegalStateException("Kelas privat hanya boleh memiliki satu siswa aktif")
                    }
                }

                classDao.upsertClass(
                    classGroup.copy(
                        name = trimmedName,
                        subject = classGroup.subject?.trim()?.takeIf { it.isNotEmpty() },
                        tutorId = classGroup.tutorId?.takeIf { it.isNotBlank() },
                        locationNote = classGroup.locationNote?.trim()?.takeIf { it.isNotEmpty() }
                    ).toEntity()
                )
            }
        }
    }

    override suspend fun archiveClass(id: String): Result<Unit> {
        return runCatching {
            classDao.archiveClass(id)
        }
    }

    override suspend fun enrollStudent(enrollment: Enrollment): Result<Unit> {
        if (enrollment.feeAmount < 0) {
            return Result.failure(IllegalArgumentException("Nominal biaya tidak boleh negatif"))
        }

        when (enrollment.billingType) {
            BillingType.MONTHLY -> {
                val dueDay = enrollment.dueDay
                if (dueDay != null && dueDay !in 1..28) {
                    return Result.failure(IllegalArgumentException("Tanggal jatuh tempo harus 1–28"))
                }
            }
            BillingType.PACKAGE -> {
                val sessions = enrollment.packageSessionCount
                    ?: return Result.failure(IllegalArgumentException("Jumlah sesi paket wajib diisi"))
                if (sessions !in LespriConstants.MIN_SESSIONS_PER_PACKAGE..LespriConstants.MAX_SESSIONS_PER_PACKAGE) {
                    return Result.failure(IllegalArgumentException("Jumlah sesi paket harus 1–100"))
                }
            }
            BillingType.PER_SESSION -> Unit
        }

        return runCatching {
            database.withTransaction {
                val classEntity = classDao.getClassById(enrollment.classId).firstOrNull()
                    ?: throw IllegalStateException("Kelas tidak ditemukan")
                if (classEntity.status != "ACTIVE") {
                    throw IllegalStateException("Kelas sudah tidak aktif")
                }

                val studentEntity = studentDao.getStudentById(enrollment.studentId).firstOrNull()
                    ?: throw IllegalStateException("Siswa tidak ditemukan")
                if (studentEntity.status != "ACTIVE") {
                    throw IllegalStateException("Siswa sudah tidak aktif")
                }

                if (classDao.countActiveEnrollmentForStudentAndClass(
                        enrollment.studentId,
                        enrollment.classId
                    ) > 0
                ) {
                    throw IllegalStateException("Siswa sudah terdaftar di kelas ini")
                }

                if (classEntity.classType == ClassType.PRIVATE.name &&
                    classDao.countActiveEnrollmentsForClass(enrollment.classId) >= 1
                ) {
                    throw IllegalStateException("Kelas privat hanya boleh memiliki satu siswa aktif")
                }

                val normalized = when (enrollment.billingType) {
                    BillingType.MONTHLY -> enrollment.copy(packageSessionCount = null)
                    BillingType.PACKAGE -> enrollment.copy(dueDay = null)
                    BillingType.PER_SESSION -> enrollment.copy(dueDay = null, packageSessionCount = null)
                }
                classDao.insertEnrollment(normalized.toEntity())

                // Invarian: Paket sesi meng-insert PackageLedger dan tagihan awal
                if (normalized.billingType == BillingType.PACKAGE &&
                    normalized.packageSessionCount != null &&
                    normalized.packageSessionCount > 0
                ) {
                    val ledgerEntry = PackageLedgerEntity(
                        id = UUID.randomUUID().toString(),
                        enrollmentId = normalized.id,
                        sessionId = null,
                        deltaSessions = normalized.packageSessionCount,
                        reason = LedgerEntryType.PACKAGE_PURCHASE.name,
                        createdAt = System.currentTimeMillis()
                    )
                    financeDao.insertLedgerEntry(ledgerEntry)

                    if (normalized.feeAmount > 0) {
                        val charge = ChargeEntity(
                            id = UUID.randomUUID().toString(),
                            enrollmentId = normalized.id,
                            chargeType = ChargeType.SESSION_PACKAGE.name,
                            amount = normalized.feeAmount,
                            status = ChargeStatus.UNPAID.name,
                            createdAt = System.currentTimeMillis()
                        )
                        financeDao.insertCharge(charge)
                    }
                } else if (normalized.billingType == BillingType.MONTHLY && normalized.feeAmount > 0) {
                    val currentMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
                    val charge = ChargeEntity(
                        id = UUID.randomUUID().toString(),
                        enrollmentId = normalized.id,
                        chargeType = ChargeType.MONTHLY_FEE.name,
                        periodStart = "$currentMonth-01",
                        amount = normalized.feeAmount,
                        status = ChargeStatus.UNPAID.name,
                        createdAt = System.currentTimeMillis()
                    )
                    financeDao.insertCharge(charge)
                }
            }
        }
    }

    override suspend fun removeEnrollment(enrollmentId: String): Result<Unit> {
        return runCatching {
            classDao.updateEnrollmentStatus(enrollmentId, EnrollmentStatus.WITHDRAWN.name)
        }
    }

    private fun getTodayIsoDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun getFutureIsoDate(daysAhead: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAhead)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
}
