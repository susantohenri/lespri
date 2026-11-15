package edu.lespri.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.StudentEntity
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
public class StudentDao_Impl(
  __db: RoomDatabase,
) : StudentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStudentEntity: EntityInsertAdapter<StudentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStudentEntity = object : EntityInsertAdapter<StudentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `students` (`id`,`name`,`guardian_name`,`guardian_phone`,`school_grade`,`notes`,`status`,`joined_at`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StudentEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpGuardianName: String? = entity.guardianName
        if (_tmpGuardianName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpGuardianName)
        }
        val _tmpGuardianPhone: String? = entity.guardianPhone
        if (_tmpGuardianPhone == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpGuardianPhone)
        }
        val _tmpSchoolGrade: String? = entity.schoolGrade
        if (_tmpSchoolGrade == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSchoolGrade)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpNotes)
        }
        statement.bindText(7, entity.status)
        statement.bindLong(8, entity.joinedAt)
      }
    }
  }

  public override suspend fun upsertStudent(student: StudentEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfStudentEntity.insert(_connection, student)
  }

  public override suspend fun insertStudentsBatch(students: List<StudentEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfStudentEntity.insert(_connection, students)
  }

  public override fun getStudentsByStatus(status: String): Flow<List<StudentEntity>> {
    val _sql: String = "SELECT * FROM students WHERE status = ? ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("students")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGuardianName: Int = getColumnIndexOrThrow(_stmt, "guardian_name")
        val _columnIndexOfGuardianPhone: Int = getColumnIndexOrThrow(_stmt, "guardian_phone")
        val _columnIndexOfSchoolGrade: Int = getColumnIndexOrThrow(_stmt, "school_grade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfJoinedAt: Int = getColumnIndexOrThrow(_stmt, "joined_at")
        val _result: MutableList<StudentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StudentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGuardianName: String?
          if (_stmt.isNull(_columnIndexOfGuardianName)) {
            _tmpGuardianName = null
          } else {
            _tmpGuardianName = _stmt.getText(_columnIndexOfGuardianName)
          }
          val _tmpGuardianPhone: String?
          if (_stmt.isNull(_columnIndexOfGuardianPhone)) {
            _tmpGuardianPhone = null
          } else {
            _tmpGuardianPhone = _stmt.getText(_columnIndexOfGuardianPhone)
          }
          val _tmpSchoolGrade: String?
          if (_stmt.isNull(_columnIndexOfSchoolGrade)) {
            _tmpSchoolGrade = null
          } else {
            _tmpSchoolGrade = _stmt.getText(_columnIndexOfSchoolGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _item =
              StudentEntity(_tmpId,_tmpName,_tmpGuardianName,_tmpGuardianPhone,_tmpSchoolGrade,_tmpNotes,_tmpStatus,_tmpJoinedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getStudentById(id: String): Flow<StudentEntity?> {
    val _sql: String = "SELECT * FROM students WHERE id = ?"
    return createFlow(__db, false, arrayOf("students")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGuardianName: Int = getColumnIndexOrThrow(_stmt, "guardian_name")
        val _columnIndexOfGuardianPhone: Int = getColumnIndexOrThrow(_stmt, "guardian_phone")
        val _columnIndexOfSchoolGrade: Int = getColumnIndexOrThrow(_stmt, "school_grade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfJoinedAt: Int = getColumnIndexOrThrow(_stmt, "joined_at")
        val _result: StudentEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGuardianName: String?
          if (_stmt.isNull(_columnIndexOfGuardianName)) {
            _tmpGuardianName = null
          } else {
            _tmpGuardianName = _stmt.getText(_columnIndexOfGuardianName)
          }
          val _tmpGuardianPhone: String?
          if (_stmt.isNull(_columnIndexOfGuardianPhone)) {
            _tmpGuardianPhone = null
          } else {
            _tmpGuardianPhone = _stmt.getText(_columnIndexOfGuardianPhone)
          }
          val _tmpSchoolGrade: String?
          if (_stmt.isNull(_columnIndexOfSchoolGrade)) {
            _tmpSchoolGrade = null
          } else {
            _tmpSchoolGrade = _stmt.getText(_columnIndexOfSchoolGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _result =
              StudentEntity(_tmpId,_tmpName,_tmpGuardianName,_tmpGuardianPhone,_tmpSchoolGrade,_tmpNotes,_tmpStatus,_tmpJoinedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchStudents(query: String): Flow<List<StudentEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM students 
        |        WHERE status = 'ACTIVE' 
        |        AND (name LIKE '%' || ? || '%' OR guardian_phone LIKE '%' || ? || '%')
        |        ORDER BY name ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("students")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGuardianName: Int = getColumnIndexOrThrow(_stmt, "guardian_name")
        val _columnIndexOfGuardianPhone: Int = getColumnIndexOrThrow(_stmt, "guardian_phone")
        val _columnIndexOfSchoolGrade: Int = getColumnIndexOrThrow(_stmt, "school_grade")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfJoinedAt: Int = getColumnIndexOrThrow(_stmt, "joined_at")
        val _result: MutableList<StudentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StudentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGuardianName: String?
          if (_stmt.isNull(_columnIndexOfGuardianName)) {
            _tmpGuardianName = null
          } else {
            _tmpGuardianName = _stmt.getText(_columnIndexOfGuardianName)
          }
          val _tmpGuardianPhone: String?
          if (_stmt.isNull(_columnIndexOfGuardianPhone)) {
            _tmpGuardianPhone = null
          } else {
            _tmpGuardianPhone = _stmt.getText(_columnIndexOfGuardianPhone)
          }
          val _tmpSchoolGrade: String?
          if (_stmt.isNull(_columnIndexOfSchoolGrade)) {
            _tmpSchoolGrade = null
          } else {
            _tmpSchoolGrade = _stmt.getText(_columnIndexOfSchoolGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _item =
              StudentEntity(_tmpId,_tmpName,_tmpGuardianName,_tmpGuardianPhone,_tmpSchoolGrade,_tmpNotes,_tmpStatus,_tmpJoinedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllStudentsForBackup(): List<StudentEntity> {
    val _sql: String =
        "SELECT `students`.`id` AS `id`, `students`.`name` AS `name`, `students`.`guardian_name` AS `guardian_name`, `students`.`guardian_phone` AS `guardian_phone`, `students`.`school_grade` AS `school_grade`, `students`.`notes` AS `notes`, `students`.`status` AS `status`, `students`.`joined_at` AS `joined_at` FROM students ORDER BY joined_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfGuardianName: Int = 2
        val _columnIndexOfGuardianPhone: Int = 3
        val _columnIndexOfSchoolGrade: Int = 4
        val _columnIndexOfNotes: Int = 5
        val _columnIndexOfStatus: Int = 6
        val _columnIndexOfJoinedAt: Int = 7
        val _result: MutableList<StudentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StudentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGuardianName: String?
          if (_stmt.isNull(_columnIndexOfGuardianName)) {
            _tmpGuardianName = null
          } else {
            _tmpGuardianName = _stmt.getText(_columnIndexOfGuardianName)
          }
          val _tmpGuardianPhone: String?
          if (_stmt.isNull(_columnIndexOfGuardianPhone)) {
            _tmpGuardianPhone = null
          } else {
            _tmpGuardianPhone = _stmt.getText(_columnIndexOfGuardianPhone)
          }
          val _tmpSchoolGrade: String?
          if (_stmt.isNull(_columnIndexOfSchoolGrade)) {
            _tmpSchoolGrade = null
          } else {
            _tmpSchoolGrade = _stmt.getText(_columnIndexOfSchoolGrade)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _stmt.getLong(_columnIndexOfJoinedAt)
          _item =
              StudentEntity(_tmpId,_tmpName,_tmpGuardianName,_tmpGuardianPhone,_tmpSchoolGrade,_tmpNotes,_tmpStatus,_tmpJoinedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun archiveStudent(id: String) {
    val _sql: String = "UPDATE students SET status = 'ARCHIVED' WHERE id = ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
