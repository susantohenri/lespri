package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas catatan kehadiran seorang siswa pada satu sesi pelajaran.
 *
 * Invariant:
 * - Satu siswa hanya boleh memiliki SATU record per sesi.
 *   Dijaga oleh [index_attendance_records_session_enrollment] (unique composite index).
 * - [session_id] ON DELETE CASCADE — saat sesi dihapus, seluruh attendance-nya ikut dihapus.
 *   Namun dalam praktik MVP, sesi tidak pernah di-DELETE (hanya status -> CANCELLED),
 *   sehingga reversal saldo dilakukan via @Transaction sebelum status diubah.
 * - [enrollment_id] ON DELETE RESTRICT — record kehadiran melindungi enrollment dari penghapusan.
 *
 * Status flow:
 *   UNRECORDED ──▶ PRESENT       → insert PackageLedger(delta = -1) jika billing = PACKAGE
 *   PRESENT    ──▶ EXCUSED/SICK/ABSENT/UNRECORDED  → insert PackageLedger(delta = +1) reversal
 */
@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = LessonSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EnrollmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["enrollment_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        // UNIQUE: satu siswa (enrollment) hanya punya satu record per sesi.
        Index(
            value = ["session_id", "enrollment_id"],
            name = "index_attendance_records_session_enrollment",
            unique = true
        ),
        Index(value = ["enrollment_id"], name = "index_attendance_records_enrollment_id")
    ]
)
data class AttendanceRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "enrollment_id")
    val enrollmentId: String,

    /**
     * Status kehadiran: 'UNRECORDED' | 'PRESENT' | 'EXCUSED' | 'SICK' | 'ABSENT'.
     * Hanya 'PRESENT' yang menyebabkan pemotongan saldo paket (via PackageLedger).
     */
    @ColumnInfo(name = "status", defaultValue = "UNRECORDED")
    val status: String = "UNRECORDED",

    /**
     * Catatan opsional (misal: "Sakit - ada surat dokter", "Izin acara sekolah").
     */
    @ColumnInfo(name = "note")
    val note: String? = null,

    /**
     * Timestamp (epoch ms UTC) terakhir status diubah — untuk audit trail.
     */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
