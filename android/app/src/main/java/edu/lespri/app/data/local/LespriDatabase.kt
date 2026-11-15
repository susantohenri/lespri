package edu.lespri.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
import edu.lespri.app.data.local.entity.NotificationPreferenceEntity
import edu.lespri.app.data.local.entity.OwnerProfileEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.local.entity.PaymentEntity
import edu.lespri.app.data.local.entity.ScheduleRuleEntity
import edu.lespri.app.data.local.entity.StudentEntity
import edu.lespri.app.data.local.entity.TutorEntity
import edu.lespri.app.domain.model.LespriConstants

// =============================================================================
// ROOM DATABASE
// =============================================================================

/**
 * Database utama aplikasi Lespri — SQLite via Room.
 *
 * Versi: 1 (MVP). Setiap perubahan skema HARUS menaikkan [version] dan
 * menyertakan [androidx.room.migration.Migration] sebelum di-deploy ke produksi.
 *
 * Entitas (13 tabel):
 * 1.  OwnerProfileEntity        → owner_profile
 * 2.  StudentEntity             → students
 * 3.  TutorEntity               → tutors
 * 4.  ClassGroupEntity          → class_groups
 * 5.  EnrollmentEntity          → enrollments
 * 6.  ScheduleRuleEntity        → schedule_rules
 * 7.  LessonSessionEntity       → lesson_sessions
 * 8.  AttendanceRecordEntity    → attendance_records
 * 9.  ChargeEntity              → charges
 * 10. PaymentEntity             → payments
 * 11. PackageLedgerEntity       → package_ledgers
 * 12. NotificationPreferenceEntity → notification_preferences
 * 13. AppSettingEntity          → app_settings
 *
 * Offline-first: tidak ada network dependency. Seluruh operasi murni SQLite.
 */
@Database(
    entities = [
        OwnerProfileEntity::class,
        StudentEntity::class,
        TutorEntity::class,
        ClassGroupEntity::class,
        EnrollmentEntity::class,
        ScheduleRuleEntity::class,
        LessonSessionEntity::class,
        AttendanceRecordEntity::class,
        ChargeEntity::class,
        PaymentEntity::class,
        PackageLedgerEntity::class,
        NotificationPreferenceEntity::class,
        AppSettingEntity::class
    ],
    version = LespriConstants.DATABASE_VERSION,
    exportSchema = true  // Schema JSON di-export ke android/app/schemas/ untuk migrasi
)
abstract class LespriDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun tutorDao(): TutorDao
    abstract fun classDao(): ClassDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun financeDao(): FinanceDao
    abstract fun settingDao(): SettingDao

    companion object {
        /**
         * Nama file database SQLite. Harus konsisten dengan [LespriConstants.DATABASE_NAME].
         */
        const val DATABASE_NAME = LespriConstants.DATABASE_NAME
    }
}
