package edu.lespri.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.TutorEntity
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
public class TutorDao_Impl(
  __db: RoomDatabase,
) : TutorDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTutorEntity: EntityInsertAdapter<TutorEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTutorEntity = object : EntityInsertAdapter<TutorEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `tutors` (`id`,`name`,`phone`,`expertise`,`notes`,`status`,`created_at`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TutorEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpPhone: String? = entity.phone
        if (_tmpPhone == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpPhone)
        }
        val _tmpExpertise: String? = entity.expertise
        if (_tmpExpertise == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpExpertise)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotes)
        }
        statement.bindText(6, entity.status)
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun upsertTutor(tutor: TutorEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfTutorEntity.insert(_connection, tutor)
  }

  public override fun getTutorsByStatus(status: String): Flow<List<TutorEntity>> {
    val _sql: String = "SELECT * FROM tutors WHERE status = ? ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("tutors")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfExpertise: Int = getColumnIndexOrThrow(_stmt, "expertise")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<TutorEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TutorEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String?
          if (_stmt.isNull(_columnIndexOfPhone)) {
            _tmpPhone = null
          } else {
            _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          }
          val _tmpExpertise: String?
          if (_stmt.isNull(_columnIndexOfExpertise)) {
            _tmpExpertise = null
          } else {
            _tmpExpertise = _stmt.getText(_columnIndexOfExpertise)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              TutorEntity(_tmpId,_tmpName,_tmpPhone,_tmpExpertise,_tmpNotes,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTutorById(id: String): Flow<TutorEntity?> {
    val _sql: String = "SELECT * FROM tutors WHERE id = ?"
    return createFlow(__db, false, arrayOf("tutors")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfExpertise: Int = getColumnIndexOrThrow(_stmt, "expertise")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: TutorEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String?
          if (_stmt.isNull(_columnIndexOfPhone)) {
            _tmpPhone = null
          } else {
            _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          }
          val _tmpExpertise: String?
          if (_stmt.isNull(_columnIndexOfExpertise)) {
            _tmpExpertise = null
          } else {
            _tmpExpertise = _stmt.getText(_columnIndexOfExpertise)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              TutorEntity(_tmpId,_tmpName,_tmpPhone,_tmpExpertise,_tmpNotes,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllTutorsForBackup(): List<TutorEntity> {
    val _sql: String =
        "SELECT `tutors`.`id` AS `id`, `tutors`.`name` AS `name`, `tutors`.`phone` AS `phone`, `tutors`.`expertise` AS `expertise`, `tutors`.`notes` AS `notes`, `tutors`.`status` AS `status`, `tutors`.`created_at` AS `created_at` FROM tutors ORDER BY created_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfPhone: Int = 2
        val _columnIndexOfExpertise: Int = 3
        val _columnIndexOfNotes: Int = 4
        val _columnIndexOfStatus: Int = 5
        val _columnIndexOfCreatedAt: Int = 6
        val _result: MutableList<TutorEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TutorEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String?
          if (_stmt.isNull(_columnIndexOfPhone)) {
            _tmpPhone = null
          } else {
            _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          }
          val _tmpExpertise: String?
          if (_stmt.isNull(_columnIndexOfExpertise)) {
            _tmpExpertise = null
          } else {
            _tmpExpertise = _stmt.getText(_columnIndexOfExpertise)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              TutorEntity(_tmpId,_tmpName,_tmpPhone,_tmpExpertise,_tmpNotes,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun archiveTutor(id: String) {
    val _sql: String = "UPDATE tutors SET status = 'ARCHIVED' WHERE id = ?"
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
