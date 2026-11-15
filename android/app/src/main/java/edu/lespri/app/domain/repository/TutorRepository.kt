package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.model.TutorStatus
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk data tutor / pengajar.
 */
interface TutorRepository {
    fun getActiveTutors(): Flow<List<Tutor>>
    fun getTutorsByStatus(status: TutorStatus): Flow<List<Tutor>>
    fun getTutor(id: String): Flow<Tutor?>
    suspend fun saveTutor(tutor: Tutor): Result<Unit>
    suspend fun archiveTutor(id: String): Result<Unit>
}
