package edu.lespri.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.LessonSessionEntity
import edu.lespri.app.`data`.local.entity.ScheduleRuleEntity
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
public class ScheduleDao_Impl(
  __db: RoomDatabase,
) : ScheduleDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfScheduleRuleEntity: EntityInsertAdapter<ScheduleRuleEntity>

  private val __insertAdapterOfLessonSessionEntity: EntityInsertAdapter<LessonSessionEntity>

  private val __insertAdapterOfLessonSessionEntity_1: EntityInsertAdapter<LessonSessionEntity>

  private val __updateAdapterOfLessonSessionEntity: EntityDeleteOrUpdateAdapter<LessonSessionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfScheduleRuleEntity = object : EntityInsertAdapter<ScheduleRuleEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `schedule_rules` (`id`,`class_id`,`tutor_id`,`weekday`,`start_time`,`duration_minutes`,`effective_from`,`effective_until`,`status`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ScheduleRuleEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.classId)
        val _tmpTutorId: String? = entity.tutorId
        if (_tmpTutorId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTutorId)
        }
        statement.bindLong(4, entity.weekday.toLong())
        statement.bindText(5, entity.startTime)
        statement.bindLong(6, entity.durationMinutes.toLong())
        statement.bindText(7, entity.effectiveFrom)
        val _tmpEffectiveUntil: String? = entity.effectiveUntil
        if (_tmpEffectiveUntil == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpEffectiveUntil)
        }
        statement.bindText(9, entity.status)
      }
    }
    this.__insertAdapterOfLessonSessionEntity = object : EntityInsertAdapter<LessonSessionEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `lesson_sessions` (`id`,`class_id`,`tutor_id`,`session_date`,`start_time`,`duration_minutes`,`status`,`source_rule_id`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LessonSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.classId)
        val _tmpTutorId: String? = entity.tutorId
        if (_tmpTutorId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTutorId)
        }
        statement.bindText(4, entity.sessionDate)
        statement.bindText(5, entity.startTime)
        statement.bindLong(6, entity.durationMinutes.toLong())
        statement.bindText(7, entity.status)
        val _tmpSourceRuleId: String? = entity.sourceRuleId
        if (_tmpSourceRuleId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSourceRuleId)
        }
      }
    }
    this.__insertAdapterOfLessonSessionEntity_1 = object :
        EntityInsertAdapter<LessonSessionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `lesson_sessions` (`id`,`class_id`,`tutor_id`,`session_date`,`start_time`,`duration_minutes`,`status`,`source_rule_id`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LessonSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.classId)
        val _tmpTutorId: String? = entity.tutorId
        if (_tmpTutorId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTutorId)
        }
        statement.bindText(4, entity.sessionDate)
        statement.bindText(5, entity.startTime)
        statement.bindLong(6, entity.durationMinutes.toLong())
        statement.bindText(7, entity.status)
        val _tmpSourceRuleId: String? = entity.sourceRuleId
        if (_tmpSourceRuleId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSourceRuleId)
        }
      }
    }
    this.__updateAdapterOfLessonSessionEntity = object :
        EntityDeleteOrUpdateAdapter<LessonSessionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `lesson_sessions` SET `id` = ?,`class_id` = ?,`tutor_id` = ?,`session_date` = ?,`start_time` = ?,`duration_minutes` = ?,`status` = ?,`source_rule_id` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: LessonSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.classId)
        val _tmpTutorId: String? = entity.tutorId
        if (_tmpTutorId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTutorId)
        }
        statement.bindText(4, entity.sessionDate)
        statement.bindText(5, entity.startTime)
        statement.bindLong(6, entity.durationMinutes.toLong())
        statement.bindText(7, entity.status)
        val _tmpSourceRuleId: String? = entity.sourceRuleId
        if (_tmpSourceRuleId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSourceRuleId)
        }
        statement.bindText(9, entity.id)
      }
    }
  }

  public override suspend fun upsertScheduleRule(rule: ScheduleRuleEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfScheduleRuleEntity.insert(_connection, rule)
  }

  public override suspend fun insertSessions(sessions: List<LessonSessionEntity>): List<Long> =
      performSuspending(__db, false, true) { _connection ->
    val _result: List<Long> =
        __insertAdapterOfLessonSessionEntity.insertAndReturnIdsList(_connection, sessions)
    _result
  }

  public override suspend fun insertAdHocSession(session: LessonSessionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfLessonSessionEntity_1.insert(_connection, session)
  }

  public override suspend fun updateSession(session: LessonSessionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfLessonSessionEntity.handle(_connection, session)
  }

  public override fun getActiveRulesForClass(classId: String): Flow<List<ScheduleRuleEntity>> {
    val _sql: String = "SELECT * FROM schedule_rules WHERE class_id = ? AND status = 'ACTIVE'"
    return createFlow(__db, false, arrayOf("schedule_rules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, classId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfWeekday: Int = getColumnIndexOrThrow(_stmt, "weekday")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "start_time")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "duration_minutes")
        val _columnIndexOfEffectiveFrom: Int = getColumnIndexOrThrow(_stmt, "effective_from")
        val _columnIndexOfEffectiveUntil: Int = getColumnIndexOrThrow(_stmt, "effective_until")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<ScheduleRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ScheduleRuleEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpWeekday: Int
          _tmpWeekday = _stmt.getLong(_columnIndexOfWeekday).toInt()
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpEffectiveFrom: String
          _tmpEffectiveFrom = _stmt.getText(_columnIndexOfEffectiveFrom)
          val _tmpEffectiveUntil: String?
          if (_stmt.isNull(_columnIndexOfEffectiveUntil)) {
            _tmpEffectiveUntil = null
          } else {
            _tmpEffectiveUntil = _stmt.getText(_columnIndexOfEffectiveUntil)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              ScheduleRuleEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpWeekday,_tmpStartTime,_tmpDurationMinutes,_tmpEffectiveFrom,_tmpEffectiveUntil,_tmpStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllActiveRules(): List<ScheduleRuleEntity> {
    val _sql: String =
        "SELECT `schedule_rules`.`id` AS `id`, `schedule_rules`.`class_id` AS `class_id`, `schedule_rules`.`tutor_id` AS `tutor_id`, `schedule_rules`.`weekday` AS `weekday`, `schedule_rules`.`start_time` AS `start_time`, `schedule_rules`.`duration_minutes` AS `duration_minutes`, `schedule_rules`.`effective_from` AS `effective_from`, `schedule_rules`.`effective_until` AS `effective_until`, `schedule_rules`.`status` AS `status` FROM schedule_rules WHERE status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfClassId: Int = 1
        val _columnIndexOfTutorId: Int = 2
        val _columnIndexOfWeekday: Int = 3
        val _columnIndexOfStartTime: Int = 4
        val _columnIndexOfDurationMinutes: Int = 5
        val _columnIndexOfEffectiveFrom: Int = 6
        val _columnIndexOfEffectiveUntil: Int = 7
        val _columnIndexOfStatus: Int = 8
        val _result: MutableList<ScheduleRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ScheduleRuleEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpWeekday: Int
          _tmpWeekday = _stmt.getLong(_columnIndexOfWeekday).toInt()
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpEffectiveFrom: String
          _tmpEffectiveFrom = _stmt.getText(_columnIndexOfEffectiveFrom)
          val _tmpEffectiveUntil: String?
          if (_stmt.isNull(_columnIndexOfEffectiveUntil)) {
            _tmpEffectiveUntil = null
          } else {
            _tmpEffectiveUntil = _stmt.getText(_columnIndexOfEffectiveUntil)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              ScheduleRuleEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpWeekday,_tmpStartTime,_tmpDurationMinutes,_tmpEffectiveFrom,_tmpEffectiveUntil,_tmpStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSessionsForDate(date: String): Flow<List<LessonSessionEntity>> {
    val _sql: String =
        "SELECT * FROM lesson_sessions WHERE session_date = ? ORDER BY start_time ASC"
    return createFlow(__db, false, arrayOf("lesson_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfSessionDate: Int = getColumnIndexOrThrow(_stmt, "session_date")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "start_time")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "duration_minutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSourceRuleId: Int = getColumnIndexOrThrow(_stmt, "source_rule_id")
        val _result: MutableList<LessonSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LessonSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpSessionDate: String
          _tmpSessionDate = _stmt.getText(_columnIndexOfSessionDate)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSourceRuleId: String?
          if (_stmt.isNull(_columnIndexOfSourceRuleId)) {
            _tmpSourceRuleId = null
          } else {
            _tmpSourceRuleId = _stmt.getText(_columnIndexOfSourceRuleId)
          }
          _item =
              LessonSessionEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpSessionDate,_tmpStartTime,_tmpDurationMinutes,_tmpStatus,_tmpSourceRuleId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSessionById(sessionId: String): Flow<LessonSessionEntity?> {
    val _sql: String = "SELECT * FROM lesson_sessions WHERE id = ?"
    return createFlow(__db, false, arrayOf("lesson_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfSessionDate: Int = getColumnIndexOrThrow(_stmt, "session_date")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "start_time")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "duration_minutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSourceRuleId: Int = getColumnIndexOrThrow(_stmt, "source_rule_id")
        val _result: LessonSessionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpSessionDate: String
          _tmpSessionDate = _stmt.getText(_columnIndexOfSessionDate)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSourceRuleId: String?
          if (_stmt.isNull(_columnIndexOfSourceRuleId)) {
            _tmpSourceRuleId = null
          } else {
            _tmpSourceRuleId = _stmt.getText(_columnIndexOfSourceRuleId)
          }
          _result =
              LessonSessionEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpSessionDate,_tmpStartTime,_tmpDurationMinutes,_tmpStatus,_tmpSourceRuleId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSessionByIdSync(sessionId: String): LessonSessionEntity? {
    val _sql: String = "SELECT * FROM lesson_sessions WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfSessionDate: Int = getColumnIndexOrThrow(_stmt, "session_date")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "start_time")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "duration_minutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSourceRuleId: Int = getColumnIndexOrThrow(_stmt, "source_rule_id")
        val _result: LessonSessionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpSessionDate: String
          _tmpSessionDate = _stmt.getText(_columnIndexOfSessionDate)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSourceRuleId: String?
          if (_stmt.isNull(_columnIndexOfSourceRuleId)) {
            _tmpSourceRuleId = null
          } else {
            _tmpSourceRuleId = _stmt.getText(_columnIndexOfSourceRuleId)
          }
          _result =
              LessonSessionEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpSessionDate,_tmpStartTime,_tmpDurationMinutes,_tmpStatus,_tmpSourceRuleId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSessionsInRange(startDate: String, endDate: String):
      Flow<List<LessonSessionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM lesson_sessions 
        |        WHERE session_date >= ? AND session_date <= ? 
        |        ORDER BY session_date ASC, start_time ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("lesson_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, startDate)
        _argIndex = 2
        _stmt.bindText(_argIndex, endDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClassId: Int = getColumnIndexOrThrow(_stmt, "class_id")
        val _columnIndexOfTutorId: Int = getColumnIndexOrThrow(_stmt, "tutor_id")
        val _columnIndexOfSessionDate: Int = getColumnIndexOrThrow(_stmt, "session_date")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "start_time")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "duration_minutes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSourceRuleId: Int = getColumnIndexOrThrow(_stmt, "source_rule_id")
        val _result: MutableList<LessonSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LessonSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpSessionDate: String
          _tmpSessionDate = _stmt.getText(_columnIndexOfSessionDate)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSourceRuleId: String?
          if (_stmt.isNull(_columnIndexOfSourceRuleId)) {
            _tmpSourceRuleId = null
          } else {
            _tmpSourceRuleId = _stmt.getText(_columnIndexOfSourceRuleId)
          }
          _item =
              LessonSessionEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpSessionDate,_tmpStartTime,_tmpDurationMinutes,_tmpStatus,_tmpSourceRuleId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSessionsForBackup(): List<LessonSessionEntity> {
    val _sql: String =
        "SELECT `lesson_sessions`.`id` AS `id`, `lesson_sessions`.`class_id` AS `class_id`, `lesson_sessions`.`tutor_id` AS `tutor_id`, `lesson_sessions`.`session_date` AS `session_date`, `lesson_sessions`.`start_time` AS `start_time`, `lesson_sessions`.`duration_minutes` AS `duration_minutes`, `lesson_sessions`.`status` AS `status`, `lesson_sessions`.`source_rule_id` AS `source_rule_id` FROM lesson_sessions ORDER BY session_date ASC, start_time ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfClassId: Int = 1
        val _columnIndexOfTutorId: Int = 2
        val _columnIndexOfSessionDate: Int = 3
        val _columnIndexOfStartTime: Int = 4
        val _columnIndexOfDurationMinutes: Int = 5
        val _columnIndexOfStatus: Int = 6
        val _columnIndexOfSourceRuleId: Int = 7
        val _result: MutableList<LessonSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LessonSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClassId: String
          _tmpClassId = _stmt.getText(_columnIndexOfClassId)
          val _tmpTutorId: String?
          if (_stmt.isNull(_columnIndexOfTutorId)) {
            _tmpTutorId = null
          } else {
            _tmpTutorId = _stmt.getText(_columnIndexOfTutorId)
          }
          val _tmpSessionDate: String
          _tmpSessionDate = _stmt.getText(_columnIndexOfSessionDate)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSourceRuleId: String?
          if (_stmt.isNull(_columnIndexOfSourceRuleId)) {
            _tmpSourceRuleId = null
          } else {
            _tmpSourceRuleId = _stmt.getText(_columnIndexOfSourceRuleId)
          }
          _item =
              LessonSessionEntity(_tmpId,_tmpClassId,_tmpTutorId,_tmpSessionDate,_tmpStartTime,_tmpDurationMinutes,_tmpStatus,_tmpSourceRuleId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deactivateRule(ruleId: String) {
    val _sql: String = "UPDATE schedule_rules SET status = 'INACTIVE' WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ruleId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cancelSession(sessionId: String) {
    val _sql: String = "UPDATE lesson_sessions SET status = 'CANCELLED' WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
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
