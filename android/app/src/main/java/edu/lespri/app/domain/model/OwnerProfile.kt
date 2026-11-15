package edu.lespri.app.domain.model

/**
 * Domain model untuk profil pemilik bimbel.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class OwnerProfile(
    val id: String,
    val businessName: String,
    val createdAt: Long,
    val updatedAt: Long
)
