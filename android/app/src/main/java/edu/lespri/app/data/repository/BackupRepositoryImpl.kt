package edu.lespri.app.data.repository

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.AttendanceDao
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.ScheduleDao
import edu.lespri.app.data.local.dao.SettingDao
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
import edu.lespri.app.data.local.entity.ScheduleRuleEntity
import edu.lespri.app.data.local.entity.StudentEntity
import edu.lespri.app.data.local.entity.TutorEntity
import edu.lespri.app.domain.model.ImportSummary
import edu.lespri.app.domain.repository.BackupRepository
import edu.lespri.app.util.BackupManager
import edu.lespri.app.util.BackupPayload
import edu.lespri.app.util.CsvEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
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

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    override suspend fun exportJsonBackup(): Result<String> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val owner = settingDao.getOwnerProfile().firstOrNull()
                val students = studentDao.getStudentsByStatus("ACTIVE").firstOrNull().orEmpty()
                val tutors = tutorDao.getTutorsByStatus("ACTIVE").firstOrNull().orEmpty()
                val classes = classDao.getClassesByStatus("ACTIVE").firstOrNull().orEmpty()

                val dataMap = mutableMapOf<String, List<Map<String, String>>>()

                if (owner != null) {
                    dataMap["owner_profile"] = listOf(
                        mapOf(
                            "id" to owner.id,
                            "business_name" to owner.businessName,
                            "created_at" to owner.createdAt.toString(),
                            "updated_at" to owner.updatedAt.toString()
                        )
                    )
                }

                dataMap["students"] = students.map { s ->
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

                dataMap["tutors"] = tutors.map { t ->
                    mapOf(
                        "id" to t.id,
                        "name" to t.name,
                        "phone" to t.phone.orEmpty(),
                        "expertise" to t.expertise.orEmpty(),
                        "notes" to t.notes.orEmpty(),
                        "status" to t.status,
                        "created_at" to t.createdAt.toString()
                    )
                }

                dataMap["class_groups"] = classes.map { c ->
                    mapOf(
                        "id" to c.id,
                        "name" to c.name,
                        "class_type" to c.classType,
                        "subject" to c.subject.orEmpty(),
                        "tutor_id" to c.tutorId.orEmpty(),
                        "location_note" to c.locationNote.orEmpty(),
                        "status" to c.status,
                        "created_at" to c.createdAt.toString()
                    )
                }

                val payload = BackupPayload(
                    version = 1,
                    exportedAt = System.currentTimeMillis(),
                    data = dataMap
                )

                json.encodeToString(payload)
            }
        }
    }

    override suspend fun restoreJsonBackup(jsonPayload: String): Result<Unit> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val payload = json.decodeFromString<BackupPayload>(jsonPayload)
                database.withTransaction {
                    payload.data["students"]?.forEach { m ->
                        val entity = StudentEntity(
                            id = m["id"] ?: UUID.randomUUID().toString(),
                            name = m["name"].orEmpty(),
                            guardianName = m["guardian_name"]?.takeIf { it.isNotBlank() },
                            guardianPhone = m["guardian_phone"]?.takeIf { it.isNotBlank() },
                            schoolGrade = m["school_grade"]?.takeIf { it.isNotBlank() },
                            notes = m["notes"]?.takeIf { it.isNotBlank() },
                            status = m["status"] ?: "ACTIVE",
                            joinedAt = m["joined_at"]?.toLongOrNull() ?: System.currentTimeMillis()
                        )
                        studentDao.upsertStudent(entity)
                    }

                    payload.data["tutors"]?.forEach { m ->
                        val entity = TutorEntity(
                            id = m["id"] ?: UUID.randomUUID().toString(),
                            name = m["name"].orEmpty(),
                            phone = m["phone"]?.takeIf { it.isNotBlank() },
                            expertise = m["expertise"]?.takeIf { it.isNotBlank() },
                            notes = m["notes"]?.takeIf { it.isNotBlank() },
                            status = m["status"] ?: "ACTIVE",
                            createdAt = m["created_at"]?.toLongOrNull() ?: System.currentTimeMillis()
                        )
                        tutorDao.upsertTutor(entity)
                    }

                    payload.data["class_groups"]?.forEach { m ->
                        val entity = ClassGroupEntity(
                            id = m["id"] ?: UUID.randomUUID().toString(),
                            name = m["name"].orEmpty(),
                            classType = m["class_type"] ?: "GROUP",
                            subject = m["subject"]?.takeIf { it.isNotBlank() },
                            tutorId = m["tutor_id"]?.takeIf { it.isNotBlank() },
                            locationNote = m["location_note"]?.takeIf { it.isNotBlank() },
                            status = m["status"] ?: "ACTIVE",
                            createdAt = m["created_at"]?.toLongOrNull() ?: System.currentTimeMillis()
                        )
                        classDao.upsertClass(entity)
                    }
                }
            }
        }
    }

    override suspend fun exportCsvDataset(datasetName: String): Result<String> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val headers = listOf("id", "name", "guardian_name", "guardian_phone", "school_grade", "notes", "status", "joined_at")
                val rows = when (datasetName.lowercase()) {
                    "students", "siswa" -> {
                        val list = studentDao.getStudentsByStatus("ACTIVE").firstOrNull().orEmpty()
                        list.map { s ->
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
                val dir = backupManager.backupDir
                val targetFile = File(dir, fileName)
                targetFile.writeText(csvText, Charsets.UTF_8)
                fileName
            }
        }
    }

    override suspend fun importCsvData(rawCsv: String, columnMapping: Map<String, String>): Result<ImportSummary> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val parseResult = CsvEngine.parse(rawCsv).getOrThrow()
                val rows = parseResult.rows

                val nameKey = columnMapping.entries.firstOrNull { it.value == "name" }?.key
                    ?: throw IllegalArgumentException("Kolom 'name' (nama siswa) harus dipetakan.")

                val students = mutableListOf<StudentEntity>()
                val errors = mutableListOf<String>()

                for ((index, row) in rows.withIndex()) {
                    val name = row[nameKey]?.trim()
                    if (name.isNullOrBlank()) {
                        errors += "Baris ${index + 2}: nama siswa kosong, dilewati."
                        continue
                    }
                    val id = row[columnMapping.entries.firstOrNull { it.value == "id" }?.key]?.trim()
                        ?: UUID.randomUUID().toString()

                    students += StudentEntity(
                        id = id,
                        name = name,
                        guardianName = row[columnMapping.entries.firstOrNull { it.value == "guardian_name" || it.value == "guardianName" }?.key]?.takeIf { it.isNotBlank() },
                        guardianPhone = row[columnMapping.entries.firstOrNull { it.value == "guardian_phone" || it.value == "guardianPhone" }?.key]?.takeIf { it.isNotBlank() },
                        schoolGrade = row[columnMapping.entries.firstOrNull { it.value == "school_grade" || it.value == "schoolGrade" }?.key]?.takeIf { it.isNotBlank() },
                        notes = row[columnMapping.entries.firstOrNull { it.value == "notes" }?.key]?.takeIf { it.isNotBlank() },
                        status = "ACTIVE",
                        joinedAt = System.currentTimeMillis()
                    )
                }

                if (students.isNotEmpty()) {
                    studentDao.insertStudentsBatch(students)
                }

                ImportSummary(
                    totalRows = rows.size,
                    importedCount = students.size,
                    failedCount = errors.size,
                    errors = errors
                )
            }
        }
    }
}
