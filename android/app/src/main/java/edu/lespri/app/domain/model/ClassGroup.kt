package edu.lespri.app.domain.model

/**
 * Model domain kelompok / kelas bimbingan belajar (Privat maupun Grup).
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class ClassGroup(
    val id: String,
    val name: String,
    val classType: ClassType,
    val subject: String? = null,
    val tutorId: String? = null,
    val locationNote: String? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)
