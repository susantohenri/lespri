package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas data siswa bimbingan belajar.
 * Soft-delete didukung melalui field status ('ACTIVE' | 'ARCHIVED').
 */
@Entity(
    tableName = "students",
    indices = [
        Index(value = ["status"], name = "index_students_status"),
        Index(value = ["name"], name = "index_students_name")
    ]
)
data class StudentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "guardian_name")
    val guardianName: String? = null,

    @ColumnInfo(name = "guardian_phone")
    val guardianPhone: String? = null,

    @ColumnInfo(name = "school_grade")
    val schoolGrade: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "status", defaultValue = "ACTIVE")
    val status: String = "ACTIVE",

    @ColumnInfo(name = "joined_at")
    val joinedAt: Long
)
