package edu.lespri.app.domain.model

/**
 * Model domain tutor / pengajar bimbingan belajar.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class Tutor(
    val id: String,
    val name: String,
    val phone: String? = null,
    val expertise: String? = null,
    val notes: String? = null,
    val status: TutorStatus = TutorStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)
