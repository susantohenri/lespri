package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.Student
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk data siswa.
 */
interface StudentRepository {
    fun getActiveStudents(): Flow<List<Student>>
    fun getStudent(id: String): Flow<Student?>
    fun searchStudents(query: String): Flow<List<Student>>
    suspend fun saveStudent(student: Student): Result<Unit>
    suspend fun archiveStudent(id: String): Result<Unit>
}
