package edu.lespri.app.domain.model

/**
 * Model domain siswa bimbingan belajar.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class Student(
    val id: String,
    val name: String,
    val guardianName: String? = null,
    val guardianPhone: String? = null,
    val schoolGrade: String? = null,
    val notes: String? = null,
    val status: StudentStatus = StudentStatus.ACTIVE,
    val joinedAt: Long = System.currentTimeMillis()
)
