# ARCHITECTURE CONTRACT: LESPRI ANDROID (MVP v1.1)

> **Document Type:** Machine-Readable Architecture Specification & Execution Contract  
> **Target Package:** `edu.lespri.app` | **Platform:** Android (compileSdk 36, targetSdk 36, minSdk 24)  
> **Source Documents:** `documents/PRD.md` & `documents/SCREEN-MAP.md`  
> **Strict Policy:** Offline-First, Zero Backend, Deterministic State Flow, Single-File Atomic Tasks.

---

## 1. DATA INVARIANTS & DOMAIN BUSINESS RULES

### 1.1 Primitive Type & Value Invariants
- **Monetary Values (IDR):** `Long` representing raw Rupiah. Zero floating-point (`Float`/`Double`) allowed in domain/database layers. Example: Rp 150.000 $\rightarrow$ `150000L`. Minimum value: `0L`.
- **Phone Numbers:** `String`. Must preserve leading zeros (e.g., `"08123456789"`). Stripped of non-numeric chars except leading `+` for international format.
- **Identifier Strategy:** `String` (UUID v4) generated client-side before persistence (`java.util.UUID.randomUUID().toString()`). No SQLite autoincrement primary keys for domain entities to ensure clean backup/restore portability.
- **Date & Time Representation:**
  - Timestamps (audit, payments): `Long` (Epoch milliseconds UTC).
  - Calendar Dates: `String` in ISO-8601 extended format (`YYYY-MM-DD`).
  - Time of Day: `String` in 24-hour format (`HH:mm`).
  - Weekday: `Int` following `java.time.DayOfWeek` standard (`1 = MONDAY` ... `7 = SUNDAY`).

### 1.2 Domain Enums & Value Sets
```kotlin
enum class StudentStatus { ACTIVE, ARCHIVED }
enum class TutorStatus { ACTIVE, ARCHIVED }
enum class ClassType { PRIVATE, GROUP }
enum class BillingType { MONTHLY, PACKAGE, PER_SESSION }
enum class SessionStatus { SCHEDULED, COMPLETED, CANCELLED }
enum class AttendanceStatus { UNRECORDED, PRESENT, EXCUSED, SICK, ABSENT }
enum class ChargeType { MONTHLY, PACKAGE, PER_SESSION }
enum class ChargeStatus { DRAFT, UNPAID, PARTIALLY_PAID, PAID, CANCELLED }
enum class PaymentMethod { CASH, TRANSFER, OTHER }
enum class LedgerReason { PACKAGE_PURCHASE, ATTENDANCE_DEDUCTION, ATTENDANCE_REVERSAL, MANUAL_ADJUSTMENT }
```

### 1.3 Business Logic Invariants
1. **Attendance $\leftrightarrow$ Package Session Balance:**
   - When an `AttendanceRecord` is marked `PRESENT`:
     - If `Enrollment.billing_type == PACKAGE`, system must insert a `PackageLedger` entry with `delta_sessions = -1` and `reason = ATTENDANCE_DEDUCTION`.
   - When an `AttendanceRecord` changes from `PRESENT` to (`EXCUSED` | `SICK` | `ABSENT` | `UNRECORDED`):
     - If `Enrollment.billing_type == PACKAGE`, system must insert a `PackageLedger` entry with `delta_sessions = +1` and `reason = ATTENDANCE_REVERSAL`.
   - Remaining session balance is calculated dynamically:  
     $$\text{RemainingSessions} = \sum (\text{delta\_sessions})$$
2. **Attendance $\leftrightarrow$ Per-Session Billing:**
   - Marking `PRESENT` for `PER_SESSION` billing generates a draft/unpaid `Charge` of `fee_amount` linked to the session.
   - Reverting from `PRESENT` cancels or removes the associated unpaid `Charge`.
3. **Session Cancellation:**
   - Changing `LessonSession.status` to `CANCELLED` nullifies any attendance-based session deduction or per-session charge generation.
4. **Charge Settlement & Payment Invariants:**
   - Balance formula:  
     $$\text{RemainingBalance} = \text{Charge.amount} - \sum (\text{Payment.amount})$$
   - Status transitions:
     - `UNPAID` if $\sum \text{Payment.amount} == 0$.
     - `PARTIALLY_PAID` if $0 < \sum \text{Payment.amount} < \text{Charge.amount}$.
     - `PAID` if $\sum \text{Payment.amount} \ge \text{Charge.amount}$.
   - Overpayment prohibition: A `Payment` cannot exceed the target `Charge`'s remaining balance.
5. **Entity Retention (Soft Delete):**
   - Students, Tutors, and Classes with historical sessions, attendance, or charges MUST be set to `ARCHIVED`, never deleted with SQLite `DELETE` queries.

---

## 2. ROOM DATABASE SCHEMA SPECIFICATION

Database Name: `lespri_database.db`  
Schema Version: `1`

### 2.1 Table Definitions

```sql
-- 1. Owner Profile
CREATE TABLE IF NOT EXISTS `owner_profile` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `business_name` TEXT NOT NULL,
    `created_at` INTEGER NOT NULL,
    `updated_at` INTEGER NOT NULL
);

-- 2. Students
CREATE TABLE IF NOT EXISTS `students` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `name` TEXT NOT NULL,
    `guardian_name` TEXT,
    `guardian_phone` TEXT,
    `school_grade` TEXT,
    `notes` TEXT,
    `status` TEXT NOT NULL DEFAULT 'ACTIVE',
    `joined_at` INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_students_status` ON `students` (`status`);
CREATE INDEX IF NOT EXISTS `index_students_name` ON `students` (`name`);

-- 3. Tutors
CREATE TABLE IF NOT EXISTS `tutors` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `name` TEXT NOT NULL,
    `phone` TEXT,
    `expertise` TEXT,
    `notes` TEXT,
    `status` TEXT NOT NULL DEFAULT 'ACTIVE',
    `created_at` INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_tutors_status` ON `tutors` (`status`);

-- 4. Class Groups
CREATE TABLE IF NOT EXISTS `class_groups` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `name` TEXT NOT NULL,
    `class_type` TEXT NOT NULL,
    `subject` TEXT,
    `tutor_id` TEXT,
    `location_note` TEXT,
    `status` TEXT NOT NULL DEFAULT 'ACTIVE',
    `created_at` INTEGER NOT NULL,
    FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`id`) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS `index_class_groups_tutor_id` ON `class_groups` (`tutor_id`);
CREATE INDEX IF NOT EXISTS `index_class_groups_status` ON `class_groups` (`status`);

-- 5. Enrollments (Student-to-Class link with billing agreement)
CREATE TABLE IF NOT EXISTS `enrollments` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `student_id` TEXT NOT NULL,
    `class_id` TEXT NOT NULL,
    `billing_type` TEXT NOT NULL,
    `fee_amount` INTEGER NOT NULL,
    `due_day` INTEGER,
    `package_session_count` INTEGER,
    `status` TEXT NOT NULL DEFAULT 'ACTIVE',
    `created_at` INTEGER NOT NULL,
    FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`class_id`) REFERENCES `class_groups` (`id`) ON DELETE RESTRICT
);
CREATE UNIQUE INDEX IF NOT EXISTS `index_enrollments_active_student_class` 
ON `enrollments` (`student_id`, `class_id`, `status`);
CREATE INDEX IF NOT EXISTS `index_enrollments_class_id` ON `enrollments` (`class_id`);
CREATE INDEX IF NOT EXISTS `index_enrollments_student_id` ON `enrollments` (`student_id`);

-- 6. Schedule Rules (Recurring schedule patterns)
CREATE TABLE IF NOT EXISTS `schedule_rules` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `class_id` TEXT NOT NULL,
    `tutor_id` TEXT,
    `weekday` INTEGER NOT NULL,
    `start_time` TEXT NOT NULL,
    `duration_minutes` INTEGER NOT NULL,
    `effective_from` TEXT NOT NULL,
    `effective_until` TEXT,
    `status` TEXT NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (`class_id`) REFERENCES `class_groups` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`id`) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS `index_schedule_rules_class_id` ON `schedule_rules` (`class_id`);

-- 7. Lesson Sessions (Concrete scheduled/ad-hoc instances)
CREATE TABLE IF NOT EXISTS `lesson_sessions` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `class_id` TEXT NOT NULL,
    `tutor_id` TEXT,
    `session_date` TEXT NOT NULL,
    `start_time` TEXT NOT NULL,
    `duration_minutes` INTEGER NOT NULL,
    `status` TEXT NOT NULL DEFAULT 'SCHEDULED',
    `source_rule_id` TEXT,
    FOREIGN KEY (`class_id`) REFERENCES `class_groups` (`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`source_rule_id`) REFERENCES `schedule_rules` (`id`) ON DELETE SET NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS `index_lesson_sessions_unique_slot` 
ON `lesson_sessions` (`class_id`, `session_date`, `start_time`);
CREATE INDEX IF NOT EXISTS `index_lesson_sessions_date` ON `lesson_sessions` (`session_date`);

-- 8. Attendance Records
CREATE TABLE IF NOT EXISTS `attendance_records` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `session_id` TEXT NOT NULL,
    `enrollment_id` TEXT NOT NULL,
    `status` TEXT NOT NULL DEFAULT 'UNRECORDED',
    `note` TEXT,
    `updated_at` INTEGER NOT NULL,
    FOREIGN KEY (`session_id`) REFERENCES `lesson_sessions` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`id`) ON DELETE RESTRICT
);
CREATE UNIQUE INDEX IF NOT EXISTS `index_attendance_records_session_enrollment` 
ON `attendance_records` (`session_id`, `enrollment_id`);

-- 9. Charges (Invoices / Bills)
CREATE TABLE IF NOT EXISTS `charges` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `enrollment_id` TEXT NOT NULL,
    `charge_type` TEXT NOT NULL,
    `period_start` TEXT,
    `period_end` TEXT,
    `amount` INTEGER NOT NULL,
    `due_date` TEXT,
    `status` TEXT NOT NULL DEFAULT 'UNPAID',
    `created_at` INTEGER NOT NULL,
    FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`id`) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS `index_charges_enrollment_id` ON `charges` (`enrollment_id`);
CREATE INDEX IF NOT EXISTS `index_charges_status` ON `charges` (`status`);

-- 10. Payments
CREATE TABLE IF NOT EXISTS `payments` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `charge_id` TEXT NOT NULL,
    `amount` INTEGER NOT NULL,
    `paid_at` INTEGER NOT NULL,
    `method` TEXT NOT NULL,
    `reference_note` TEXT,
    FOREIGN KEY (`charge_id`) REFERENCES `charges` (`id`) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS `index_payments_charge_id` ON `payments` (`charge_id`);

-- 11. Package Ledgers (Audit log of package sessions)
CREATE TABLE IF NOT EXISTS `package_ledgers` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `enrollment_id` TEXT NOT NULL,
    `session_id` TEXT,
    `delta_sessions` INTEGER NOT NULL,
    `reason` TEXT NOT NULL,
    `created_at` INTEGER NOT NULL,
    FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`session_id`) REFERENCES `lesson_sessions` (`id`) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS `index_package_ledgers_enrollment_id` ON `package_ledgers` (`enrollment_id`);

-- 12. Notification Preferences
CREATE TABLE IF NOT EXISTS `notification_preferences` (
    `id` TEXT PRIMARY KEY NOT NULL,
    `notification_type` TEXT NOT NULL UNIQUE,
    `is_enabled` INTEGER NOT NULL DEFAULT 1,
    `lead_time_minutes` INTEGER NOT NULL DEFAULT 0
);

-- 13. App Settings (Key-Value)
CREATE TABLE IF NOT EXISTS `app_settings` (
    `setting_key` TEXT PRIMARY KEY NOT NULL,
    `setting_value` TEXT NOT NULL
);
```

---

## 3. CLEAN ARCHITECTURE CONTRACTS

Target Root Package: `edu.lespri.app`

### 3.1 Room DAO Interfaces

```kotlin
package edu.lespri.app.data.local.dao

import androidx.room.*
import edu.lespri.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE status = :status ORDER BY name ASC")
    fun getStudentsByStatus(status: String = "ACTIVE"): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: String): Flow<StudentEntity?>

    @Query("""
        SELECT * FROM students 
        WHERE status = 'ACTIVE' 
        AND (name LIKE '%' || :query || '%' OR guardian_phone LIKE '%' || :query || '%')
    """)
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentsBatch(students: List<StudentEntity>)

    @Query("UPDATE students SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveStudent(id: String)
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM class_groups WHERE status = :status ORDER BY name ASC")
    fun getClassesByStatus(status: String = "ACTIVE"): Flow<List<ClassGroupEntity>>

    @Query("SELECT * FROM class_groups WHERE id = :id")
    fun getClassById(id: String): Flow<ClassGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClass(classGroup: ClassGroupEntity)

    @Query("UPDATE class_groups SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveClass(id: String)

    @Query("SELECT * FROM enrollments WHERE class_id = :classId AND status = 'ACTIVE'")
    fun getActiveEnrollmentsForClass(classId: String): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE student_id = :studentId AND status = 'ACTIVE'")
    fun getActiveEnrollmentsForStudent(studentId: String): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("UPDATE enrollments SET status = 'ARCHIVED' WHERE id = :enrollmentId")
    suspend fun archiveEnrollment(enrollmentId: String)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM lesson_sessions WHERE session_date = :date ORDER BY start_time ASC")
    fun getSessionsForDate(date: String): Flow<List<LessonSessionEntity>>

    @Query("SELECT * FROM lesson_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: String): Flow<LessonSessionEntity?>

    @Query("""
        SELECT * FROM lesson_sessions 
        WHERE session_date >= :startDate AND session_date <= :endDate 
        ORDER BY session_date ASC, start_time ASC
    """)
    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<LessonSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSessions(sessions: List<LessonSessionEntity>): List<Long>

    @Update
    suspend fun updateSession(session: LessonSessionEntity)

    @Query("UPDATE lesson_sessions SET status = 'CANCELLED' WHERE id = :sessionId")
    suspend fun cancelSession(sessionId: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId")
    fun getAttendanceForSession(sessionId: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendanceBatch(records: List<AttendanceRecordEntity>)

    @Query("""
        SELECT COUNT(*) FROM lesson_sessions s
        LEFT JOIN attendance_records a ON s.id = a.session_id
        WHERE s.session_date <= :todayDate AND s.status = 'SCHEDULED' 
        AND (a.id IS NULL OR a.status = 'UNRECORDED')
    """)
    fun countUnrecordedSessions(todayDate: String): Flow<Int>
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM charges WHERE status != 'CANCELLED' ORDER BY created_at DESC")
    fun getAllActiveCharges(): Flow<List<ChargeEntity>>

    @Query("SELECT * FROM charges WHERE enrollment_id = :enrollmentId")
    fun getChargesForEnrollment(enrollmentId: String): Flow<List<ChargeEntity>>

    @Query("SELECT * FROM charges WHERE id = :chargeId")
    fun getChargeById(chargeId: String): Flow<ChargeEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCharge(charge: ChargeEntity)

    @Update
    suspend fun updateCharge(charge: ChargeEntity)

    @Query("SELECT * FROM payments WHERE charge_id = :chargeId ORDER BY paid_at ASC")
    fun getPaymentsForCharge(chargeId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE charge_id = :chargeId")
    suspend fun getTotalPaidForCharge(chargeId: String): Long

    @Query("SELECT * FROM package_ledgers WHERE enrollment_id = :enrollmentId ORDER BY created_at ASC")
    fun getPackageLedger(enrollmentId: String): Flow<List<PackageLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLedgerEntry(entry: PackageLedgerEntity)

    @Query("SELECT COALESCE(SUM(delta_sessions), 0) FROM package_ledgers WHERE enrollment_id = :enrollmentId")
    fun getRemainingSessions(enrollmentId: String): Flow<Int>
}
```

### 3.2 Domain Repository Interfaces

```kotlin
package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getActiveStudents(): Flow<List<Student>>
    fun getStudent(id: String): Flow<Student?>
    fun searchStudents(query: String): Flow<List<Student>>
    suspend fun saveStudent(student: Student): Result<Unit>
    suspend fun archiveStudent(id: String): Result<Unit>
}

interface ClassRepository {
    fun getClasses(): Flow<List<ClassGroup>>
    fun getClassDetail(id: String): Flow<ClassGroupDetail?>
    suspend fun saveClass(classGroup: ClassGroup): Result<Unit>
    suspend fun enrollStudent(enrollment: Enrollment): Result<Unit>
    suspend fun removeEnrollment(enrollmentId: String): Result<Unit>
}

interface ScheduleRepository {
    fun getAgendaSessions(startDate: String, endDate: String): Flow<List<LessonSession>>
    fun getSessionDetail(sessionId: String): Flow<LessonSessionDetail?>
    suspend fun createAdHocSession(session: LessonSession): Result<Unit>
    suspend fun generateRecurringSessions(upToDate: String): Result<Int>
    suspend fun cancelSession(sessionId: String): Result<Unit>
}

interface AttendanceRepository {
    fun getSessionAttendance(sessionId: String): Flow<List<AttendanceRecord>>
    suspend fun submitSessionAttendance(
        sessionId: String, 
        records: List<AttendanceRecord>
    ): Result<Unit>
}

interface FinanceRepository {
    fun getFinancialSummary(): Flow<FinancialSummary>
    fun getCharges(statusFilter: ChargeStatus?): Flow<List<ChargeWithBalance>>
    suspend fun createCharge(charge: Charge): Result<Unit>
    suspend fun recordPayment(payment: Payment): Result<Unit>
    fun getRemainingPackageSessions(enrollmentId: String): Flow<Int>
}

interface BackupRepository {
    suspend fun exportJsonBackup(): Result<String>
    suspend fun restoreJsonBackup(jsonPayload: String): Result<Unit>
    suspend fun exportCsvDataset(datasetName: String): Result<String>
    suspend fun importCsvData(rawCsv: String, columnMapping: Map<String, String>): Result<ImportSummary>
}
```

### 3.3 UI State Models (Unidirectional Data Flow)

```kotlin
package edu.lespri.app.ui.state

import edu.lespri.app.domain.model.*

// P02 - Dashboard
data class DashboardUiState(
    val isLoading: Boolean = false,
    val businessName: String = "",
    val totalOutstandingDebt: Long = 0L,
    val unpaidStudentsCount: Int = 0,
    val currentMonthIncome: Long = 0L,
    val todaySessions: List<LessonSession> = emptyList(),
    val unrecordedSessionsCount: Int = 0,
    val lowPackageStudentsCount: Int = 0,
    val errorMessage: String? = null
)

// P03 - Student List
data class StudentListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val activeFilterGrade: String? = null,
    val students: List<StudentListItem> = emptyList(),
    val errorMessage: String? = null
)

// P08 - Class Detail
data class ClassDetailUiState(
    val isLoading: Boolean = false,
    val classGroup: ClassGroup? = null,
    val assignedTutorName: String? = null,
    val enrolledStudents: List<EnrolledStudentItem> = emptyList(),
    val upcomingSessions: List<LessonSession> = emptyList(),
    val errorMessage: String? = null
)

// P15 - Attendance Recording
data class AttendanceSheetUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val session: LessonSession? = null,
    val className: String = "",
    val studentAttendanceList: List<StudentAttendanceItem> = emptyList(),
    val hasDeductionImpact: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

// P16 - Finance Overview
data class FinanceUiState(
    val selectedTab: FinanceTab = FinanceTab.CHARGES,
    val totalUnpaidAmount: Long = 0L,
    val totalOverdueAmount: Long = 0L,
    val monthlyRevenue: Long = 0L,
    val chargesList: List<ChargeWithBalance> = emptyList(),
    val paymentsList: List<PaymentListItem> = emptyList(),
    val activePackagesList: List<PackageStatusItem> = emptyList(),
    val errorMessage: String? = null
)
enum class FinanceTab { CHARGES, PAYMENTS, PACKAGES }

// P21 - CSV Importer
data class CsvImportUiState(
    val step: ImportStep = ImportStep.SELECT_FILE,
    val fileName: String? = null,
    val detectedHeaders: List<String> = emptyList(),
    val columnMappings: Map<String, String> = emptyMap(), // AppField -> CsvHeader
    val previewRows: List<Map<String, String>> = emptyList(),
    val validRowCount: Int = 0,
    val invalidRowCount: Int = 0,
    val isImporting: Boolean = false,
    val importResult: ImportSummary? = null,
    val errorMessage: String? = null
)
enum class ImportStep { SELECT_FILE, MAP_COLUMNS, PREVIEW, COMPLETED }
```

---

## 4. ATOMIC MILESTONES & TASK BREAKDOWN

*Execution Rule:* 1 Task = Strictly 1 Target File/Component.

### Milestone 1: Foundation, Room Database & Core Models
- [ ] **Task 1.1:** Setup gradle version catalog & dependencies (`gradle/libs.versions.toml`).
- [ ] **Task 1.2:** Implement Domain Enums & Core Entities (`core/model/DomainEnums.kt`).
- [ ] **Task 1.3:** Create Room Entity: `OwnerProfileEntity.kt`.
- [ ] **Task 1.4:** Create Room Entity: `StudentEntity.kt`.
- [ ] **Task 1.5:** Create Room Entity: `TutorEntity.kt`.
- [ ] **Task 1.6:** Create Room Entity: `ClassGroupEntity.kt`.
- [ ] **Task 1.7:** Create Room Entity: `EnrollmentEntity.kt`.
- [ ] **Task 1.8:** Create Room Entity: `ScheduleRuleEntity.kt`.
- [ ] **Task 1.9:** Create Room Entity: `LessonSessionEntity.kt`.
- [ ] **Task 1.10:** Create Room Entity: `AttendanceRecordEntity.kt`.
- [ ] **Task 1.11:** Create Room Entity: `ChargeEntity.kt`.
- [ ] **Task 1.12:** Create Room Entity: `PaymentEntity.kt`.
- [ ] **Task 1.13:** Create Room Entity: `PackageLedgerEntity.kt`.
- [ ] **Task 1.14:** Create Room Entity: `NotificationPreferenceEntity.kt` & `AppSettingEntity.kt`.
- [ ] **Task 1.15:** Create Room DAO: `StudentDao.kt`.
- [ ] **Task 1.16:** Create Room DAO: `TutorDao.kt`.
- [ ] **Task 1.17:** Create Room DAO: `ClassDao.kt`.
- [ ] **Task 1.18:** Create Room DAO: `ScheduleDao.kt`.
- [ ] **Task 1.19:** Create Room DAO: `AttendanceDao.kt`.
- [ ] **Task 1.20:** Create Room DAO: `FinanceDao.kt`.
- [ ] **Task 1.21:** Implement Room Database configuration & migrations (`LespriDatabase.kt`).
- [ ] **Task 1.22:** Setup Hilt/Koin DI Module for Database & DAOs (`DatabaseModule.kt`).

### Milestone 2: Domain Layer & Repositories
- [ ] **Task 2.1:** Implement `StudentRepositoryImpl.kt`.
- [ ] **Task 2.2:** Implement `TutorRepositoryImpl.kt`.
- [ ] **Task 2.3:** Implement `ClassRepositoryImpl.kt`.
- [ ] **Task 2.4:** Implement `ScheduleRepositoryImpl.kt` (including recurring session generation logic).
- [ ] **Task 2.5:** Implement `AttendanceRepositoryImpl.kt` (with atomic package deduction & reversal transaction).
- [ ] **Task 2.6:** Implement `FinanceRepositoryImpl.kt` (with balance checks & overpayment prevention).
- [ ] **Task 2.7:** Implement Use Case: `RecordAttendanceUseCase.kt`.
- [ ] **Task 2.8:** Implement Use Case: `CalculateStudentDebtUseCase.kt`.
- [ ] **Task 2.9:** Implement Use Case: `GenerateMonthlyChargesUseCase.kt`.

### Milestone 3: Presentation Core, Navigation & Dashboard
- [ ] **Task 3.1:** Setup Theme, Typography & Color Palette (`ui/theme/Theme.kt`).
- [ ] **Task 3.2:** Define Navigation Route definitions & Screen Graph (`ui/navigation/NavRoutes.kt`).
- [ ] **Task 3.3:** Implement App Scaffold with Adaptive BottomBar/NavRail (`ui/components/LespriScaffold.kt`).
- [ ] **Task 3.4:** Implement Reusable UI: Compact Row Card (`ui/components/CompactRow.kt`).
- [ ] **Task 3.5:** Implement Reusable UI: Status Chip (`ui/components/StatusChip.kt`).
- [ ] **Task 3.6:** Implement `DashboardViewModel.kt`.
- [ ] **Task 3.7:** Implement `DashboardScreen.kt` (P02).
- [ ] **Task 3.8:** Implement `FirstLaunchSetupScreen.kt` (P01).

### Milestone 4: Students, Tutors & Classes Flow
- [ ] **Task 4.1:** Implement `StudentListViewModel.kt`.
- [ ] **Task 4.2:** Implement `StudentListScreen.kt` (P03).
- [ ] **Task 4.3:** Implement `StudentFormScreen.kt` (P04).
- [ ] **Task 4.4:** Implement `StudentDetailScreen.kt` (P05).
- [ ] **Task 4.5:** Implement `TutorListScreen.kt` (P09).
- [ ] **Task 4.6:** Implement `TutorFormScreen.kt` (P10).
- [ ] **Task 4.7:** Implement `ClassListScreen.kt` (P06).
- [ ] **Task 4.8:** Implement `ClassFormScreen.kt` (P07).
- [ ] **Task 4.9:** Implement `ClassDetailScreen.kt` (P08).

### Milestone 5: Schedule & Attendance
- [ ] **Task 5.1:** Implement `ScheduleAgendaViewModel.kt`.
- [ ] **Task 5.2:** Implement `ScheduleAgendaScreen.kt` (P12).
- [ ] **Task 5.3:** Implement `SessionFormScreen.kt` (P13).
- [ ] **Task 5.4:** Implement `SessionDetailScreen.kt` (P14).
- [ ] **Task 5.5:** Implement `AttendanceSheetViewModel.kt`.
- [ ] **Task 5.6:** Implement `AttendanceSheetScreen.kt` (P15).

### Milestone 6: Financial Operations & Reminders
- [ ] **Task 6.1:** Implement `FinanceViewModel.kt`.
- [ ] **Task 6.2:** Implement `FinanceOverviewScreen.kt` (P16).
- [ ] **Task 6.3:** Implement `ChargeDetailScreen.kt` (P17).
- [ ] **Task 6.4:** Implement `RecordPaymentScreen.kt` (P18).
- [ ] **Task 6.5:** Implement `PaymentReceiptScreen.kt` (P19).
- [ ] **Task 6.6:** Implement WhatsApp/System Share Intent Dispatcher (`util/SharesheetHelper.kt`).
- [ ] **Task 6.7:** Implement Local Android Reminder Scheduler (`notification/ReminderScheduler.kt`).
- [ ] **Task 6.8:** Implement BroadcastReceiver for device reboot (`notification/BootReceiver.kt`).

### Milestone 7: Data Portability (CSV/JSON) & Monetization
- [ ] **Task 7.1:** Implement RFC 4180 Streaming CSV Parser & Generator (`data/csv/CsvEngine.kt`).
- [ ] **Task 7.2:** Implement `CsvImportViewModel.kt`.
- [ ] **Task 7.3:** Implement `CsvImportScreen.kt` (P21).
- [ ] **Task 7.4:** Implement `CsvExportScreen.kt` (P22).
- [ ] **Task 7.5:** Implement Full JSON Backup/Restore Serializer with GZIP (`data/backup/BackupManager.kt`).
- [ ] **Task 7.6:** Implement `BackupRestoreScreen.kt` (P23).
- [ ] **Task 7.7:** Implement Centralized AdMob & Remote Config Manager (`ads/AdManager.kt`).
- [ ] **Task 7.8:** Implement Adaptive Banner Composable with 16dp Safety Margin (`ads/BannerAdView.kt`).
- [ ] **Task 7.9:** Implement `SettingsScreen.kt` (P24).

---

## 5. VERIFICATION & QUALITY GATES

1. **Deterministic Unit Tests:**
   - Attendance deduction math must verify balance consistency across 100 random simulated check-ins/cancellations.
   - Payment balance calculation must assert zero floating-point imprecision (`Long` assertions only).
2. **Offline-First Isolation:**
   - Every DAO read/write must succeed under airplane mode with zero network socket allocation.
3. **Storage Access Framework (SAF) Compliance:**
   - Backup JSON and CSV exports must write directly via Android DocumentFile / ContentResolver URI stream.
