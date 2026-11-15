package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.lespri.app.data.local.entity.ClassGroupEntity
import edu.lespri.app.data.local.entity.EnrollmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi pada tabel `class_groups` dan `enrollments`.
 *
 * Enrollment dikelompokkan bersama ClassDao karena keduanya selalu diakses
 * bersamaan pada layar detail kelas (ClassDetailScreen).
 */
@Dao
interface ClassDao {

    // ───────────────────────── Class Groups ─────────────────────────

    /**
     * Observasi daftar kelas berdasarkan status, diurut alfabetis.
     */
    @Query("SELECT * FROM class_groups WHERE status = :status ORDER BY name ASC")
    fun getClassesByStatus(status: String = "ACTIVE"): Flow<List<ClassGroupEntity>>

    /**
     * Observasi data detail satu kelas berdasarkan ID.
     */
    @Query("SELECT * FROM class_groups WHERE id = :id")
    fun getClassById(id: String): Flow<ClassGroupEntity?>

    /**
     * Insert atau update (replace) satu kelas.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClass(classGroup: ClassGroupEntity)

    /**
     * Soft-delete: ubah status kelas menjadi ARCHIVED.
     */
    @Query("UPDATE class_groups SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveClass(id: String)

    /**
     * Ambil semua kelas untuk keperluan backup JSON.
     */
    @Query("SELECT * FROM class_groups ORDER BY created_at ASC")
    suspend fun getAllClassesForBackup(): List<ClassGroupEntity>

    // ───────────────────────── Enrollments ─────────────────────────

    /**
     * Observasi daftar enrollment aktif untuk sebuah kelas.
     * Digunakan di ClassDetailScreen untuk menampilkan daftar siswa.
     */
    @Query("SELECT * FROM enrollments WHERE class_id = :classId AND status = 'ACTIVE'")
    fun getActiveEnrollmentsForClass(classId: String): Flow<List<EnrollmentEntity>>

    /**
     * Observasi daftar enrollment aktif untuk seorang siswa.
     * Digunakan di StudentDetailScreen untuk menampilkan kelas yang diikuti.
     */
    @Query("SELECT * FROM enrollments WHERE student_id = :studentId AND status = 'ACTIVE'")
    fun getActiveEnrollmentsForStudent(studentId: String): Flow<List<EnrollmentEntity>>

    /**
     * Ambil satu enrollment berdasarkan ID (suspend, bukan Flow).
     * Digunakan dalam @Transaction saat rekam kehadiran.
     */
    @Query("SELECT * FROM enrollments WHERE id = :enrollmentId")
    suspend fun getEnrollmentById(enrollmentId: String): EnrollmentEntity?

    /**
     * Insert enrollment baru.
     * OnConflictStrategy.ABORT: gagal jika unique constraint (student+class+ACTIVE) dilanggar.
     * Repository harus menangkap SQLiteConstraintException dan return Result.failure.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    /**
     * Ubah status enrollment menjadi WITHDRAWN/COMPLETED/SUSPENDED.
     * Gunakan status = 'WITHDRAWN' untuk siswa berhenti.
     */
    @Query("UPDATE enrollments SET status = :newStatus WHERE id = :enrollmentId")
    suspend fun updateEnrollmentStatus(enrollmentId: String, newStatus: String)

    /**
     * Ambil semua enrollment untuk keperluan backup JSON.
     */
    @Query("SELECT * FROM enrollments ORDER BY created_at ASC")
    suspend fun getAllEnrollmentsForBackup(): List<EnrollmentEntity>
}
