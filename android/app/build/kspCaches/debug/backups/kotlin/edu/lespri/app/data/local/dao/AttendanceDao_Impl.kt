package edu.lespri.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.AttendanceRecordEntity
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
public class AttendanceDao_Impl(
  __db: RoomDatabase,
) : AttendanceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAttendanceRecordEntity: EntityInsertAdapter<AttendanceRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAttendanceRecordEntity = object :
        EntityInsertAdapter<AttendanceRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `attendance_records` (`id`,`session_id`,`enrollment_id`,`status`,`note`,`updated_at`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AttendanceRecordEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionId)
        statement.bindText(3, entity.enrollmentId)
        statement.bindText(4, entity.status)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNote)
        }
        statement.bindLong(6, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsertAttendanceBatch(records: List<AttendanceRecordEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAttendanceRecordEntity.insert(_connection, records)
  }

  public override fun getAttendanceForSession(sessionId: String):
      Flow<List<AttendanceRecordEntity>> {
    val _sql: String = "SELECT * FROM attendance_records WHERE session_id = ?"
    return createFlow(__db, false, arrayOf("attendance_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<AttendanceRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AttendanceRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              AttendanceRecordEntity(_tmpId,_tmpSessionId,_tmpEnrollmentId,_tmpStatus,_tmpNote,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAttendanceForSessionSync(sessionId: String):
      List<AttendanceRecordEntity> {
    val _sql: String = "SELECT * FROM attendance_records WHERE session_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<AttendanceRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AttendanceRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              AttendanceRecordEntity(_tmpId,_tmpSessionId,_tmpEnrollmentId,_tmpStatus,_tmpNote,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAttendanceRecord(sessionId: String, enrollmentId: String):
      AttendanceRecordEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM attendance_records 
        |        WHERE session_id = ? AND enrollment_id = ?
        |        LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        _argIndex = 2
        _stmt.bindText(_argIndex, enrollmentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: AttendanceRecordEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              AttendanceRecordEntity(_tmpId,_tmpSessionId,_tmpEnrollmentId,_tmpStatus,_tmpNote,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAttendanceForEnrollment(enrollmentId: String):
      Flow<List<AttendanceRecordEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM attendance_records 
        |        WHERE enrollment_id = ? 
        |        ORDER BY updated_at DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("attendance_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<AttendanceRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AttendanceRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              AttendanceRecordEntity(_tmpId,_tmpSessionId,_tmpEnrollmentId,_tmpStatus,_tmpNote,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun countUnrecordedSessions(todayDate: String): Flow<Int> {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM lesson_sessions s
        |        LEFT JOIN attendance_records a ON s.id = a.session_id
        |        WHERE s.session_date <= ? 
        |        AND s.status = 'SCHEDULED' 
        |        AND (a.id IS NULL OR a.status = 'UNRECORDED')
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("lesson_sessions", "attendance_records")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, todayDate)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllAttendanceForBackup(): List<AttendanceRecordEntity> {
    val _sql: String =
        "SELECT `attendance_records`.`id` AS `id`, `attendance_records`.`session_id` AS `session_id`, `attendance_records`.`enrollment_id` AS `enrollment_id`, `attendance_records`.`status` AS `status`, `attendance_records`.`note` AS `note`, `attendance_records`.`updated_at` AS `updated_at` FROM attendance_records ORDER BY updated_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfSessionId: Int = 1
        val _columnIndexOfEnrollmentId: Int = 2
        val _columnIndexOfStatus: Int = 3
        val _columnIndexOfNote: Int = 4
        val _columnIndexOfUpdatedAt: Int = 5
        val _result: MutableList<AttendanceRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AttendanceRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              AttendanceRecordEntity(_tmpId,_tmpSessionId,_tmpEnrollmentId,_tmpStatus,_tmpNote,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
