package edu.lespri.app.data.repository

import edu.lespri.app.data.local.dao.TutorDao
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.model.TutorStatus
import edu.lespri.app.domain.repository.TutorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori untuk pengelolaan data tutor menggunakan Room [TutorDao].
 */
@Singleton
class TutorRepositoryImpl @Inject constructor(
    private val tutorDao: TutorDao
) : TutorRepository {

    override fun getActiveTutors(): Flow<List<Tutor>> {
        return tutorDao.getTutorsByStatus("ACTIVE").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTutorsByStatus(status: TutorStatus): Flow<List<Tutor>> {
        return tutorDao.getTutorsByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTutor(id: String): Flow<Tutor?> {
        return tutorDao.getTutorById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveTutor(tutor: Tutor): Result<Unit> {
        val trimmedName = tutor.name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Nama tutor wajib diisi"))
        }
        return runCatching {
            tutorDao.upsertTutor(tutor.copy(name = trimmedName).toEntity())
        }
    }

    override suspend fun archiveTutor(id: String): Result<Unit> {
        return runCatching {
            tutorDao.archiveTutor(id)
        }
    }
}
