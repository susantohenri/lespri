package edu.lespri.app.data.repository

import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.mapper.toDomain
import edu.lespri.app.data.mapper.toEntity
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori untuk pengelolaan data siswa menggunakan Room [StudentDao].
 */
@Singleton
class StudentRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao
) : StudentRepository {

    override fun getActiveStudents(): Flow<List<Student>> {
        return studentDao.getStudentsByStatus("ACTIVE").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getStudent(id: String): Flow<Student?> {
        return studentDao.getStudentById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchStudents(query: String): Flow<List<Student>> {
        return studentDao.searchStudents(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveStudent(student: Student): Result<Unit> {
        return runCatching {
            studentDao.upsertStudent(student.toEntity())
        }
    }

    override suspend fun archiveStudent(id: String): Result<Unit> {
        return runCatching {
            studentDao.archiveStudent(id)
        }
    }
}
