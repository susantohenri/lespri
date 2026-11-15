package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas sesi pelajaran konkret — baik yang di-generate dari ScheduleRule
 * maupun yang dibuat secara ad-hoc oleh admin.
 *
 * Invariant:
 * - Satu kelas tidak boleh memiliki dua sesi di hari dan jam yang sama.
 *   Dijaga oleh [index_lesson_sessions_unique_slot] (unique composite index).
 * - [class_id] ON DELETE RESTRICT — sesi harus selesai/dibatalkan dulu
 *   sebelum kelas bisa di-archive.
 * - [tutor_id] dan [source_rule_id] ON DELETE SET NULL — opsional, boleh null
 *   jika tutor/rule dihapus.
 */
@Entity(
    tableName = "lesson_sessions",
    foreignKeys = [
        ForeignKey(
            entity = ClassGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TutorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tutor_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ScheduleRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_rule_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        // UNIQUE: tidak boleh ada dua sesi pada kelas, tanggal, dan jam yang sama.
        Index(
            value = ["class_id", "session_date", "start_time"],
            name = "index_lesson_sessions_unique_slot",
            unique = true
        ),
        Index(value = ["session_date"], name = "index_lesson_sessions_date"),
        Index(value = ["tutor_id"], name = "index_lesson_sessions_tutor_id"),
        Index(value = ["source_rule_id"], name = "index_lesson_sessions_source_rule_id")
    ]
)
data class LessonSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "class_id")
    val classId: String,

    @ColumnInfo(name = "tutor_id")
    val tutorId: String? = null,

    /**
     * Tanggal sesi dalam format ISO-8601: "YYYY-MM-DD".
     * Disimpan sebagai String agar SQLite ORDER BY lexicographic bekerja benar.
     */
    @ColumnInfo(name = "session_date")
    val sessionDate: String,

    /**
     * Jam mulai dalam format 24-jam: "HH:mm".
     */
    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,

    /**
     * Status sesi: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'.
     * CANCELLED akan men-cascade reversal saldo paket secara atomik via @Transaction.
     */
    @ColumnInfo(name = "status", defaultValue = "SCHEDULED")
    val status: String = "SCHEDULED",

    /**
     * ID aturan jadwal berulang yang menghasilkan sesi ini.
     * Null jika sesi dibuat secara ad-hoc oleh admin.
     */
    @ColumnInfo(name = "source_rule_id")
    val sourceRuleId: String? = null
)
