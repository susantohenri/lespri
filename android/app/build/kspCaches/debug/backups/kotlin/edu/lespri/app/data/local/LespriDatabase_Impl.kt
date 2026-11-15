package edu.lespri.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import edu.lespri.app.`data`.local.dao.AttendanceDao
import edu.lespri.app.`data`.local.dao.AttendanceDao_Impl
import edu.lespri.app.`data`.local.dao.ClassDao
import edu.lespri.app.`data`.local.dao.ClassDao_Impl
import edu.lespri.app.`data`.local.dao.FinanceDao
import edu.lespri.app.`data`.local.dao.FinanceDao_Impl
import edu.lespri.app.`data`.local.dao.ScheduleDao
import edu.lespri.app.`data`.local.dao.ScheduleDao_Impl
import edu.lespri.app.`data`.local.dao.StudentDao
import edu.lespri.app.`data`.local.dao.StudentDao_Impl
import edu.lespri.app.`data`.local.dao.TutorDao
import edu.lespri.app.`data`.local.dao.TutorDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LespriDatabase_Impl : LespriDatabase() {
  private val _studentDao: Lazy<StudentDao> = lazy {
    StudentDao_Impl(this)
  }

  private val _tutorDao: Lazy<TutorDao> = lazy {
    TutorDao_Impl(this)
  }

  private val _classDao: Lazy<ClassDao> = lazy {
    ClassDao_Impl(this)
  }

  private val _scheduleDao: Lazy<ScheduleDao> = lazy {
    ScheduleDao_Impl(this)
  }

  private val _attendanceDao: Lazy<AttendanceDao> = lazy {
    AttendanceDao_Impl(this)
  }

  private val _financeDao: Lazy<FinanceDao> = lazy {
    FinanceDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "5e08f86878532265e585e8dd050f055c", "b3fb74f57a469f3ad80a877f0aec9757") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `owner_profile` (`id` TEXT NOT NULL, `business_name` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `students` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `guardian_name` TEXT, `guardian_phone` TEXT, `school_grade` TEXT, `notes` TEXT, `status` TEXT NOT NULL DEFAULT 'ACTIVE', `joined_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_students_status` ON `students` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_students_name` ON `students` (`name`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tutors` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT, `expertise` TEXT, `notes` TEXT, `status` TEXT NOT NULL DEFAULT 'ACTIVE', `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tutors_status` ON `tutors` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `class_groups` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `class_type` TEXT NOT NULL, `subject` TEXT, `tutor_id` TEXT, `location_note` TEXT, `status` TEXT NOT NULL DEFAULT 'ACTIVE', `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`tutor_id`) REFERENCES `tutors`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_class_groups_tutor_id` ON `class_groups` (`tutor_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_class_groups_status` ON `class_groups` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `enrollments` (`id` TEXT NOT NULL, `student_id` TEXT NOT NULL, `class_id` TEXT NOT NULL, `billing_type` TEXT NOT NULL, `fee_amount` INTEGER NOT NULL, `due_day` INTEGER, `package_session_count` INTEGER, `status` TEXT NOT NULL DEFAULT 'ACTIVE', `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`student_id`) REFERENCES `students`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`class_id`) REFERENCES `class_groups`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_enrollments_active_student_class` ON `enrollments` (`student_id`, `class_id`, `status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enrollments_class_id` ON `enrollments` (`class_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enrollments_student_id` ON `enrollments` (`student_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `schedule_rules` (`id` TEXT NOT NULL, `class_id` TEXT NOT NULL, `tutor_id` TEXT, `weekday` INTEGER NOT NULL, `start_time` TEXT NOT NULL, `duration_minutes` INTEGER NOT NULL, `effective_from` TEXT NOT NULL, `effective_until` TEXT, `status` TEXT NOT NULL DEFAULT 'ACTIVE', PRIMARY KEY(`id`), FOREIGN KEY(`class_id`) REFERENCES `class_groups`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tutor_id`) REFERENCES `tutors`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_schedule_rules_class_id` ON `schedule_rules` (`class_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_schedule_rules_tutor_id` ON `schedule_rules` (`tutor_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `lesson_sessions` (`id` TEXT NOT NULL, `class_id` TEXT NOT NULL, `tutor_id` TEXT, `session_date` TEXT NOT NULL, `start_time` TEXT NOT NULL, `duration_minutes` INTEGER NOT NULL, `status` TEXT NOT NULL DEFAULT 'SCHEDULED', `source_rule_id` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`class_id`) REFERENCES `class_groups`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`tutor_id`) REFERENCES `tutors`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`source_rule_id`) REFERENCES `schedule_rules`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lesson_sessions_unique_slot` ON `lesson_sessions` (`class_id`, `session_date`, `start_time`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lesson_sessions_date` ON `lesson_sessions` (`session_date`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lesson_sessions_tutor_id` ON `lesson_sessions` (`tutor_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lesson_sessions_source_rule_id` ON `lesson_sessions` (`source_rule_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `attendance_records` (`id` TEXT NOT NULL, `session_id` TEXT NOT NULL, `enrollment_id` TEXT NOT NULL, `status` TEXT NOT NULL DEFAULT 'UNRECORDED', `note` TEXT, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`session_id`) REFERENCES `lesson_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`enrollment_id`) REFERENCES `enrollments`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_attendance_records_session_enrollment` ON `attendance_records` (`session_id`, `enrollment_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_attendance_records_enrollment_id` ON `attendance_records` (`enrollment_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `charges` (`id` TEXT NOT NULL, `enrollment_id` TEXT NOT NULL, `charge_type` TEXT NOT NULL, `period_start` TEXT, `period_end` TEXT, `amount` INTEGER NOT NULL, `due_date` TEXT, `status` TEXT NOT NULL DEFAULT 'UNPAID', `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`enrollment_id`) REFERENCES `enrollments`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_charges_enrollment_id` ON `charges` (`enrollment_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_charges_status` ON `charges` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`id` TEXT NOT NULL, `charge_id` TEXT NOT NULL, `amount` INTEGER NOT NULL, `paid_at` INTEGER NOT NULL, `method` TEXT NOT NULL, `reference_note` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`charge_id`) REFERENCES `charges`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_charge_id` ON `payments` (`charge_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `package_ledgers` (`id` TEXT NOT NULL, `enrollment_id` TEXT NOT NULL, `session_id` TEXT, `delta_sessions` INTEGER NOT NULL, `reason` TEXT NOT NULL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`enrollment_id`) REFERENCES `enrollments`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`session_id`) REFERENCES `lesson_sessions`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_package_ledgers_enrollment_id` ON `package_ledgers` (`enrollment_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_package_ledgers_session_id` ON `package_ledgers` (`session_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notification_preferences` (`id` TEXT NOT NULL, `notification_type` TEXT NOT NULL, `is_enabled` INTEGER NOT NULL DEFAULT 1, `lead_time_minutes` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_notification_preferences_notification_type` ON `notification_preferences` (`notification_type`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_settings` (`setting_key` TEXT NOT NULL, `setting_value` TEXT NOT NULL, PRIMARY KEY(`setting_key`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5e08f86878532265e585e8dd050f055c')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `owner_profile`")
        connection.execSQL("DROP TABLE IF EXISTS `students`")
        connection.execSQL("DROP TABLE IF EXISTS `tutors`")
        connection.execSQL("DROP TABLE IF EXISTS `class_groups`")
        connection.execSQL("DROP TABLE IF EXISTS `enrollments`")
        connection.execSQL("DROP TABLE IF EXISTS `schedule_rules`")
        connection.execSQL("DROP TABLE IF EXISTS `lesson_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `attendance_records`")
        connection.execSQL("DROP TABLE IF EXISTS `charges`")
        connection.execSQL("DROP TABLE IF EXISTS `payments`")
        connection.execSQL("DROP TABLE IF EXISTS `package_ledgers`")
        connection.execSQL("DROP TABLE IF EXISTS `notification_preferences`")
        connection.execSQL("DROP TABLE IF EXISTS `app_settings`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsOwnerProfile: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOwnerProfile.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOwnerProfile.put("business_name", TableInfo.Column("business_name", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOwnerProfile.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOwnerProfile.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOwnerProfile: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOwnerProfile: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoOwnerProfile: TableInfo = TableInfo("owner_profile", _columnsOwnerProfile,
            _foreignKeysOwnerProfile, _indicesOwnerProfile)
        val _existingOwnerProfile: TableInfo = read(connection, "owner_profile")
        if (!_infoOwnerProfile.equals(_existingOwnerProfile)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |owner_profile(edu.lespri.app.data.local.entity.OwnerProfileEntity).
              | Expected:
              |""".trimMargin() + _infoOwnerProfile + """
              |
              | Found:
              |""".trimMargin() + _existingOwnerProfile)
        }
        val _columnsStudents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStudents.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("guardian_name", TableInfo.Column("guardian_name", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("guardian_phone", TableInfo.Column("guardian_phone", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("school_grade", TableInfo.Column("school_grade", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("status", TableInfo.Column("status", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudents.put("joined_at", TableInfo.Column("joined_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStudents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStudents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStudents.add(TableInfo.Index("index_students_status", false, listOf("status"),
            listOf("ASC")))
        _indicesStudents.add(TableInfo.Index("index_students_name", false, listOf("name"),
            listOf("ASC")))
        val _infoStudents: TableInfo = TableInfo("students", _columnsStudents, _foreignKeysStudents,
            _indicesStudents)
        val _existingStudents: TableInfo = read(connection, "students")
        if (!_infoStudents.equals(_existingStudents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |students(edu.lespri.app.data.local.entity.StudentEntity).
              | Expected:
              |""".trimMargin() + _infoStudents + """
              |
              | Found:
              |""".trimMargin() + _existingStudents)
        }
        val _columnsTutors: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTutors.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("phone", TableInfo.Column("phone", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("expertise", TableInfo.Column("expertise", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("status", TableInfo.Column("status", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutors.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTutors: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTutors: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTutors.add(TableInfo.Index("index_tutors_status", false, listOf("status"),
            listOf("ASC")))
        val _infoTutors: TableInfo = TableInfo("tutors", _columnsTutors, _foreignKeysTutors,
            _indicesTutors)
        val _existingTutors: TableInfo = read(connection, "tutors")
        if (!_infoTutors.equals(_existingTutors)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tutors(edu.lespri.app.data.local.entity.TutorEntity).
              | Expected:
              |""".trimMargin() + _infoTutors + """
              |
              | Found:
              |""".trimMargin() + _existingTutors)
        }
        val _columnsClassGroups: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsClassGroups.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("class_type", TableInfo.Column("class_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("subject", TableInfo.Column("subject", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("tutor_id", TableInfo.Column("tutor_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("location_note", TableInfo.Column("location_note", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("status", TableInfo.Column("status", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClassGroups.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysClassGroups: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysClassGroups.add(TableInfo.ForeignKey("tutors", "SET NULL", "NO ACTION",
            listOf("tutor_id"), listOf("id")))
        val _indicesClassGroups: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesClassGroups.add(TableInfo.Index("index_class_groups_tutor_id", false,
            listOf("tutor_id"), listOf("ASC")))
        _indicesClassGroups.add(TableInfo.Index("index_class_groups_status", false,
            listOf("status"), listOf("ASC")))
        val _infoClassGroups: TableInfo = TableInfo("class_groups", _columnsClassGroups,
            _foreignKeysClassGroups, _indicesClassGroups)
        val _existingClassGroups: TableInfo = read(connection, "class_groups")
        if (!_infoClassGroups.equals(_existingClassGroups)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |class_groups(edu.lespri.app.data.local.entity.ClassGroupEntity).
              | Expected:
              |""".trimMargin() + _infoClassGroups + """
              |
              | Found:
              |""".trimMargin() + _existingClassGroups)
        }
        val _columnsEnrollments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEnrollments.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("student_id", TableInfo.Column("student_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("class_id", TableInfo.Column("class_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("billing_type", TableInfo.Column("billing_type", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("fee_amount", TableInfo.Column("fee_amount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("due_day", TableInfo.Column("due_day", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("package_session_count", TableInfo.Column("package_session_count",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("status", TableInfo.Column("status", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnrollments.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEnrollments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysEnrollments.add(TableInfo.ForeignKey("students", "RESTRICT", "NO ACTION",
            listOf("student_id"), listOf("id")))
        _foreignKeysEnrollments.add(TableInfo.ForeignKey("class_groups", "RESTRICT", "NO ACTION",
            listOf("class_id"), listOf("id")))
        val _indicesEnrollments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEnrollments.add(TableInfo.Index("index_enrollments_active_student_class", true,
            listOf("student_id", "class_id", "status"), listOf("ASC", "ASC", "ASC")))
        _indicesEnrollments.add(TableInfo.Index("index_enrollments_class_id", false,
            listOf("class_id"), listOf("ASC")))
        _indicesEnrollments.add(TableInfo.Index("index_enrollments_student_id", false,
            listOf("student_id"), listOf("ASC")))
        val _infoEnrollments: TableInfo = TableInfo("enrollments", _columnsEnrollments,
            _foreignKeysEnrollments, _indicesEnrollments)
        val _existingEnrollments: TableInfo = read(connection, "enrollments")
        if (!_infoEnrollments.equals(_existingEnrollments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |enrollments(edu.lespri.app.data.local.entity.EnrollmentEntity).
              | Expected:
              |""".trimMargin() + _infoEnrollments + """
              |
              | Found:
              |""".trimMargin() + _existingEnrollments)
        }
        val _columnsScheduleRules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsScheduleRules.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("class_id", TableInfo.Column("class_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("tutor_id", TableInfo.Column("tutor_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("weekday", TableInfo.Column("weekday", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("start_time", TableInfo.Column("start_time", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("duration_minutes", TableInfo.Column("duration_minutes",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("effective_from", TableInfo.Column("effective_from", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("effective_until", TableInfo.Column("effective_until", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleRules.put("status", TableInfo.Column("status", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysScheduleRules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysScheduleRules.add(TableInfo.ForeignKey("class_groups", "CASCADE", "NO ACTION",
            listOf("class_id"), listOf("id")))
        _foreignKeysScheduleRules.add(TableInfo.ForeignKey("tutors", "SET NULL", "NO ACTION",
            listOf("tutor_id"), listOf("id")))
        val _indicesScheduleRules: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesScheduleRules.add(TableInfo.Index("index_schedule_rules_class_id", false,
            listOf("class_id"), listOf("ASC")))
        _indicesScheduleRules.add(TableInfo.Index("index_schedule_rules_tutor_id", false,
            listOf("tutor_id"), listOf("ASC")))
        val _infoScheduleRules: TableInfo = TableInfo("schedule_rules", _columnsScheduleRules,
            _foreignKeysScheduleRules, _indicesScheduleRules)
        val _existingScheduleRules: TableInfo = read(connection, "schedule_rules")
        if (!_infoScheduleRules.equals(_existingScheduleRules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |schedule_rules(edu.lespri.app.data.local.entity.ScheduleRuleEntity).
              | Expected:
              |""".trimMargin() + _infoScheduleRules + """
              |
              | Found:
              |""".trimMargin() + _existingScheduleRules)
        }
        val _columnsLessonSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLessonSessions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("class_id", TableInfo.Column("class_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("tutor_id", TableInfo.Column("tutor_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("session_date", TableInfo.Column("session_date", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("start_time", TableInfo.Column("start_time", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("duration_minutes", TableInfo.Column("duration_minutes",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("status", TableInfo.Column("status", "TEXT", true, 0,
            "'SCHEDULED'", TableInfo.CREATED_FROM_ENTITY))
        _columnsLessonSessions.put("source_rule_id", TableInfo.Column("source_rule_id", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLessonSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysLessonSessions.add(TableInfo.ForeignKey("class_groups", "RESTRICT", "NO ACTION",
            listOf("class_id"), listOf("id")))
        _foreignKeysLessonSessions.add(TableInfo.ForeignKey("tutors", "SET NULL", "NO ACTION",
            listOf("tutor_id"), listOf("id")))
        _foreignKeysLessonSessions.add(TableInfo.ForeignKey("schedule_rules", "SET NULL",
            "NO ACTION", listOf("source_rule_id"), listOf("id")))
        val _indicesLessonSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLessonSessions.add(TableInfo.Index("index_lesson_sessions_unique_slot", true,
            listOf("class_id", "session_date", "start_time"), listOf("ASC", "ASC", "ASC")))
        _indicesLessonSessions.add(TableInfo.Index("index_lesson_sessions_date", false,
            listOf("session_date"), listOf("ASC")))
        _indicesLessonSessions.add(TableInfo.Index("index_lesson_sessions_tutor_id", false,
            listOf("tutor_id"), listOf("ASC")))
        _indicesLessonSessions.add(TableInfo.Index("index_lesson_sessions_source_rule_id", false,
            listOf("source_rule_id"), listOf("ASC")))
        val _infoLessonSessions: TableInfo = TableInfo("lesson_sessions", _columnsLessonSessions,
            _foreignKeysLessonSessions, _indicesLessonSessions)
        val _existingLessonSessions: TableInfo = read(connection, "lesson_sessions")
        if (!_infoLessonSessions.equals(_existingLessonSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |lesson_sessions(edu.lespri.app.data.local.entity.LessonSessionEntity).
              | Expected:
              |""".trimMargin() + _infoLessonSessions + """
              |
              | Found:
              |""".trimMargin() + _existingLessonSessions)
        }
        val _columnsAttendanceRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAttendanceRecords.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAttendanceRecords.put("session_id", TableInfo.Column("session_id", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAttendanceRecords.put("enrollment_id", TableInfo.Column("enrollment_id", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAttendanceRecords.put("status", TableInfo.Column("status", "TEXT", true, 0,
            "'UNRECORDED'", TableInfo.CREATED_FROM_ENTITY))
        _columnsAttendanceRecords.put("note", TableInfo.Column("note", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAttendanceRecords.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAttendanceRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysAttendanceRecords.add(TableInfo.ForeignKey("lesson_sessions", "CASCADE",
            "NO ACTION", listOf("session_id"), listOf("id")))
        _foreignKeysAttendanceRecords.add(TableInfo.ForeignKey("enrollments", "RESTRICT",
            "NO ACTION", listOf("enrollment_id"), listOf("id")))
        val _indicesAttendanceRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAttendanceRecords.add(TableInfo.Index("index_attendance_records_session_enrollment",
            true, listOf("session_id", "enrollment_id"), listOf("ASC", "ASC")))
        _indicesAttendanceRecords.add(TableInfo.Index("index_attendance_records_enrollment_id",
            false, listOf("enrollment_id"), listOf("ASC")))
        val _infoAttendanceRecords: TableInfo = TableInfo("attendance_records",
            _columnsAttendanceRecords, _foreignKeysAttendanceRecords, _indicesAttendanceRecords)
        val _existingAttendanceRecords: TableInfo = read(connection, "attendance_records")
        if (!_infoAttendanceRecords.equals(_existingAttendanceRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |attendance_records(edu.lespri.app.data.local.entity.AttendanceRecordEntity).
              | Expected:
              |""".trimMargin() + _infoAttendanceRecords + """
              |
              | Found:
              |""".trimMargin() + _existingAttendanceRecords)
        }
        val _columnsCharges: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCharges.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("enrollment_id", TableInfo.Column("enrollment_id", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("charge_type", TableInfo.Column("charge_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("period_start", TableInfo.Column("period_start", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("period_end", TableInfo.Column("period_end", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("amount", TableInfo.Column("amount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("due_date", TableInfo.Column("due_date", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("status", TableInfo.Column("status", "TEXT", true, 0, "'UNPAID'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCharges.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCharges: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCharges.add(TableInfo.ForeignKey("enrollments", "RESTRICT", "NO ACTION",
            listOf("enrollment_id"), listOf("id")))
        val _indicesCharges: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCharges.add(TableInfo.Index("index_charges_enrollment_id", false,
            listOf("enrollment_id"), listOf("ASC")))
        _indicesCharges.add(TableInfo.Index("index_charges_status", false, listOf("status"),
            listOf("ASC")))
        val _infoCharges: TableInfo = TableInfo("charges", _columnsCharges, _foreignKeysCharges,
            _indicesCharges)
        val _existingCharges: TableInfo = read(connection, "charges")
        if (!_infoCharges.equals(_existingCharges)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |charges(edu.lespri.app.data.local.entity.ChargeEntity).
              | Expected:
              |""".trimMargin() + _infoCharges + """
              |
              | Found:
              |""".trimMargin() + _existingCharges)
        }
        val _columnsPayments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPayments.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("charge_id", TableInfo.Column("charge_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("amount", TableInfo.Column("amount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("paid_at", TableInfo.Column("paid_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("method", TableInfo.Column("method", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("reference_note", TableInfo.Column("reference_note", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPayments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysPayments.add(TableInfo.ForeignKey("charges", "RESTRICT", "NO ACTION",
            listOf("charge_id"), listOf("id")))
        val _indicesPayments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPayments.add(TableInfo.Index("index_payments_charge_id", false, listOf("charge_id"),
            listOf("ASC")))
        val _infoPayments: TableInfo = TableInfo("payments", _columnsPayments, _foreignKeysPayments,
            _indicesPayments)
        val _existingPayments: TableInfo = read(connection, "payments")
        if (!_infoPayments.equals(_existingPayments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |payments(edu.lespri.app.data.local.entity.PaymentEntity).
              | Expected:
              |""".trimMargin() + _infoPayments + """
              |
              | Found:
              |""".trimMargin() + _existingPayments)
        }
        val _columnsPackageLedgers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPackageLedgers.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPackageLedgers.put("enrollment_id", TableInfo.Column("enrollment_id", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPackageLedgers.put("session_id", TableInfo.Column("session_id", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPackageLedgers.put("delta_sessions", TableInfo.Column("delta_sessions", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPackageLedgers.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPackageLedgers.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPackageLedgers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysPackageLedgers.add(TableInfo.ForeignKey("enrollments", "RESTRICT", "NO ACTION",
            listOf("enrollment_id"), listOf("id")))
        _foreignKeysPackageLedgers.add(TableInfo.ForeignKey("lesson_sessions", "SET NULL",
            "NO ACTION", listOf("session_id"), listOf("id")))
        val _indicesPackageLedgers: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPackageLedgers.add(TableInfo.Index("index_package_ledgers_enrollment_id", false,
            listOf("enrollment_id"), listOf("ASC")))
        _indicesPackageLedgers.add(TableInfo.Index("index_package_ledgers_session_id", false,
            listOf("session_id"), listOf("ASC")))
        val _infoPackageLedgers: TableInfo = TableInfo("package_ledgers", _columnsPackageLedgers,
            _foreignKeysPackageLedgers, _indicesPackageLedgers)
        val _existingPackageLedgers: TableInfo = read(connection, "package_ledgers")
        if (!_infoPackageLedgers.equals(_existingPackageLedgers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |package_ledgers(edu.lespri.app.data.local.entity.PackageLedgerEntity).
              | Expected:
              |""".trimMargin() + _infoPackageLedgers + """
              |
              | Found:
              |""".trimMargin() + _existingPackageLedgers)
        }
        val _columnsNotificationPreferences: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotificationPreferences.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificationPreferences.put("notification_type",
            TableInfo.Column("notification_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificationPreferences.put("is_enabled", TableInfo.Column("is_enabled", "INTEGER",
            true, 0, "1", TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificationPreferences.put("lead_time_minutes",
            TableInfo.Column("lead_time_minutes", "INTEGER", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotificationPreferences: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNotificationPreferences: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesNotificationPreferences.add(TableInfo.Index("index_notification_preferences_notification_type",
            true, listOf("notification_type"), listOf("ASC")))
        val _infoNotificationPreferences: TableInfo = TableInfo("notification_preferences",
            _columnsNotificationPreferences, _foreignKeysNotificationPreferences,
            _indicesNotificationPreferences)
        val _existingNotificationPreferences: TableInfo = read(connection,
            "notification_preferences")
        if (!_infoNotificationPreferences.equals(_existingNotificationPreferences)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notification_preferences(edu.lespri.app.data.local.entity.NotificationPreferenceEntity).
              | Expected:
              |""".trimMargin() + _infoNotificationPreferences + """
              |
              | Found:
              |""".trimMargin() + _existingNotificationPreferences)
        }
        val _columnsAppSettings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAppSettings.put("setting_key", TableInfo.Column("setting_key", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("setting_value", TableInfo.Column("setting_value", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAppSettings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAppSettings: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAppSettings: TableInfo = TableInfo("app_settings", _columnsAppSettings,
            _foreignKeysAppSettings, _indicesAppSettings)
        val _existingAppSettings: TableInfo = read(connection, "app_settings")
        if (!_infoAppSettings.equals(_existingAppSettings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |app_settings(edu.lespri.app.data.local.entity.AppSettingEntity).
              | Expected:
              |""".trimMargin() + _infoAppSettings + """
              |
              | Found:
              |""".trimMargin() + _existingAppSettings)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "owner_profile", "students",
        "tutors", "class_groups", "enrollments", "schedule_rules", "lesson_sessions",
        "attendance_records", "charges", "payments", "package_ledgers", "notification_preferences",
        "app_settings")
  }

  public override fun clearAllTables() {
    super.performClear(true, "owner_profile", "students", "tutors", "attendance_records",
        "payments", "charges", "enrollments", "class_groups", "schedule_rules", "lesson_sessions",
        "package_ledgers", "notification_preferences", "app_settings")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(StudentDao::class, StudentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TutorDao::class, TutorDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ClassDao::class, ClassDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ScheduleDao::class, ScheduleDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AttendanceDao::class, AttendanceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FinanceDao::class, FinanceDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun studentDao(): StudentDao = _studentDao.value

  public override fun tutorDao(): TutorDao = _tutorDao.value

  public override fun classDao(): ClassDao = _classDao.value

  public override fun scheduleDao(): ScheduleDao = _scheduleDao.value

  public override fun attendanceDao(): AttendanceDao = _attendanceDao.value

  public override fun financeDao(): FinanceDao = _financeDao.value
}
