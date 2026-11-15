package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.AttendanceDao
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.ScheduleDao
import edu.lespri.app.data.local.dao.SettingDao
    override suspend fun exportJsonBackup(): Result<String> {
        return runCatching {
            val payload = BackupPayload(
                data = mapOf(
                    "owner_profile" to listOfNotNull(database.ownerProfileSync()).map { it.toBackupMap() },
                    "students" to studentDao.getAllStudentsForBackup().map { it.toBackupMap() },
                    "tutors" to tutorDao.getAllTutorsForBackup().map { it.toBackupMap() },
                    "class_groups" to classDao.getAllClassesForBackup().map { it.toBackupMap() },
                    "enrollments" to classDao.getAllEnrollmentsForBackup().map { it.toBackupMap() },
                    "schedule_rules" to scheduleDao.getAllScheduleRulesForBackup().map { it.toBackupMap() },
                    "lesson_sessions" to scheduleDao.getAllSessionsForBackup().map { it.toBackupMap() },
                    "attendance_records" to attendanceDao.getAllAttendanceForBackup().map { it.toBackupMap() },
                    "charges" to financeDao.getAllChargesForBackup().map { it.toBackupMap() },
                    "payments" to financeDao.getAllPaymentsForBackup().map { it.toBackupMap() },
                    "package_ledgers" to financeDao.getAllLedgerEntriesForBackup().map { it.toBackupMap() },
                    "notification_preferences" to emptyList(),
                    "app_settings" to settingDao.getAllSettingsForBackup().map { it.toBackupMap() }
                )
            )
            json.encodeToString(payload)
        }
    }

import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.dao.TutorDao
import edu.lespri.app.data.local.entity.AppSettingEntity
import edu.lespri.app.data.local.entity.AttendanceRecordEntity
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.local.entity.ClassGroupEntity
import edu.lespri.app.data.local.entity.EnrollmentEntity
import edu.lespri.app.data.local.entity.LessonSessionEntity
import edu.lespri.app.data.local.entity.OwnerProfileEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.local.entity.PaymentEntity
    override suspend fun exportCsvDataset(datasetName: String): Result<String> {
        return runCatching {
            val headers = listOf("id", "name", "guardian_name", "guardian_phone", "school_grade", "notes", "status", "joined_at")
            val rows = when (datasetName.lowercase()) {
                "students", "siswa" -> {
                    studentDao.getAllStudentsForBackup().map { s ->
                        mapOf(
                            "id" to s.id,
                            "name" to s.name,
                            "guardian_name" to s.guardianName.orEmpty(),
                            "guardian_phone" to s.guardianPhone.orEmpty(),
                            "school_grade" to s.schoolGrade.orEmpty(),
                            "notes" to s.notes.orEmpty(),
                            "status" to s.status,
                            "joined_at" to s.joinedAt.toString()
                        )
                    }
                }
                else -> throw IllegalArgumentException("Dataset '$datasetName' tidak didukung untuk ekspor CSV.")
            }
            val csvText = CsvEngine.generate(headers, rows)
            val fileName = "lespri_${datasetName}_export.csv"
            backupManager.exportToFile(mapOf("content" to csvText), fileName)
            fileName
        }
    }

    override suspend fun importCsvData(rawCsv: String, columnMapping: Map<String, String>): Result<ImportSummary> {
        return runCatching {
            val parsed = CsvEngine.parse(rawCsv) ?: throw IllegalArgumentException("CSV kosong atau tidak valid.")
            val rows = parsed.rows

            val nameKey = columnMapping.entries.firstOrNull { it.value == "name" }?.key
                ?: throw IllegalArgumentException("Kolom 'name' (nama siswa) harus dipetakan pada impor CSV.")

            val students = mutableListOf<StudentEntity>()
            val errors = mutableListOf<String>()

            for ((index, row) in rows.withIndex()) {
                val name = row[nameKey]?.trim()
                if (name.isNullOrBlank()) {
                    errors += "Baris ${index + 2}: nama siswa kosong, dilewati."
                    continue
                }
                val id = row[columnMapping.entries.firstOrNull { it.value == "id" }?.key]?.trim()
                    ?: java.util.UUID.randomUUID().toString()

                students += StudentEntity(
                    id = id,
                    name = name,
                    guardianName = row[columnMapping.entries.firstOrNull { it.value == "guardianName" }?.key]?.takeIf { it.isNotBlank() },
                    guardianPhone = row[columnMapping.entries.firstOrNull { it.value == "guardianPhone" }?.key]?.takeIf { it.isNotBlank() },
                    schoolGrade = row[columnMapping.entries.firstOrNull { it.value == "schoolGrade" }?.key]?.takeIf { it.isNotBlank() },
                    notes = row[columnMapping.entries.firstOrNull { it.value == "notes" }?.key]?.takeIf { it.isNotBlank() },
                    status = "ACTIVE",
                    joinedAt = System.currentTimeMillis()
                )
            }

            if (students.isNotEmpty()) {
                studentDao.insertStudentsBatch(students)
            }

            ImportSummary(
    // ========================================================================
    // Helper: konversi Map → Entity (untuk restore JSON)
    // ========================================================================

    private fun Map<String, Any?>.toBackupMap(): Map<String, Any?> = this

    private fun mapToStudentEntity(m: Map<String, Any?>): StudentEntity =
        StudentEntity(
            id = m["id"] as String,
            name = m["name"] as String,
            guardianName = m["guardianName"] as? String,
            guardianPhone = m["guardianPhone"] as? String,
            schoolGrade = m["schoolGrade"] as? String,
            notes = m["notes"] as? String,
            status = m["status"] as? String ?: "ACTIVE",
    private fun mapToClassEntity(m: Map<String, Any?>): ClassGroupEntity =
        ClassGroupEntity(
            id = m["id"] as String,
            name = m["name"] as String,
            classType = m["classType"] as? String ?: "GROUP",
            tutorId = m["tutorId"] as? String,
            billingType = m["billingType"] as? String ?: "MONTHLY",
            sessionPrice = (m["sessionPrice"] as? Number)?.toLong() ?: 0L,
            packageSessions = (m["packageSessions"] as? Number)?.toInt() ?: 0,
            status = m["status"] as? String ?: "ACTIVE",
            createdAt = (m["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )

    private fun mapToEnrollmentEntity(m: Map<String, Any?>): EnrollmentEntity =
        EnrollmentEntity(
            id = m["id"] as String,
            studentId = m["studentId"] as String,
            classId = m["classId"] as String,
            status = m["status"] as? String ?: "ACTIVE",
            billingType = m["billingType"] as? String ?: "MONTHLY",
            totalSessions = (m["totalSessions"] as? Number)?.toInt() ?: 0,
    private fun mapToScheduleRuleEntity(m: Map<String, Any?>): ScheduleRuleEntity =
        ScheduleRuleEntity(
            id = m["id"] as String,
            classId = m["classId"] as String,
            dayOfWeek = m["dayOfWeek"] as? String ?: "SENIN",
            startTime = m["startTime"] as? String ?: "08:00",
            durationMinutes = (m["durationMinutes"] as? Number)?.toInt() ?: 60,
            tutorId = m["tutorId"] as? String,
            status = m["status"] as? String ?: "ACTIVE",
            createdAt = (m["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )

    private fun mapToLessonSessionEntity(m: Map<String, Any?>): LessonSessionEntity =
        LessonSessionEntity(
            id = m["id"] as String,
            classId = m["classId"] as String,
            sessionDate = m["sessionDate"] as? String ?: "",
            startTime = m["startTime"] as? String ?: "08:00",
            endTime = m["endTime"] as? String ?: "09:00",
            tutorId = m["tutorId"] as? String,
            status = m["status"] as? String ?: "SCHEDULED",
            notes = m["notes"] as? String,
            createdAt = (m["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    private fun mapToChargeEntity(m: Map<String, Any?>): ChargeEntity =
        ChargeEntity(
            id = m["id"] as String,
            enrollmentId = m["enrollmentId"] as String,
            amount = (m["amount"] as? Number)?.toLong() ?: 0L,
            dueDate = m["dueDate"] as? String,
            status = m["status"] as? String ?: "UNPAID",
            description = m["description"] as? String,
            createdAt = (m["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )

    private fun mapToPaymentEntity(m: Map<String, Any?>): PaymentEntity =
        PaymentEntity(
            id = m["id"] as String,
            chargeId = m["chargeId"] as String,
            amount = (m["amount"] as? Number)?.toLong() ?: 0L,
            paidAt = (m["paidAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            method = m["method"] as? String ?: "CASH",
            referenceNote = m["referenceNote"] as? String
        )

    private fun mapToLedgerEntryEntity(m: Map<String, Any?>): PackageLedgerEntity =
        PackageLedgerEntity(
            id = m["id"] as String,
            enrollmentId = m["enrollmentId"] as String,
            deltaSessions = (m["deltaSessions"] as? Number)?.toInt() ?: 0,
            transactionType = m["transactionType"] as? String ?: "ADJUSTMENT",
            description = m["description"] as? String,
            createdAt = (m["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )

    private suspend fun LespriDatabase.ownerProfileSync(): OwnerProfileEntity? =
        withTransaction { settingDao.getOwnerProfileSync() }

    private fun mapToAttendanceRecordEntity(m: Map<String, Any?>): AttendanceRecordEntity =
        AttendanceRecordEntity(
            id = m["id"] as String,
            sessionId = m["sessionId"] as String,
            enrollmentId = m["enrollmentId"] as String,
            status = m["status"] as? String ?: "UNRECORDED",
            updatedAt = (m["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
            remainingSessions = (m["remainingSessions"] as? Number)?.toInt() ?: 0,
            joinedAt = (m["joinedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
            joinedAt = (m["joinedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )

    private fun mapToTutorEntity(m: Map<String, Any?>): TutorEntity =
        TutorEntity(
            id = m["id"] as String,
            name = m["name"] as String,
            phone = m["phone"] as? String,
            email = m["email"] as? String,
            subjects = m["subjects"] as? String,
            status = m["status"] as? String ?: "ACTIVE",
            joinedAt = (m["joinedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
                totalRows = rows.size,
                importedCount = students.size,
                failedCount = errors.size,
                errors = errors
            )
        }
    }

    override suspend fun restoreJsonBackup(jsonPayload: String): Result<Unit> {
        return runCatching {
            val payload: BackupPayload = json.decodeFromString(jsonPayload)
            database.withTransaction {
                // App settings di-replace (key unik)
                payload.data["app_settings"]?.forEach { m ->
                    settingDao.setSetting(AppSettingEntity(m["settingKey"] as String, m["settingValue"] as String))
                }
                // Entitas utama menggunakan REPLACE/IGNORE — tidak perlu delete sebelumnya
                payload.data["students"]?.forEach { m ->
                    studentDao.upsertStudent(mapToStudentEntity(m))
                }
                payload.data["tutors"]?.forEach { m ->
                    tutorDao.upsertTutor(mapToTutorEntity(m))
                }
                payload.data["class_groups"]?.forEach { m ->
                    classDao.upsertClass(mapToClassEntity(m))
                }
                payload.data["enrollments"]?.forEach { m ->
                    classDao.insertEnrollment(mapToEnrollmentEntity(m))
                }
                payload.data["schedule_rules"]?.forEach { m ->
                    scheduleDao.upsertScheduleRule(mapToScheduleRuleEntity(m))
                }
                payload.data["lesson_sessions"]?.forEach { m ->
                    scheduleDao.insertSessions(listOf(mapToLessonSessionEntity(m)))
                }
                payload.data["attendance_records"]?.forEach { m ->
                    attendanceDao.upsertAttendanceBatch(listOf(mapToAttendanceRecordEntity(m)))
                }
                payload.data["charges"]?.forEach { m ->
                    financeDao.insertCharge(mapToChargeEntity(m))
                }
                payload.data["payments"]?.forEach { m ->
                    financeDao.insertPayment(mapToPaymentEntity(m))
                }
                payload.data["package_ledgers"]?.forEach { m ->
                    financeDao.insertLedgerEntry(mapToLedgerEntryEntity(m))
                }
            }
        }
    }

import edu.lespri.app.data.local.entity.ScheduleRuleEntity
import edu.lespri.app.data.local.entity.StudentEntity
import edu.lespri.app.data.local.entity.TutorEntity
import edu.lespri.app.domain.model.ImportSummary
import edu.lespri.app.domain.repository.BackupRepository
import edu.lespri.app.util.BackupManager
import edu.lespri.app.util.BackupPayload
import edu.lespri.app.util.CsvEngine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val database: LespriDatabase,
    private val studentDao: StudentDao,
    private val tutorDao: TutorDao,
    private val classDao: ClassDao,
    private val scheduleDao: ScheduleDao,
    private val attendanceDao: AttendanceDao,
    private val financeDao: FinanceDao,
    private val settingDao: SettingDao,
    private val backupManager: BackupManager
) : BackupRepository {

    private val json = Json { ignoreUnknownKeys = true }