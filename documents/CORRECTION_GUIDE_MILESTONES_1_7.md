# PANDUAN KOREKSI DETAIL: LESPRI ANDROID (MILESTONE 1 - 7)

> **Tipe Dokumen:** Instruksi Perbaikan Komprehensif & Berbasis Langkah  
> **Target Pengguna:** AI Model Berkualitas Menengah ke Bawah (Medium / Small Context / Fast Model)  
> **Tujuan:** Memperbaiki seluruh error kompilasi, kesalahan sintaksis, pelanggaran arsitektur, dan fitur yang belum terhubung dari eksekusi Milestone 1 sampai 7 tanpa perlu menebak-nebak.  
> **Status Proyek Saat Ini:** ❌ Gagal Kompilasi (Syntax & Compilation Errors di Data & UI Layer, Navigasi Terputus).

---

## DAFTAR ISI
1. [Ringkasan Eksekutif & Status Audit](#1-ringkasan-eksekutif--status-audit)
2. [Instruksi Cara Eksekusi untuk AI Model](#2-instruksi-cara-eksekusi-untuk-ai-model)
3. [FASE 1: Perbaikan Error Kompilasi & Sintaksis Kritis (Wajib Selesai Pertama)](#3-fase-1-perbaikan-error-kompilasi--sintaksis-kritis)
   - [Task 1.1: Rewrite Total BackupRepositoryImpl.kt](#task-11-rewrite-total-backuprepositoryimplkt)
   - [Task 1.2: Perbaikan CsvEngine.kt](#task-12-perbaikan-csvenginekt)
   - [Task 1.3: Perbaikan BackupManager.kt](#task-13-perbaikan-backupmanagerkt)
   - [Task 1.4: Rewrite Total CsvExportScreen.kt](#task-14-rewrite-total-csvexportscreenkt)
   - [Task 1.5: Rewrite Total CsvImportScreen.kt](#task-15-rewrite-total-csvimportscreenkt)
   - [Task 1.6: Rewrite Total BackupRestoreScreen.kt](#task-16-rewrite-total-backuprestorescreenkt)
   - [Task 1.7: Perbaikan BannerAdView.kt](#task-17-perbaikan-banneradviewkt)
   - [Task 1.8: Perbaikan SettingsScreen.kt](#task-18-perbaikan-settingsscreenkt)
   - [Task 1.9: Perbaikan Parameter Catch Gaya Scala pada 4 File](#task-19-perbaikan-parameter-catch-gaya-scala-pada-4-file)
4. [FASE 2: Perbaikan Koneksi Navigasi & UI (Milestone 3, 5, 7)](#4-fase-2-perbaikan-koneksi-navigasi--ui)
   - [Task 2.1: Sambungkan Seluruh Layar Nyata di LespriNavHost.kt](#task-21-sambungkan-seluruh-layar-nyata-di-lesprinavhostkt)
   - [Task 2.2: Perbaikan Race Condition Navigasi Awal di MainActivity.kt](#task-22-perbaikan-race-condition-navigasi-awal-di-mainactivitykt)
   - [Task 2.3: Perbaikan Navigasi Sesi di DashboardScreen.kt](#task-23-perbaikan-navigasi-sesi-di-dashboardscreenkt)
5. [FASE 3: Perbaikan Logika Bisnis & Invarian Data (Milestone 5 & 6)](#5-fase-3-perbaikan-logika-bisnis--invarian-data)
   - [Task 3.1: Reversal Tagihan Per-Sesi di AttendanceRepositoryImpl.kt](#task-31-reversal-tagihan-per-sesi-di-attendancerepositoryimplkt)
   - [Task 3.2: Cascade Batal Tagihan Per-Sesi di ScheduleRepositoryImpl.kt](#task-32-cascade-batal-tagihan-per-sesi-di-schedulerepositoryimplkt)
   - [Task 3.3: Implementasi AlarmReceiver untuk Notifikasi Lokal Android 10+](#task-33-implementasi-alarmreceiver-untuk-notifikasi-lokal-android-10)
6. [FASE 4: Pembersihan Kemurnian Arsitektur Domain & ViewModel (Milestone 1 & 2)](#6-fase-4-pembersihan-kemurnian-arsitektur-domain--viewmodel)
   - [Task 4.1: Hilangkan Import Room dari GenerateMonthlyChargesUseCase.kt](#task-41-hilangkan-import-room-dari-generatemonthlychargesusecasekt)
   - [Task 4.2: Pemisahan Akses DAO Langsung dari ViewModel](#task-42-pemisahan-akses-dao-langsung-dari-viewmodel)
7. [Checklist Verifikasi Akhir](#7-checklist-verifikasi-akhir)

---

## 1. RINGKASAN EKSEKUTIF & STATUS AUDIT

Hasil audit menyeluruh terhadap kode sumber Milestone 1 sampai 7 menghasilkan temuan berikut:

| Kategori | Jumlah Masalah | Dampak |
|---|---|---|
| **CRITICAL (Error Kompilasi & File Corrupted)** | 9 file | Aplikasi **TIDAK BISA DIKOMPILASI** (`./gradlew compileDebugKotlin` gagal total). Kode acak-acakan di `BackupRepositoryImpl.kt`, `CsvExportScreen.kt`, `CsvImportScreen.kt`. |
| **CRITICAL (Runtime Crash)** | 2 file | Pemanggilan `registerForActivityResult` di dalam `remember` pada composable (`CsvImportScreen`, `BackupRestoreScreen`) pasti melempar `IllegalStateException` saat runtime. |
| **HIGH (Navigasi Terputus / Layar Kosong)** | 8 layar | `LespriNavHost.kt` masih menggunakan `PlaceholderScreen` untuk seluruh layar M5 (Jadwal, Sesi, Presensi) dan M7 (Impor CSV, Ekspor CSV, Backup, Pengaturan), sehingga layar yang sudah dibuat tidak bisa dibuka pengguna. |
| **HIGH (Pelanggaran Invarian Bisnis)** | 2 use case | Pembatalan presensi atau sesi `PER_SESSION` tidak membatalkan tagihan yang sudah terbit di draft. |
| **MEDIUM (Pelanggaran Clean Architecture)** | 9 ViewModel & UseCase | Domain layer mengimpor Room Database; beberapa ViewModel menginjeksi DAO Room secara langsung alih-alih melalui Repository Interface. |

---

## 2. INSTRUKSI CARA EKSEKUSI UNTUK AI MODEL

> [!IMPORTANT]
> **Petunjuk bagi AI Model yang memperbaiki proyek ini:**
> 1. Kerjakan secara berurutan mulai dari **FASE 1**, lalu **FASE 2**, **FASE 3**, dan **FASE 4**.
> 2. Jangan mengubah nama package, struktur folder, atau kontrak nama model di luar yang diinstruksikan.
> 3. Gunakan blok **KODE SEBELUM (TARGET)** dan **KODE SESUDAH (REPLACEMENT)** yang telah disediakan secara presisi.
> 4. Untuk file yang ditandai **REWRITE TOTAL**, gantikan seluruh isi file tersebut secara utuh dari baris 1 sampai akhir.

---

## 3. FASE 1: PERBAIKAN ERROR KOMPILASI & SINTAKSIS KRITIS

### Task 1.1: Rewrite Total `BackupRepositoryImpl.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/data/repository/BackupRepositoryImpl.kt`
- **Masalah:** File terpotong dan teracak saat digenerate. Definisi fungsi override berada di atas sebelum deklarasi class, import terpecah di 3 lokasi berbeda, dan syntax rusak di berbagai baris.
- **Tindakan:** Timpa seluruh isi file dengan kode bersih berikut:

```kotlin
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

                val file = backupManager.exportBackup(payload)
                file.name
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
```

---

### Task 1.2: Perbaikan `CsvEngine.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/util/CsvEngine.kt`
- **Masalah:** 
  1. `sealed class CsvError` tidak mewarisi `Throwable` atau `Exception()`, namun dilempar dengan kata kunci `throw`.
  2. `headers.indices.associateWith` menghasilkan `Map<Int, String>`, bukan `Map<String, String>`.
- **Tindakan:**
  1. Ubah deklarasi `sealed class CsvError`:
  ```kotlin
  // SEBELUM:
  sealed class CsvError {
      object EmptyInput : CsvError()
      data class MissingHeaders(val found: List<String>) : CsvError()
      data class RowLengthMismatch(val rowNumber: Int, val expected: Int, val actual: Int) : CsvError()
      data class ParseError(val message: String) : CsvError()
  }

  // SESUDAH:
  sealed class CsvError(message: String? = null) : Exception(message) {
      object EmptyInput : CsvError("Input CSV kosong")
      data class MissingHeaders(val found: List<String>) : CsvError("Header tidak ditemukan")
      data class RowLengthMismatch(val rowNumber: Int, val expected: Int, val actual: Int) : CsvError("Panjang baris tidak sesuai")
      data class ParseError(override val message: String) : CsvError(message)
  }
  ```
  2. Ubah pemetaan baris pada baris 63:
  ```kotlin
  // SEBELUM:
  val row = headers.indices.associateWith { idx ->
      fields.getOrNull(idx) ?: ""
  }

  // SESUDAH:
  val row = headers.indices.associate { idx ->
      headers[idx] to (fields.getOrNull(idx) ?: "")
  }
  ```

---

### Task 1.3: Perbaikan `BackupManager.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/util/BackupManager.kt`
- **Masalah:**
  1. `@constructor internal constructor` (sintaks Kotlin salah).
  2. `const val` ditaruh langsung di dalam class body (harus di dalam `companion object`).
  3. `context.externalFilesDir(null)` (harus `context.getExternalFilesDir(null)`).
  4. Kurang import `withContext` dan `Dispatchers`.
  5. `data class BackupPayload` belum dianotasi `@Serializable`.
- **Tindakan:**
  1. Tambahkan import di baris 12:
  ```kotlin
  import kotlinx.coroutines.Dispatchers
  import kotlinx.coroutines.withContext
  import kotlinx.serialization.Serializable
  ```
  2. Deklarasikan class `BackupManager`:
  ```kotlin
  // SEBELUM:
  class BackupManager @constructor internal constructor(
      private val context: Context
  ) {
      private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
      internal const val BACKUP_VERSION = 1
      internal val backupDir: File by lazy {
          val ext = context.externalFilesDir(null)
          val dir = if (ext != null) File(ext, "backups") else File(context.filesDir, "backups")
          dir.mkdirs()
          dir
      }

  // SESUDAH:
  class BackupManager(
      private val context: Context
  ) {
      private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

      companion object {
          const val BACKUP_VERSION = 1
          fun create(context: Context): BackupManager = BackupManager(context)
      }

      internal val backupDir: File by lazy {
          val ext = context.getExternalFilesDir(null)
          val dir = if (ext != null) File(ext, "backups") else File(context.filesDir, "backups")
          dir.mkdirs()
          dir
      }
  ```
  3. Tambahkan fungsi `exportBackup` dan perbaiki `BackupPayload`:
  ```kotlin
  // Gantikan exportToFile dan BackupPayload dengan:
  suspend fun exportBackup(payload: BackupPayload, fileName: String = backupFileName()): File {
      return withContext(Dispatchers.IO) {
          val target = File(backupDir, fileName)
          val jsonString = json.encodeToString(payload)
          val compressed = gzipCompress(jsonString.toByteArray(Charsets.UTF_8))
          target.outputStream().use { fos ->
              BufferedOutputStream(fos).use { bos ->
                  bos.write(compressed)
              }
          }
          target
      }
  }

  @Serializable
  data class BackupPayload(
      val version: Int = BackupManager.BACKUP_VERSION,
      val exportedAt: Long = System.currentTimeMillis(),
      val data: Map<String, List<Map<String, String>>>
  )
  ```

---

### Task 1.4: Rewrite Total `CsvExportScreen.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/csv/CsvExportScreen.kt`
- **Masalah:** Blok composable rusak, tagihan `items()` salah posisi, `FileDownload()` dipanggil sebagai method alih-alih `Icon`, import `Modifier`, `dp`, dan `collectAsState` hilang.
- **Tindakan:** Timpa seluruh isi file dengan kode bersih berikut:

```kotlin
package edu.lespri.app.ui.csv

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes

@Composable
fun CsvExportScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CsvExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LespriScaffold(
        currentRoute = NavRoutes.CsvExport.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Ekspor Data",
        subtitle = "Simpan data sebagai berkas CSV"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Pilih Dataset",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pilih data yang akan diekspor ke berkas CSV.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.availableDatasets) { dataset ->
                    val isSelected = uiState.selectedDataset == dataset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectDataset(dataset) }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.selectDataset(dataset) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = dataset.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            uiState.errorMessage?.let { error ->
                Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            uiState.successMessage?.let { msg ->
                Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }
            uiState.exportedFileName?.let { fileName ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Berkas tersimpan di folder aplikasi:", style = MaterialTheme.typography.labelSmall)
                            Text(text = fileName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onNavigateToRoute(NavRoutes.Dashboard.route) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Kembali")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.exportDataset() },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text(text = "Ekspor CSV")
                    }
                }
            }
        }
    }
}
```

---

### Task 1.5: Rewrite Total `CsvImportScreen.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/csv/CsvImportScreen.kt`
- **Masalah:** Fungsi composable ditaruh di dalam lambda Column, `activity.registerForActivityResult` dipanggil di dalam remember, `items()` ditaruh sebagai argumen konstruktor LazyColumn.
- **Tindakan:** Timpa seluruh isi file dengan kode bersih berikut:

```kotlin
package edu.lespri.app.ui.csv

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun CsvImportScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CsvImportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val content = stream.bufferedReader().readText()
                    viewModel.loadCsv(content)
                }
            }
        }
    }

    LespriScaffold(
        currentRoute = NavRoutes.CsvImport.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Impor CSV",
        subtitle = "Muat data siswa dari berkas CSV"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (uiState.csvResult == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pilih Berkas CSV",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pilih berkas .csv yang berisi data siswa. Baris pertama harus berupa header kolom.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { fileLauncher.launch(arrayOf("text/*", "text/comma-separated-values", "*/*")) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Buka Berkas")
                        }
                    }
                }
            } else {
                Text(
                    text = "Pratinjau Impor (${uiState.previewRows.size} baris)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(uiState.previewRows.take(10)) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.values.joinToString(" | "),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                uiState.errorMessage?.let { error ->
                    Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onNavigateToRoute(NavRoutes.Dashboard.route) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.confirmImport() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simpan Impor")
                    }
                }
            }
        }
    }
}
```

---

### Task 1.6: Rewrite Total `BackupRestoreScreen.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/backup/BackupRestoreScreen.kt`
- **Masalah:** `rememberFileLauncher` memanggil `registerForActivityResult` di dalam `remember`, crash saat runtime, icon `CloudUpload` hilang import.
- **Tindakan:** Timpa seluruh isi file dengan kode bersih berikut:

```kotlin
package edu.lespri.app.ui.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun BackupRestoreScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val tempFile = File(context.cacheDir, "lespri_restore_${System.currentTimeMillis()}.gz")
                    tempFile.outputStream().use { fos -> stream.copyTo(fos) }
                    viewModel.restoreFromFile(tempFile.absolutePath)
                }
            }
        }
    }

    LespriScaffold(
        currentRoute = NavRoutes.BackupRestore.route,
        onNavigateToRoute = onNavigateToRoute,
        title = "Backup & Restore",
        subtitle = "Cadangkan dan pulihkan data aplikasi"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.exportBackup() },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buat Backup")
                }

                OutlinedButton(
                    onClick = { fileLauncher.launch(arrayOf("*/*")) },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pulihkan Berkas")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            uiState.errorMessage?.let { error ->
                Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            uiState.successMessage?.let { msg ->
                Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Riwayat Cadangan Tersimpan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.backupFiles.isEmpty()) {
                Text(
                    text = "Belum ada berkas backup di folder lokal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.backupFiles) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = file.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text(text = "${file.length() / 1024} KB", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
```

---

### Task 1.7: Perbaikan `BannerAdView.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/components/BannerAdView.kt`
- **Masalah:** Missing import untuk `collectAsState`, `getValue`, `padding`, dan import salah `androidx.compose.ui.platform.AndroidView` (seharusnya `androidx.compose.ui.viewinterop.AndroidView`).
- **Tindakan:**
  1. Tambahkan import di bagian atas:
  ```kotlin
  import androidx.compose.foundation.layout.padding
  import androidx.compose.runtime.collectAsState
  import androidx.compose.runtime.getValue
  import androidx.compose.ui.viewinterop.AndroidView
  ```
  2. Gantikan baris 68:
  ```kotlin
  // SEBELUM:
  androidx.compose.ui.platform.AndroidView(

  // SESUDAH:
  AndroidView(
  ```

---

### Task 1.8: Perbaikan `SettingsScreen.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/settings/SettingsScreen.kt`
- **Masalah:** Missing import untuk `Modifier`, `dp`, `width`, `collectAsState`.
- **Tindakan:** Tambahkan import berikut di bagian atas file:
  ```kotlin
  import androidx.compose.foundation.layout.width
  import androidx.compose.runtime.collectAsState
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.unit.dp
  ```

---

### Task 1.9: Perbaikan Parameter Catch Gaya Scala pada 4 File
- **Masalah:** Penulisan `catch (_: Exception)` tidak valid di Kotlin standar (gaya Scala). Harus diubah menjadi `catch (e: Exception)`.
- **Daftar File & Tindakan:**
  1. `android/app/src/main/java/edu/lespri/app/ui/finance/ChargeDetailScreen.kt:304`
     - Ubah `} catch (_: Exception) {` $\rightarrow$ `} catch (e: Exception) {`
  2. `android/app/src/main/java/edu/lespri/app/ui/finance/FinanceOverviewScreen.kt:463`
     - Ubah `} catch (_: Exception) {` $\rightarrow$ `} catch (e: Exception) {`
  3. `android/app/src/main/java/edu/lespri/app/ui/finance/PaymentReceiptScreen.kt:256`
     - Ubah `} catch (_: Exception) {` $\rightarrow$ `} catch (e: Exception) {`
  4. `android/app/src/main/java/edu/lespri/app/util/SharesheetHelper.kt:33` & `baris 76`
     - Ubah `} catch (_: Exception) {` $\rightarrow$ `} catch (e: Exception) {`

---

## 4. FASE 2: PERBAIKAN KONEKSI NAVIGASI & UI

### Task 2.1: Sambungkan Seluruh Layar Nyata di `LespriNavHost.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/navigation/LespriNavHost.kt`
- **Masalah:** Layar-layar Milestone 5 (P12, P13, P14, P15) dan Milestone 7 (P21, P22, P23, P24) digantikan oleh `PlaceholderScreen`. Pengguna tidak bisa menggunakan fitur yang sudah dibuat.
- **Tindakan:**
  1. Tambahkan import untuk seluruh layar di atas `LespriNavHost.kt`:
  ```kotlin
  import edu.lespri.app.ui.attendance.AttendanceSheetScreen
  import edu.lespri.app.ui.backup.BackupRestoreScreen
  import edu.lespri.app.ui.csv.CsvExportScreen
  import edu.lespri.app.ui.csv.CsvImportScreen
  import edu.lespri.app.ui.schedule.ScheduleAgendaScreen
  import edu.lespri.app.ui.schedule.SessionDetailScreen
  import edu.lespri.app.ui.schedule.SessionFormScreen
  import edu.lespri.app.ui.settings.SettingsScreen
  ```
  2. Gantikan blok rute baris 200–253 dan baris 299–310 dengan kode berikut:
  ```kotlin
        // --- P12: Jadwal & Agenda (Milestone 5) ---
        composable(NavRoutes.Schedule.route) {
            ScheduleAgendaScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToSession = { sessionId ->
                    navController.navigate(NavRoutes.SessionDetail.createRoute(sessionId))
                }
            )
        }

        // --- P13: Form Sesi (Milestone 5) ---
        composable(
            route = NavRoutes.SessionForm.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("classId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            SessionFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P14: Detail Sesi (Milestone 5) ---
        composable(
            route = NavRoutes.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            SessionDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onOpenAttendance = { sessionId ->
                    navController.navigate(NavRoutes.AttendanceSheet.createRoute(sessionId))
                }
            )
        }

        // --- P15: Lembar Presensi (Milestone 5) ---
        composable(
            route = NavRoutes.AttendanceSheet.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            AttendanceSheetScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P21: Impor CSV (Milestone 7) ---
        composable(NavRoutes.CsvImport.route) {
            CsvImportScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P22: Ekspor CSV (Milestone 7) ---
        composable(NavRoutes.CsvExport.route) {
            CsvExportScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P23: Backup & Restore (Milestone 7) ---
        composable(NavRoutes.BackupRestore.route) {
            BackupRestoreScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P24: Pengaturan (Milestone 7) ---
        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }
  ```

---

### Task 2.2: Perbaikan Race Condition Navigasi Awal di `MainActivity.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/MainActivity.kt`
- **Masalah:** Injeksi langsung `SettingDao` ke `MainActivity` dan `ownerProfile by settingDao.getOwnerProfile().collectAsState(initial = null)` membuat `startDestination` selalu mengarah ke `FirstLaunchSetup` saat pertama kali dikomposisi karena nilai awal adalah null.
- **Tindakan:** Buat pemeriksaan `isSetupLoaded` sebelum merender `LespriNavHost`:
```kotlin
package edu.lespri.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.ui.navigation.LespriNavHost
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.LespriTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingDao: SettingDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LespriTheme {
                val ownerProfileState = settingDao.getOwnerProfile().collectAsState(initial = "LOADING")
                val navController = rememberNavController()

                when (val profile = ownerProfileState.value) {
                    "LOADING" -> {
                        // Tampilkan loading sebentar saat database Room sedang query di thread IO
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        val owner = profile as? edu.lespri.app.data.local.entity.OwnerProfileEntity
                        val startDestination = if (owner != null && owner.businessName.isNotBlank()) {
                            NavRoutes.Dashboard.route
                        } else {
                            NavRoutes.FirstLaunchSetup.route
                        }

                        LespriNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
```

---

### Task 2.3: Perbaikan Navigasi Sesi di `DashboardScreen.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/ui/navigation/LespriNavHost.kt`
- **Masalah:** Baris 56–58 di `LespriNavHost.kt` memiliki lambda kosong untuk `onNavigateToSession`.
- **Tindakan:** Sambungkan ke detail sesi:
```kotlin
        // --- P02: Dashboard ---
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToSession = { sessionId ->
                    navController.navigate(NavRoutes.SessionDetail.createRoute(sessionId))
                }
            )
        }
```

---

## 5. FASE 3: PERBAIKAN LOGIKA BISNIS & INVARIAN DATA

### Task 3.1: Reversal Tagihan Per-Sesi di `AttendanceRepositoryImpl.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/data/repository/AttendanceRepositoryImpl.kt`
- **Masalah:** Jika siswa dengan skema `PER_SESSION` statusnya diubah dari `PRESENT` kembali ke `EXCUSED`/`SICK`/`ABSENT`/`UNRECORDED`, tagihan yang sudah terbit di status `UNPAID` tidak dibatalkan.
- **Tindakan:** Tambahkan logika pembatalan tagihan pada baris 99:
```kotlin
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
```

---

### Task 3.2: Cascade Batal Tagihan Per-Sesi di `ScheduleRepositoryImpl.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/data/repository/ScheduleRepositoryImpl.kt`
- **Masalah:** Fungsi `cancelSession` mengembalikan saldo paket siswa, namun mengabaikan pembatalan tagihan siswa `PER_SESSION` yang terkait dengan sesi tersebut.
- **Tindakan:** Di dalam `cancelSession` (setelah baris 216), tambahkan pembatalan tagihan `PER_SESSION`:
```kotlin
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
```

---

### Task 3.3: Implementasi `AlarmReceiver` untuk Notifikasi Lokal Android 10+
- **File Baru:** `android/app/src/main/java/edu/lespri/app/notification/AlarmReceiver.kt`
- **File Terkait:** `android/app/src/main/AndroidManifest.xml` & `ReminderScheduler.kt`
- **Masalah:** `ReminderScheduler.kt` menggunakan `PendingIntent.getActivity()` dengan `AlarmManager`. Pada Android 10+ (API 29 s/d 36), peluncuran Activity dari background diblokir sistem sehingga notifikasi tidak pernah muncul.
- **Tindakan:**
  1. Buat file baru `AlarmReceiver.kt`:
  ```kotlin
  package edu.lespri.app.notification

  import android.content.BroadcastReceiver
  import android.content.Context
  import android.content.Intent

  class AlarmReceiver : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
          val title = intent.getStringExtra("extra_title") ?: "Lespri Bimbel"
          val message = intent.getStringExtra("extra_message") ?: "Ada agenda bimbingan belajar."
          val channelId = intent.getStringExtra("extra_channel_id") ?: "schedule_reminder"
          val requestId = intent.getStringExtra("extra_request_id") ?: System.currentTimeMillis().toString()

          ReminderScheduler.showNotification(
              context = context,
              channelId = channelId,
              title = title,
              message = message,
              requestId = requestId
          )
      }
  }
  ```
  2. Daftarkan di `AndroidManifest.xml` (di dalam tag `<application>`):
  ```xml
          <!-- Alarm Receiver untuk menangani notifikasi lokal tepat waktu -->
          <receiver
              android:name=".notification.AlarmReceiver"
              android:exported="false" />
  ```
  3. Ubah seluruh pemanggilan `PendingIntent.getActivity` pada `scheduleSessionReminder`, `scheduleBillingReminder`, `scheduleAttendanceReminder`, dan `schedulePackageLowBalanceReminder` di `ReminderScheduler.kt` menjadi `PendingIntent.getBroadcast(context, ..., Intent(context, AlarmReceiver::class.java), ...)`.

---

## 6. FASE 4: PEMBERSIHAN KEMURNIAN ARSITEKTUR DOMAIN & VIEWMODEL

### Task 4.1: Hilangkan Import Room dari `GenerateMonthlyChargesUseCase.kt`
- **File:** `android/app/src/main/java/edu/lespri/app/domain/usecase/GenerateMonthlyChargesUseCase.kt`
- **Masalah:** Baris 3 mengimpor `androidx.room.withTransaction` dan menggunakan DAO/Entity langsung, melanggar kemurnian Domain Layer (Zero Android/Room dependency).
- **Tindakan:**
  1. Pindahkan logika transaksi ke dalam `FinanceRepository`:
     Tambahkan fungsi di `FinanceRepository.kt`:
     ```kotlin
     suspend fun generateMonthlyCharges(targetMonth: String): Result<Int>
     ```
  2. Implementasikan di `FinanceRepositoryImpl.kt` dengan membungkus Room `withTransaction`.
  3. Sederhanakan `GenerateMonthlyChargesUseCase.kt` menjadi murni memanggil repositori:
  ```kotlin
  package edu.lespri.app.domain.usecase

  import edu.lespri.app.domain.repository.FinanceRepository
  import javax.inject.Inject

  class GenerateMonthlyChargesUseCase @Inject constructor(
      private val financeRepository: FinanceRepository
  ) {
      suspend operator fun invoke(targetMonth: String): Result<Int> {
          return financeRepository.generateMonthlyCharges(targetMonth)
      }
  }
  ```

---

### Task 4.2: Pemisahan Akses DAO Langsung dari ViewModel
- **Masalah:** 8 ViewModel menginjeksi DAO Room secara langsung (`ClassListViewModel`, `DashboardViewModel`, `SettingsViewModel`, `SetupViewModel`, `StudentDetailViewModel`, `StudentFormViewModel`, `StudentListViewModel`, `TutorListViewModel`).
- **Tindakan:**
  - Tambahkan method yang dibutuhkan pada interface Repository yang bersangkutan:
    - Contoh: `SettingDao.getOwnerProfile()` $\rightarrow$ sediakan di `SettingRepository` atau melalui method repositori domain.
    - `financeDao.getTotalOutstandingDebt()` $\rightarrow$ sudah tersedia di `financeRepository.getFinancialSummary()`.
  - Hapus injeksi DAO langsung dari konstruktor ViewModel dan gantikan dengan Repository Interface terkait.

---

## 7. CHECKLIST VERIFIKASI AKHIR

Setelah AI Model menyelesaikan perbaikan di atas, jalankan perintah verifikasi berikut di folder `android/`:

```powershell
$env:JAVA_HOME = "C:\Users\webhe\.jdks\jbr-21.0.11"
cd "c:\Users\webhe\Downloads\MSI\Local Sites\lespri\android"

# 1. Kompilasi Kode Kotlin & KSP Room/Hilt
.\gradlew compileDebugKotlin

# 2. Jalankan Unit Test Domain & Repositori
.\gradlew testDebugUnitTest

# 3. Assemble Debug APK
.\gradlew assembleDebug
```

> **Target Kualitas:**
> - [ ] `compileDebugKotlin` **BUILD SUCCESSFUL** (0 error, 0 warning kritis).
> - [ ] `testDebugUnitTest` seluruh unit test lulus 100%.
> - [ ] `assembleDebug` menghasilkan APK di `android/app/build/outputs/apk/debug/app-debug.apk`.
> - [ ] Seluruh 24 layar di `SCREEN-MAP.md` dapat diakses dari menu navigasi tanpa layar kosong `PlaceholderScreen`.
