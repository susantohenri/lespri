package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas riwayat pembayaran tagihan.
 * Setiap pembayaran terikat ke sebuah ChargeEntity (charge_id).
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = ChargeEntity::class,
            parentColumns = ["id"],
            childColumns = ["charge_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["charge_id"], name = "index_payments_charge_id")
    ]
)
data class PaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "charge_id")
    val chargeId: String,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "paid_at")
    val paidAt: Long,

    @ColumnInfo(name = "method")
    val method: String,

    @ColumnInfo(name = "reference_note")
    val referenceNote: String? = null
)
