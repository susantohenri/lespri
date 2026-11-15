package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas aturan jadwal berulang (recurring schedule rule) untuk sebuah kelas.
 * Digunakan oleh mesin penjadwalan untuk men-generate LessonSession secara otomatis.
 */
@Entity(
    tableName = "schedule_rules",
    foreignKeys = [
        ForeignKey(
            entity = ClassGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TutorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tutor_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["class_id"], name = "index_schedule_rules_class_id"),
        Index(value = ["tutor_id"], name = "index_schedule_rules_tutor_id")
    ]
)
data class ScheduleRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "class_id")
    val classId: String,

    @ColumnInfo(name = "tutor_id")
    val tutorId: String? = null,

    @ColumnInfo(name = "weekday")
    val weekday: Int,

    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,

    @ColumnInfo(name = "effective_from")
    val effectiveFrom: String,

    @ColumnInfo(name = "effective_until")
    val effectiveUntil: String? = null,

    @ColumnInfo(name = "status", defaultValue = "ACTIVE")
    val status: String = "ACTIVE"
)
