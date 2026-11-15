package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.lespri.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi CRUD tabel `students`.
 *
 * Konvensi query:
 * - Semua read yang di-observe UI menggunakan Flow (auto-update on change).
 * - Write operations menggunakan suspend function (Coroutine-safe).
 * - Soft-delete: tidak ada DELETE query. Arsip via UPDATE status = 'ARCHIVED'.
 */
@Dao
interface StudentDao {

    /**
     * Observasi daftar siswa berdasarkan status, diurut alfabetis.
     * Default: hanya siswa ACTIVE yang ditampilkan.
     */
    @Query("SELECT * FROM students WHERE status = :status ORDER BY name ASC")
    fun getStudentsByStatus(status: String = "ACTIVE"): Flow<List<StudentEntity>>

    /**
     * Observasi data detail satu siswa berdasarkan ID.
     * Null jika ID tidak ditemukan.
     */
    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: String): Flow<StudentEntity?>

    /**
     * Pencarian siswa aktif berdasarkan nama atau nomor telepon wali.
     * Case-insensitive (SQLite LIKE default).
     */
    @Query("""
        SELECT * FROM students 
        WHERE status = 'ACTIVE' 
        AND (name LIKE '%' || :query || '%' OR guardian_phone LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    /**
     * Insert atau update (replace) satu siswa.
     * Digunakan untuk operasi create dan edit form.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudent(student: StudentEntity)

    /**
     * Batch insert untuk proses impor CSV.
     * Conflict REPLACE: data lama diganti jika ID sudah ada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentsBatch(students: List<StudentEntity>)

    /**
     * Soft-delete: ubah status siswa menjadi ARCHIVED.
     * Data historis (attendance, enrollment, charges) tetap terjaga.
     */
    @Query("UPDATE students SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveStudent(id: String)

    /**
     * Ambil semua siswa (ACTIVE + ARCHIVED) untuk keperluan backup JSON.
     */
    @Query("SELECT * FROM students ORDER BY joined_at ASC")
    suspend fun getAllStudentsForBackup(): List<StudentEntity>
}
