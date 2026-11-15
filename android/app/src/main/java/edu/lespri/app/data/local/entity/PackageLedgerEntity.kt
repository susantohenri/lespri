package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas buku besar saldo paket sesi — APPEND-ONLY audit trail.
 *
 * Prinsip desain (Critical Path mitigation):
 * - Saldo TIDAK disimpan sebagai kolom mutable. Tidak ada UPDATE/DELETE pada tabel ini.
 * - Saldo aktual dihitung pada waktu query: SUM(delta_sessions) WHERE enrollment_id = :id
 * - Setiap perubahan status kehadiran menghasilkan INSERT baru, bukan UPDATE.
 * - Audit trail terjaga sepenuhnya — setiap transaksi bisa ditelusuri.
 *
 * Jenis entri ([reason]):
 * - 'PACKAGE_PURCHASE'       → delta_sessions = +N   (paket dibeli / enrollment baru)
 * - 'ATTENDANCE_DEDUCTION'   → delta_sessions = -1   (siswa PRESENT)
 * - 'ATTENDANCE_REVERSAL'    → delta_sessions = +1   (PRESENT dibatalkan)
 * - 'MANUAL_ADJUSTMENT'      → delta_sessions = ±N   (koreksi admin)
 *
 * Race condition mitigation:
 * - Semua INSERT ke tabel ini dilakukan di dalam Room @Transaction bersamaan dengan
 *   perubahan status AttendanceRecord, sehingga konsistensi terjamin secara atomik.
 */
@Entity(
    tableName = "package_ledgers",
    foreignKeys = [
        ForeignKey(
            entity = EnrollmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["enrollment_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LessonSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["enrollment_id"], name = "index_package_ledgers_enrollment_id"),
        Index(value = ["session_id"], name = "index_package_ledgers_session_id")
    ]
)
data class PackageLedgerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "enrollment_id")
    val enrollmentId: String,

    /**
     * ID sesi yang memicu entri ini. Null untuk PACKAGE_PURCHASE atau MANUAL_ADJUSTMENT.
     */
    @ColumnInfo(name = "session_id")
    val sessionId: String? = null,

    /**
     * Perubahan saldo sesi. Positif = penambahan, Negatif = pengurangan.
     * Contoh: ATTENDANCE_DEDUCTION → -1, PACKAGE_PURCHASE (8 sesi) → +8.
     */
    @ColumnInfo(name = "delta_sessions")
    val deltaSessions: Int,

    /**
     * Alasan entri: 'PACKAGE_PURCHASE' | 'ATTENDANCE_DEDUCTION' |
     * 'ATTENDANCE_REVERSAL' | 'MANUAL_ADJUSTMENT'.
     * Disimpan sebagai String, dikonversi ke [LedgerEntryType] via TypeConverter.
     */
    @ColumnInfo(name = "reason")
    val reason: String,

    /**
     * Timestamp (epoch ms UTC) saat entri ini dibuat.
     * Karena ledger append-only, ini sekaligus menjadi urutan kronologis.
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
