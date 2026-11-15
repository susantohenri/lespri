package edu.lespri.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import edu.lespri.app.`data`.local.entity.ChargeEntity
import edu.lespri.app.`data`.local.entity.PackageLedgerEntity
import edu.lespri.app.`data`.local.entity.PaymentEntity
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
public class FinanceDao_Impl(
  __db: RoomDatabase,
) : FinanceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChargeEntity: EntityInsertAdapter<ChargeEntity>

  private val __insertAdapterOfPaymentEntity: EntityInsertAdapter<PaymentEntity>

  private val __insertAdapterOfPackageLedgerEntity: EntityInsertAdapter<PackageLedgerEntity>

  private val __updateAdapterOfChargeEntity: EntityDeleteOrUpdateAdapter<ChargeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfChargeEntity = object : EntityInsertAdapter<ChargeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `charges` (`id`,`enrollment_id`,`charge_type`,`period_start`,`period_end`,`amount`,`due_date`,`status`,`created_at`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChargeEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.enrollmentId)
        statement.bindText(3, entity.chargeType)
        val _tmpPeriodStart: String? = entity.periodStart
        if (_tmpPeriodStart == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpPeriodStart)
        }
        val _tmpPeriodEnd: String? = entity.periodEnd
        if (_tmpPeriodEnd == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPeriodEnd)
        }
        statement.bindLong(6, entity.amount)
        val _tmpDueDate: String? = entity.dueDate
        if (_tmpDueDate == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDueDate)
        }
        statement.bindText(8, entity.status)
        statement.bindLong(9, entity.createdAt)
      }
    }
    this.__insertAdapterOfPaymentEntity = object : EntityInsertAdapter<PaymentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `payments` (`id`,`charge_id`,`amount`,`paid_at`,`method`,`reference_note`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PaymentEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.chargeId)
        statement.bindLong(3, entity.amount)
        statement.bindLong(4, entity.paidAt)
        statement.bindText(5, entity.method)
        val _tmpReferenceNote: String? = entity.referenceNote
        if (_tmpReferenceNote == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpReferenceNote)
        }
      }
    }
    this.__insertAdapterOfPackageLedgerEntity = object : EntityInsertAdapter<PackageLedgerEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `package_ledgers` (`id`,`enrollment_id`,`session_id`,`delta_sessions`,`reason`,`created_at`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PackageLedgerEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.enrollmentId)
        val _tmpSessionId: String? = entity.sessionId
        if (_tmpSessionId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSessionId)
        }
        statement.bindLong(4, entity.deltaSessions.toLong())
        statement.bindText(5, entity.reason)
        statement.bindLong(6, entity.createdAt)
      }
    }
    this.__updateAdapterOfChargeEntity = object : EntityDeleteOrUpdateAdapter<ChargeEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `charges` SET `id` = ?,`enrollment_id` = ?,`charge_type` = ?,`period_start` = ?,`period_end` = ?,`amount` = ?,`due_date` = ?,`status` = ?,`created_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChargeEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.enrollmentId)
        statement.bindText(3, entity.chargeType)
        val _tmpPeriodStart: String? = entity.periodStart
        if (_tmpPeriodStart == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpPeriodStart)
        }
        val _tmpPeriodEnd: String? = entity.periodEnd
        if (_tmpPeriodEnd == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPeriodEnd)
        }
        statement.bindLong(6, entity.amount)
        val _tmpDueDate: String? = entity.dueDate
        if (_tmpDueDate == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDueDate)
        }
        statement.bindText(8, entity.status)
        statement.bindLong(9, entity.createdAt)
        statement.bindText(10, entity.id)
      }
    }
  }

  public override suspend fun insertCharge(charge: ChargeEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfChargeEntity.insert(_connection, charge)
  }

  public override suspend fun insertPayment(payment: PaymentEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfPaymentEntity.insert(_connection, payment)
  }

  public override suspend fun insertLedgerEntry(entry: PackageLedgerEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPackageLedgerEntity.insert(_connection, entry)
  }

  public override suspend fun updateCharge(charge: ChargeEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfChargeEntity.handle(_connection, charge)
  }

  public override fun getAllActiveCharges(): Flow<List<ChargeEntity>> {
    val _sql: String =
        "SELECT `charges`.`id` AS `id`, `charges`.`enrollment_id` AS `enrollment_id`, `charges`.`charge_type` AS `charge_type`, `charges`.`period_start` AS `period_start`, `charges`.`period_end` AS `period_end`, `charges`.`amount` AS `amount`, `charges`.`due_date` AS `due_date`, `charges`.`status` AS `status`, `charges`.`created_at` AS `created_at` FROM charges WHERE status != 'CANCELLED' ORDER BY created_at DESC"
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfEnrollmentId: Int = 1
        val _columnIndexOfChargeType: Int = 2
        val _columnIndexOfPeriodStart: Int = 3
        val _columnIndexOfPeriodEnd: Int = 4
        val _columnIndexOfAmount: Int = 5
        val _columnIndexOfDueDate: Int = 6
        val _columnIndexOfStatus: Int = 7
        val _columnIndexOfCreatedAt: Int = 8
        val _result: MutableList<ChargeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargeEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getChargesByStatus(status: String): Flow<List<ChargeEntity>> {
    val _sql: String = "SELECT * FROM charges WHERE status = ? ORDER BY created_at DESC"
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfChargeType: Int = getColumnIndexOrThrow(_stmt, "charge_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "due_date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<ChargeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargeEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getChargesForEnrollment(enrollmentId: String): Flow<List<ChargeEntity>> {
    val _sql: String = "SELECT * FROM charges WHERE enrollment_id = ? ORDER BY created_at DESC"
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfChargeType: Int = getColumnIndexOrThrow(_stmt, "charge_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "due_date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<ChargeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargeEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getChargeById(chargeId: String): Flow<ChargeEntity?> {
    val _sql: String = "SELECT * FROM charges WHERE id = ?"
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, chargeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfChargeType: Int = getColumnIndexOrThrow(_stmt, "charge_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "due_date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: ChargeEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getChargeByIdSync(chargeId: String): ChargeEntity? {
    val _sql: String = "SELECT * FROM charges WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, chargeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfChargeType: Int = getColumnIndexOrThrow(_stmt, "charge_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "due_date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: ChargeEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTotalOutstandingDebt(): Flow<Long> {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(amount), 0) FROM charges 
        |        WHERE status IN ('UNPAID', 'PARTIALLY_PAID')
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMonthlyRevenue(monthPrefix: String): Flow<Long> {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(c.amount), 0) FROM charges c
        |        WHERE c.status = 'PAID'
        |        AND substr(c.created_at, 1, 7) = ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("charges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, monthPrefix)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllChargesForBackup(): List<ChargeEntity> {
    val _sql: String =
        "SELECT `charges`.`id` AS `id`, `charges`.`enrollment_id` AS `enrollment_id`, `charges`.`charge_type` AS `charge_type`, `charges`.`period_start` AS `period_start`, `charges`.`period_end` AS `period_end`, `charges`.`amount` AS `amount`, `charges`.`due_date` AS `due_date`, `charges`.`status` AS `status`, `charges`.`created_at` AS `created_at` FROM charges ORDER BY created_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfEnrollmentId: Int = 1
        val _columnIndexOfChargeType: Int = 2
        val _columnIndexOfPeriodStart: Int = 3
        val _columnIndexOfPeriodEnd: Int = 4
        val _columnIndexOfAmount: Int = 5
        val _columnIndexOfDueDate: Int = 6
        val _columnIndexOfStatus: Int = 7
        val _columnIndexOfCreatedAt: Int = 8
        val _result: MutableList<ChargeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargeEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpChargeType: String
          _tmpChargeType = _stmt.getText(_columnIndexOfChargeType)
          val _tmpPeriodStart: String?
          if (_stmt.isNull(_columnIndexOfPeriodStart)) {
            _tmpPeriodStart = null
          } else {
            _tmpPeriodStart = _stmt.getText(_columnIndexOfPeriodStart)
          }
          val _tmpPeriodEnd: String?
          if (_stmt.isNull(_columnIndexOfPeriodEnd)) {
            _tmpPeriodEnd = null
          } else {
            _tmpPeriodEnd = _stmt.getText(_columnIndexOfPeriodEnd)
          }
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpDueDate: String?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ChargeEntity(_tmpId,_tmpEnrollmentId,_tmpChargeType,_tmpPeriodStart,_tmpPeriodEnd,_tmpAmount,_tmpDueDate,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPaymentsForCharge(chargeId: String): Flow<List<PaymentEntity>> {
    val _sql: String = "SELECT * FROM payments WHERE charge_id = ? ORDER BY paid_at ASC"
    return createFlow(__db, false, arrayOf("payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, chargeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfChargeId: Int = getColumnIndexOrThrow(_stmt, "charge_id")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfPaidAt: Int = getColumnIndexOrThrow(_stmt, "paid_at")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfReferenceNote: Int = getColumnIndexOrThrow(_stmt, "reference_note")
        val _result: MutableList<PaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PaymentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpChargeId: String
          _tmpChargeId = _stmt.getText(_columnIndexOfChargeId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpPaidAt: Long
          _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpReferenceNote: String?
          if (_stmt.isNull(_columnIndexOfReferenceNote)) {
            _tmpReferenceNote = null
          } else {
            _tmpReferenceNote = _stmt.getText(_columnIndexOfReferenceNote)
          }
          _item =
              PaymentEntity(_tmpId,_tmpChargeId,_tmpAmount,_tmpPaidAt,_tmpMethod,_tmpReferenceNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalPaidForCharge(chargeId: String): Long {
    val _sql: String = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE charge_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, chargeId)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllPaymentsForBackup(): List<PaymentEntity> {
    val _sql: String =
        "SELECT `payments`.`id` AS `id`, `payments`.`charge_id` AS `charge_id`, `payments`.`amount` AS `amount`, `payments`.`paid_at` AS `paid_at`, `payments`.`method` AS `method`, `payments`.`reference_note` AS `reference_note` FROM payments ORDER BY paid_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfChargeId: Int = 1
        val _columnIndexOfAmount: Int = 2
        val _columnIndexOfPaidAt: Int = 3
        val _columnIndexOfMethod: Int = 4
        val _columnIndexOfReferenceNote: Int = 5
        val _result: MutableList<PaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PaymentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpChargeId: String
          _tmpChargeId = _stmt.getText(_columnIndexOfChargeId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpPaidAt: Long
          _tmpPaidAt = _stmt.getLong(_columnIndexOfPaidAt)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpReferenceNote: String?
          if (_stmt.isNull(_columnIndexOfReferenceNote)) {
            _tmpReferenceNote = null
          } else {
            _tmpReferenceNote = _stmt.getText(_columnIndexOfReferenceNote)
          }
          _item =
              PaymentEntity(_tmpId,_tmpChargeId,_tmpAmount,_tmpPaidAt,_tmpMethod,_tmpReferenceNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPackageLedger(enrollmentId: String): Flow<List<PackageLedgerEntity>> {
    val _sql: String =
        "SELECT * FROM package_ledgers WHERE enrollment_id = ? ORDER BY created_at ASC"
    return createFlow(__db, false, arrayOf("package_ledgers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEnrollmentId: Int = getColumnIndexOrThrow(_stmt, "enrollment_id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfDeltaSessions: Int = getColumnIndexOrThrow(_stmt, "delta_sessions")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<PackageLedgerEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PackageLedgerEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpDeltaSessions: Int
          _tmpDeltaSessions = _stmt.getLong(_columnIndexOfDeltaSessions).toInt()
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PackageLedgerEntity(_tmpId,_tmpEnrollmentId,_tmpSessionId,_tmpDeltaSessions,_tmpReason,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRemainingSessions(enrollmentId: String): Flow<Int> {
    val _sql: String =
        "SELECT COALESCE(SUM(delta_sessions), 0) FROM package_ledgers WHERE enrollment_id = ?"
    return createFlow(__db, false, arrayOf("package_ledgers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
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

  public override suspend fun getRemainingSessionsSync(enrollmentId: String): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(delta_sessions), 0) FROM package_ledgers WHERE enrollment_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, enrollmentId)
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

  public override fun countLowBalanceEnrollments(threshold: Int): Flow<Int> {
    val _sql: String = """
        |
        |        SELECT COUNT(DISTINCT pl.enrollment_id) 
        |        FROM package_ledgers pl
        |        INNER JOIN enrollments e ON pl.enrollment_id = e.id
        |        WHERE e.billing_type = 'PACKAGE' AND e.status = 'ACTIVE'
        |        GROUP BY pl.enrollment_id
        |        HAVING SUM(pl.delta_sessions) <= ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("package_ledgers", "enrollments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, threshold.toLong())
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

  public override suspend fun getAllLedgerEntriesForBackup(): List<PackageLedgerEntity> {
    val _sql: String =
        "SELECT `package_ledgers`.`id` AS `id`, `package_ledgers`.`enrollment_id` AS `enrollment_id`, `package_ledgers`.`session_id` AS `session_id`, `package_ledgers`.`delta_sessions` AS `delta_sessions`, `package_ledgers`.`reason` AS `reason`, `package_ledgers`.`created_at` AS `created_at` FROM package_ledgers ORDER BY created_at ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfEnrollmentId: Int = 1
        val _columnIndexOfSessionId: Int = 2
        val _columnIndexOfDeltaSessions: Int = 3
        val _columnIndexOfReason: Int = 4
        val _columnIndexOfCreatedAt: Int = 5
        val _result: MutableList<PackageLedgerEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PackageLedgerEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEnrollmentId: String
          _tmpEnrollmentId = _stmt.getText(_columnIndexOfEnrollmentId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpDeltaSessions: Int
          _tmpDeltaSessions = _stmt.getLong(_columnIndexOfDeltaSessions).toInt()
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PackageLedgerEntity(_tmpId,_tmpEnrollmentId,_tmpSessionId,_tmpDeltaSessions,_tmpReason,_tmpCreatedAt)
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
