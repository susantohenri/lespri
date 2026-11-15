package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas kelompok/kelas belajar (Privat maupun Grup).
 * Berelasi opsional dengan Tutor (tutor_id).
 */
@Entity(
    tableName = "class_groups",
    foreignKeys = [
        ForeignKey(
            entity = TutorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tutor_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["tutor_id"], name = "index_class_groups_tutor_id"),
        Index(value = ["status"], name = "index_class_groups_status")
    ]
)
data class ClassGroupEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "class_type")
    val classType: String,

    @ColumnInfo(name = "subject")
    val subject: String? = null,

    @ColumnInfo(name = "tutor_id")
    val tutorId: String? = null,

    @ColumnInfo(name = "location_note")
    val locationNote: String? = null,

    @ColumnInfo(name = "status", defaultValue = "ACTIVE")
    val status: String = "ACTIVE",

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
