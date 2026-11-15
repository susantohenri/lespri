package edu.lespri.app.domain.model

/**
 * Model domain catatan presensi / kehadiran seorang siswa pada satu sesi pelajaran.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val enrollmentId: String,
    val status: AttendanceStatus = AttendanceStatus.UNRECORDED,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
