package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.LessonSessionDetail
import edu.lespri.app.domain.model.ScheduleRule
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk jadwal dan sesi pelajaran.
 */
interface ScheduleRepository {
    fun getAgendaSessions(startDate: String, endDate: String): Flow<List<LessonSession>>
    fun getSessionsForClassInRange(classId: String, startDate: String, endDate: String): Flow<List<LessonSession>>
    fun getSessionDetail(sessionId: String): Flow<LessonSessionDetail?>
    suspend fun createAdHocSession(session: LessonSession): Result<Unit>
    suspend fun updateSession(session: LessonSession): Result<Unit>
    suspend fun saveScheduleRule(rule: ScheduleRule): Result<Unit>
    suspend fun generateRecurringSessions(upToDate: String): Result<Int>
    suspend fun cancelSession(sessionId: String): Result<Unit>
}
