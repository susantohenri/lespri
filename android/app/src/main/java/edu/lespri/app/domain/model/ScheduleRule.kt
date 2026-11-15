package edu.lespri.app.domain.model

/**
 * Model domain aturan jadwal berulang untuk sebuah kelas.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class ScheduleRule(
    val id: String,
    val classId: String,
    val tutorId: String? = null,
    val weekday: Int, // 1 = Monday ... 7 = Sunday
    val startTime: String, // Format: "HH:mm"
    val durationMinutes: Int,
    val effectiveFrom: String, // Format: "YYYY-MM-DD"
    val effectiveUntil: String? = null, // Format: "YYYY-MM-DD"
    val status: String = "ACTIVE"
)
