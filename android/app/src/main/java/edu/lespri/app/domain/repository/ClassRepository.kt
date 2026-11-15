package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassGroupDetail
import edu.lespri.app.domain.model.Enrollment
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk data kelas dan pendaftaran (enrollment).
 */
interface ClassRepository {
    fun getClasses(): Flow<List<ClassGroup>>
    fun getClassesByStatus(status: String): Flow<List<ClassGroup>>
    fun getClassDetail(id: String): Flow<ClassGroupDetail?>
    suspend fun saveClass(classGroup: ClassGroup): Result<Unit>
    suspend fun archiveClass(id: String): Result<Unit>
    suspend fun enrollStudent(enrollment: Enrollment): Result<Unit>
    suspend fun removeEnrollment(enrollmentId: String): Result<Unit>
    fun getClass(id: String): Flow<ClassGroup?>
    suspend fun countActiveEnrollmentsForClass(classId: String): Int
    suspend fun countActiveClassesForTutor(tutorId: String): Int
    suspend fun countActiveEnrollmentsForStudent(studentId: String): Int
    fun getActiveEnrollmentsForStudent(studentId: String): Flow<List<Enrollment>>
}
