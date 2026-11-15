package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.lespri.app.data.local.entity.TutorEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi CRUD tabel `tutors`.
 *
 * Soft-delete: tidak ada DELETE query. Tutor yang masih terhubung ke kelas
 * aktif tidak boleh di-archive sebelum kelasnya selesai.
 */
@Dao
interface TutorDao {

    /**
     * Observasi daftar tutor berdasarkan status, diurut alfabetis.
     */
    @Query("SELECT * FROM tutors WHERE status = :status ORDER BY name ASC")
    fun getTutorsByStatus(status: String = "ACTIVE"): Flow<List<TutorEntity>>

    /**
     * Observasi data detail satu tutor berdasarkan ID.
     */
    @Query("SELECT * FROM tutors WHERE id = :id")
    fun getTutorById(id: String): Flow<TutorEntity?>

    /**
     * Insert atau update (replace) satu tutor.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTutor(tutor: TutorEntity)

    /**
     * Soft-delete: ubah status tutor menjadi ARCHIVED.
     */
    @Query("UPDATE tutors SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveTutor(id: String)

    /**
     * Ambil semua tutor (ACTIVE + ARCHIVED) untuk keperluan backup JSON.
     */
    @Query("SELECT * FROM tutors ORDER BY created_at ASC")
    suspend fun getAllTutorsForBackup(): List<TutorEntity>
}
