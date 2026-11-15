package edu.lespri.app.domain.model

/**
 * Model domain sesi pelajaran konkret — baik yang di-generate dari ScheduleRule
 * maupun yang dibuat secara ad-hoc oleh pengelola.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class LessonSession(
    val id: String,
    val classId: String,
    val tutorId: String? = null,
    val sessionDate: String, // Format ISO-8601: "YYYY-MM-DD"
    val startTime: String,   // Format 24-jam: "HH:mm"
    val durationMinutes: Int,
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val sourceRuleId: String? = null
)
