package edu.lespri.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.ClassGroupEntity
import edu.lespri.app.`data`.local.entity.EnrollmentEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ClassDao_Impl(
  __db: RoomDatabase,
) : ClassDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfClassGroupEntity: EntityInsertAdapter<ClassGroupEntity>

  private val __insertAdapterOfEnrollmentEntity: EntityInsertAdapter<EnrollmentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfClassGroupEntity = object : EntityInsertAdapter<ClassGroupEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `class_groups` (`id`,`name`,`class_type`,`subject`,`tutor_id`,`location_note`,`status`,`created_at`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ClassGroupEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.classType)
        val _tmpSubject: String? = entity.subject
        if (_tmpSubject == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSubject)
        }
        val _tmpTutorId: String? = entity.tutorId
        if (_tmpTutorId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpTutorId)
        }
        val _tmpLocationNote: String? = entity.locationNote
        if (_tmpLocationNote == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpLocationNote)
        }
        statement.bindText(7, entity.status)
        statement.bindLong(8, entity.createdAt)
      }
    }
    this.__insertAdapterOfEnrollmentEntity = object : EntityInsertAdapter<EnrollmentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `enrollments` (`id`,`student_id`,`class_id`,`billing_type`,`fee_amount`,`due_day`,`package_session_count`,`status`,`created_at`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EnrollmentEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.studentId)
        statement.bindText(3, entity.classId)
        statement.bindText(4, entity.billingType)
        statement.bindLong(5, entity.feeAmount)
        val _tmpDueDay: Int? = entity.dueDay
        if (_tmpDueDay == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpDueDay.toLong())
        }
        val _tmpPackageSessionCount: Int? = entity.packageSessionCount
        if (_tmpPackageSessionCount == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpPackageSessionCount.toLong())
        }
        statement.bindText(8, entity.status)
        statement.bindLong(9, entity.createdAt)
      }
    }
  }

  public override suspend fun upsertClass(classGroup: ClassGroupEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfClassGroupEntity.insert(_connection, classGroup)
  }

  public override suspend fun insertEnrollment(enrollment: EnrollmentEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEnrollmentEntity.insert(_connection, enrollment)
  }

  public override fun getClassesByStatus(status: String): Flow<List<ClassGroupEntity>> {
    val _sql: String = "SELECT * FROM class_groups WHERE status = ? ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("class_groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfClassType: Int = getColumnIndexOrThrow(_stmt, "class_type")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfLocationNote: Int = getColumnIndexOrThrow(_stmt, "location_note")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<ClassGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClassGroupEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpClassType: String
          _tmpClassType = _stmt.getText(_columnIndexOfClassType)
          val _tmpSubject: String?
          if (_stmt.isNull(_columnIndexOfSubject)) {
            _tmpSubject = null
          } else {
            _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          }
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpLocationNote: String?
          if (_stmt.isNull(_columnIndexOfLocationNote)) {
            _tmpLocationNote = null
          } else {
            _tmpLocationNote = _stmt.getText(_columnIndexOfLocationNote)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ClassGroupEntity(_tmpId,_tmpName,_tmpClassType,_tmpSubject,_tmpTutorId,_tmpLocationNote,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getClassById(id: String): Flow<ClassGroupEntity?> {
    val _sql: String = "SELECT * FROM class_groups WHERE id = ?"
    return createFlow(__db, false, arrayOf("class_groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfClassType: Int = getColumnIndexOrThrow(_stmt, "class_type")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfLocationNote: Int = getColumnIndexOrThrow(_stmt, "location_note")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: ClassGroupEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpClassType: String
          _tmpClassType = _stmt.getText(_columnIndexOfClassType)
          val _tmpSubject: String?
          if (_stmt.isNull(_columnIndexOfSubject)) {
            _tmpSubject = null
          } else {
            _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          }
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpLocationNote: String?
          if (_stmt.isNull(_columnIndexOfLocationNote)) {
            _tmpLocationNote = null
          } else {
            _tmpLocationNote = _stmt.getText(_columnIndexOfLocationNote)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              ClassGroupEntity(_tmpId,_tmpName,_tmpClassType,_tmpSubject,_tmpTutorId,_tmpLocationNote,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllClassesForBackup(): List<ClassGroupEntity> {
    val _sql: String =
        "SELECT `class_groups`.`id` AS `id`, `class_groups`.`name` AS `name`, `class_groups`.`class_type` AS `class_type`, `class_groups`.`subject` AS `subject`, `class_groups`.`tutor_id` AS `tutor_id`, `class_groups`.`location_note` AS `location_note`, `class_groups`.`status` AS `status`, `class_groups`.`created_at` AS `created_at` FROM class_groups ORDER BY created_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfClassType: Int = 2
        val _columnIndexOfSubject: Int = 3
        val _columnIndexOfTutorId: Int = 4
        val _columnIndexOfLocationNote: Int = 5
        val _columnIndexOfStatus: Int = 6
        val _columnIndexOfCreatedAt: Int = 7
        val _result: MutableList<ClassGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClassGroupEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpClassType: String
          _tmpClassType = _stmt.getText(_columnIndexOfClassType)
          val _tmpSubject: String?
          if (_stmt.isNull(_columnIndexOfSubject)) {
            _tmpSubject = null
          } else {
            _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          }
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpLocationNote: String?
          if (_stmt.isNull(_columnIndexOfLocationNote)) {
            _tmpLocationNote = null
          } else {
            _tmpLocationNote = _stmt.getText(_columnIndexOfLocationNote)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ClassGroupEntity(_tmpId,_tmpName,_tmpClassType,_tmpSubject,_tmpTutorId,_tmpLocationNote,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getActiveEnrollmentsForClass(classId: String): Flow<List<EnrollmentEntity>> {
    val _sql: String = "SELECT * FROM enrollments WHERE class_id = ? AND status = 'ACTIVE'"
    return createFlow(__db, false, arrayOf("enrollments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, classId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStudentId: Int = getColumnIndexOrThrow(_stmt, "student_id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfBillingType: Int = getColumnIndexOrThrow(_stmt, "billing_type")
        val _columnIndexOfFeeAmount: Int = getColumnIndexOrThrow(_stmt, "fee_amount")
        val _columnIndexOfDueDay: Int = getColumnIndexOrThrow(_stmt, "due_day")
        val _columnIndexOfPackageSessionCount: Int = getColumnIndexOrThrow(_stmt,
            "package_session_count")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<EnrollmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnrollmentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStudentId: String
          _tmpStudentId = _stmt.getText(_columnIndexOfStudentId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpBillingType: String
          _tmpBillingType = _stmt.getText(_columnIndexOfBillingType)
          val _tmpFeeAmount: Long
          _tmpFeeAmount = _stmt.getLong(_columnIndexOfFeeAmount)
          val _tmpDueDay: Int?
          if (_stmt.isNull(_columnIndexOfDueDay)) {
            _tmpDueDay = null
          } else {
            _tmpDueDay = _stmt.getLong(_columnIndexOfDueDay).toInt()
          }
          val _tmpPackageSessionCount: Int?
          if (_stmt.isNull(_columnIndexOfPackageSessionCount)) {
            _tmpPackageSessionCount = null
          } else {
            _tmpPackageSessionCount = _stmt.getLong(_columnIndexOfPackageSessionCount).toInt()
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              EnrollmentEntity(_tmpId,_tmpStudentId,_tmpClassId,_tmpBillingType,_tmpFeeAmount,_tmpDueDay,_tmpPackageSessionCount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getActiveEnrollmentsForStudent(studentId: String):
      Flow<List<EnrollmentEntity>> {
    val _sql: String = "SELECT * FROM enrollments WHERE student_id = ? AND status = 'ACTIVE'"
    return createFlow(__db, false, arrayOf("enrollments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, studentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStudentId: Int = getColumnIndexOrThrow(_stmt, "student_id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfBillingType: Int = getColumnIndexOrThrow(_stmt, "billing_type")
        val _columnIndexOfFeeAmount: Int = getColumnIndexOrThrow(_stmt, "fee_amount")
        val _columnIndexOfDueDay: Int = getColumnIndexOrThrow(_stmt, "due_day")
        val _columnIndexOfPackageSessionCount: Int = getColumnIndexOrThrow(_stmt,
            "package_session_count")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<EnrollmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnrollmentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStudentId: String
          _tmpStudentId = _stmt.getText(_columnIndexOfStudentId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpBillingType: String
          _tmpBillingType = _stmt.getText(_columnIndexOfBillingType)
          val _tmpFeeAmount: Long
          _tmpFeeAmount = _stmt.getLong(_columnIndexOfFeeAmount)
          val _tmpDueDay: Int?
          if (_stmt.isNull(_columnIndexOfDueDay)) {
            _tmpDueDay = null
          } else {
            _tmpDueDay = _stmt.getLong(_columnIndexOfDueDay).toInt()
          }
          val _tmpPackageSessionCount: Int?
          if (_stmt.isNull(_columnIndexOfPackageSessionCount)) {
            _tmpPackageSessionCount = null
          } else {
            _tmpPackageSessionCount = _stmt.getLong(_columnIndexOfPackageSessionCount).toInt()
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              EnrollmentEntity(_tmpId,_tmpStudentId,_tmpClassId,_tmpBillingType,_tmpFeeAmount,_tmpDueDay,_tmpPackageSessionCount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEnrollmentById(enrollmentId: String): EnrollmentEntity? {
    val _sql: String = "SELECT * FROM enrollments WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStudentId: Int = getColumnIndexOrThrow(_stmt, "student_id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfBillingType: Int = getColumnIndexOrThrow(_stmt, "billing_type")
        val _columnIndexOfFeeAmount: Int = getColumnIndexOrThrow(_stmt, "fee_amount")
        val _columnIndexOfDueDay: Int = getColumnIndexOrThrow(_stmt, "due_day")
        val _columnIndexOfPackageSessionCount: Int = getColumnIndexOrThrow(_stmt,
            "package_session_count")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: EnrollmentEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStudentId: String
          _tmpStudentId = _stmt.getText(_columnIndexOfStudentId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpBillingType: String
          _tmpBillingType = _stmt.getText(_columnIndexOfBillingType)
          val _tmpFeeAmount: Long
          _tmpFeeAmount = _stmt.getLong(_columnIndexOfFeeAmount)
          val _tmpDueDay: Int?
          if (_stmt.isNull(_columnIndexOfDueDay)) {
            _tmpDueDay = null
          } else {
            _tmpDueDay = _stmt.getLong(_columnIndexOfDueDay).toInt()
          }
          val _tmpPackageSessionCount: Int?
          if (_stmt.isNull(_columnIndexOfPackageSessionCount)) {
            _tmpPackageSessionCount = null
          } else {
            _tmpPackageSessionCount = _stmt.getLong(_columnIndexOfPackageSessionCount).toInt()
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              EnrollmentEntity(_tmpId,_tmpStudentId,_tmpClassId,_tmpBillingType,_tmpFeeAmount,_tmpDueDay,_tmpPackageSessionCount,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllEnrollmentsForBackup(): List<EnrollmentEntity> {
    val _sql: String =
        "SELECT `enrollments`.`id` AS `id`, `enrollments`.`student_id` AS `student_id`, `enrollments`.`class_id` AS `class_id`, `enrollments`.`billing_type` AS `billing_type`, `enrollments`.`fee_amount` AS `fee_amount`, `enrollments`.`due_day` AS `due_day`, `enrollments`.`package_session_count` AS `package_session_count`, `enrollments`.`status` AS `status`, `enrollments`.`created_at` AS `created_at` FROM enrollments ORDER BY created_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfStudentId: Int = 1
        val _columnIndexOfClassId: Int = 2
        val _columnIndexOfBillingType: Int = 3
        val _columnIndexOfFeeAmount: Int = 4
        val _columnIndexOfDueDay: Int = 5
        val _columnIndexOfPackageSessionCount: Int = 6
        val _columnIndexOfStatus: Int = 7
        val _columnIndexOfCreatedAt: Int = 8
        val _result: MutableList<EnrollmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnrollmentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStudentId: String
          _tmpStudentId = _stmt.getText(_columnIndexOfStudentId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpBillingType: String
          _tmpBillingType = _stmt.getText(_columnIndexOfBillingType)
          val _tmpFeeAmount: Long
          _tmpFeeAmount = _stmt.getLong(_columnIndexOfFeeAmount)
          val _tmpDueDay: Int?
          if (_stmt.isNull(_columnIndexOfDueDay)) {
            _tmpDueDay = null
          } else {
            _tmpDueDay = _stmt.getLong(_columnIndexOfDueDay).toInt()
          }
          val _tmpPackageSessionCount: Int?
          if (_stmt.isNull(_columnIndexOfPackageSessionCount)) {
            _tmpPackageSessionCount = null
          } else {
            _tmpPackageSessionCount = _stmt.getLong(_columnIndexOfPackageSessionCount).toInt()
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              EnrollmentEntity(_tmpId,_tmpStudentId,_tmpClassId,_tmpBillingType,_tmpFeeAmount,_tmpDueDay,_tmpPackageSessionCount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun archiveClass(id: String) {
    val _sql: String = "UPDATE class_groups SET status = 'ARCHIVED' WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateEnrollmentStatus(enrollmentId: String, newStatus: String) {
    val _sql: String = "UPDATE enrollments SET status = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, newStatus)
        _argIndex = 2
        _stmt.bindText(_argIndex, enrollmentId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
