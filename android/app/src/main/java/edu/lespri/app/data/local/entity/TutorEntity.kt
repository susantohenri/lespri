package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas data tutor/pengajar bimbingan belajar.
 * Soft-delete didukung melalui field status ('ACTIVE' | 'ARCHIVED').
 */
@Entity(
    tableName = "tutors",
    indices = [
        Index(value = ["status"], name = "index_tutors_status")
    ]
)
data class TutorEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "expertise")
    val expertise: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "status", defaultValue = "ACTIVE")
    val status: String = "ACTIVE",

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
