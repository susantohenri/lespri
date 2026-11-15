package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas tagihan keuangan (invoices/bills).
 * Dihubungkan ke pendaftaran (enrollment_id) untuk mencatat tagihan SPP bulanan,
 * pembelian paket sesi, atau tagihan per-sesi.
 */
@Entity(
    tableName = "charges",
    foreignKeys = [
        ForeignKey(
            entity = EnrollmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["enrollment_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["enrollment_id"], name = "index_charges_enrollment_id"),
        Index(value = ["status"], name = "index_charges_status")
    ]
)
data class ChargeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "enrollment_id")
    val enrollmentId: String,

    @ColumnInfo(name = "charge_type")
    val chargeType: String,

    @ColumnInfo(name = "period_start")
    val periodStart: String? = null,

    @ColumnInfo(name = "period_end")
    val periodEnd: String? = null,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,

    @ColumnInfo(name = "status", defaultValue = "UNPAID")
    val status: String = "UNPAID",

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
