package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas pendaftaran siswa ke kelas (Enrollment).
 *
 * Menyimpan perjanjian penagihan (billing agreement) antara siswa dan kelas.
 * Saldo paket TIDAK disimpan di sini — dihitung dinamis via SUM(PackageLedger.delta_sessions).
 *
 * Invariant:
 * - Seorang siswa hanya boleh memiliki SATU enrollment ACTIVE per kelas.
 *   Dijaga oleh [index_enrollments_active_student_class] (unique composite index).
 * - [student_id] dan [class_id] ON DELETE RESTRICT — data enrollment harus
 *   terlebih dahulu di-archive sebelum siswa/kelas bisa dihapus.
 */
@Entity(
    tableName = "enrollments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClassGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        // UNIQUE: satu siswa hanya boleh punya satu enrollment per status per kelas.
        // Kombinasi (student_id, class_id, ACTIVE) harus unik.
        Index(
            value = ["student_id", "class_id", "status"],
            name = "index_enrollments_active_student_class",
            unique = true
        ),
        Index(value = ["class_id"], name = "index_enrollments_class_id"),
        Index(value = ["student_id"], name = "index_enrollments_student_id")
    ]
)
data class EnrollmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "student_id")
    val studentId: String,

    @ColumnInfo(name = "class_id")
    val classId: String,

    /**
     * Skema penagihan: 'MONTHLY' | 'PACKAGE' | 'PER_SESSION'.
     * Disimpan sebagai String, dikonversi ke [BillingType] via TypeConverter.
     */
    @ColumnInfo(name = "billing_type")
    val billingType: String,

    /**
     * Nominal biaya dalam Rupiah (Long). Tidak ada Float/Double.
     * Makna tergantung [billingType]:
     * - MONTHLY: biaya per bulan
     * - PACKAGE: total harga paket sesi
     * - PER_SESSION: biaya per kehadiran
     */
    @ColumnInfo(name = "fee_amount")
    val feeAmount: Long,

    /**
     * Tanggal jatuh tempo per bulan (1–28). Null jika billing bukan MONTHLY.
     */
    @ColumnInfo(name = "due_day")
    val dueDay: Int? = null,

    /**
     * Jumlah sesi dalam paket. Null jika billing bukan PACKAGE.
     * Saldo aktual dihitung via SUM(PackageLedger.delta_sessions), BUKAN dari kolom ini.
     */
    @ColumnInfo(name = "package_session_count")
    val packageSessionCount: Int? = null,

    /**
     * Status enrollment: 'ACTIVE' | 'COMPLETED' | 'WITHDRAWN' | 'SUSPENDED'.
     * Soft-delete — tidak pernah di-DELETE dari database.
     */
    @ColumnInfo(name = "status", defaultValue = "ACTIVE")
    val status: String = "ACTIVE",

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
