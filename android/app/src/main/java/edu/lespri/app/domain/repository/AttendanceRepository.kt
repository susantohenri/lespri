package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk lembar presensi / kehadiran.
 */
interface AttendanceRepository {
    fun getSessionAttendance(sessionId: String): Flow<List<AttendanceRecord>>
    suspend fun submitSessionAttendance(
        sessionId: String,
        records: List<AttendanceRecord>
    ): Result<Unit>
    fun countUnrecordedSessions(todayIso: String): Flow<Int>
}
