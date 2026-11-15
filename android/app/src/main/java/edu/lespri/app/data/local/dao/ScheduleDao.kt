package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.lespri.app.data.local.entity.LessonSessionEntity
import edu.lespri.app.data.local.entity.ScheduleRuleEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi pada tabel `lesson_sessions` dan `schedule_rules`.
 *
 * Konvensi penting:
 * - Tanggal selalu dalam format "YYYY-MM-DD" (ISO-8601) untuk ORDER BY lexicographic.
 * - Jam selalu dalam format "HH:mm" 24-jam.
 * - insertSessions menggunakan IGNORE conflict: session duplikat (slot sudah ada) diabaikan
 *   tanpa error — ini penting saat auto-generate berulang kali untuk range yang sama.
 */
@Dao
interface ScheduleDao {

    // ───────────────────────── Schedule Rules ─────────────────────────

    /**
     * Observasi semua aturan jadwal berulang untuk sebuah kelas.
     */
    @Query("SELECT * FROM schedule_rules WHERE class_id = :classId AND status = 'ACTIVE'")
    fun getActiveRulesForClass(classId: String): Flow<List<ScheduleRuleEntity>>

    /**
     * Ambil semua aturan jadwal aktif (untuk mesin generate sesi).
     */
    @Query("SELECT * FROM schedule_rules WHERE status = 'ACTIVE'")
    suspend fun getAllActiveRules(): List<ScheduleRuleEntity>

    /**
     * Insert aturan jadwal baru.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScheduleRule(rule: ScheduleRuleEntity)

    /**
     * Nonaktifkan aturan jadwal (soft-delete).
     */
    @Query("UPDATE schedule_rules SET status = 'INACTIVE' WHERE id = :ruleId")
    suspend fun deactivateRule(ruleId: String)

    // ───────────────────────── Lesson Sessions ─────────────────────────

    /**
     * Observasi semua sesi untuk tanggal tertentu, diurut jam mulai.
     */
    @Query("SELECT * FROM lesson_sessions WHERE session_date = :date ORDER BY start_time ASC")
    fun getSessionsForDate(date: String): Flow<List<LessonSessionEntity>>

    /**
     * Observasi detail satu sesi berdasarkan ID.
     */
    @Query("SELECT * FROM lesson_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: String): Flow<LessonSessionEntity?>

    /**
     * Ambil satu sesi (suspend) untuk operasi @Transaction.
     */
    @Query("SELECT * FROM lesson_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdSync(sessionId: String): LessonSessionEntity?

    /**
     * Observasi sesi dalam rentang tanggal [startDate, endDate] (inklusif).
     */
    @Query("""
        SELECT * FROM lesson_sessions 
        WHERE session_date >= :startDate AND session_date <= :endDate 
        ORDER BY session_date ASC, start_time ASC
    """)
    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<LessonSessionEntity>>

    /**
     * Observasi sesi sebuah kelas dalam rentang tanggal [startDate, endDate] (inklusif).
     * Digunakan untuk preview jadwal pada ClassDetailScreen (P08).
     */
    @Query("""
        SELECT * FROM lesson_sessions 
        WHERE class_id = :classId 
        AND session_date >= :startDate AND session_date <= :endDate 
        ORDER BY session_date ASC, start_time ASC
    """)
    fun getSessionsForClassInRange(
        classId: String,
        startDate: String,
        endDate: String
    ): Flow<List<LessonSessionEntity>>

    /**
     * Batch insert sesi hasil generate dari ScheduleRule.
     * OnConflictStrategy.IGNORE: slot yang sudah ada diabaikan tanpa error.
     * Return: list rowId hasil insert (negatif = diabaikan).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSessions(sessions: List<LessonSessionEntity>): List<Long>

    /**
     * Insert satu sesi ad-hoc (dibuat manual oleh admin).
     * OnConflictStrategy.ABORT: gagal jika slot sudah terisi.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAdHocSession(session: LessonSessionEntity)

    /**
     * Update data sesi (digunakan untuk edit waktu, durasi, atau tutor).
     */
    @Update
    suspend fun updateSession(session: LessonSessionEntity)

    /**
     * Ubah status sesi menjadi CANCELLED.
     * Repository harus menjalankan ini di dalam @Transaction bersama attendance reversal.
     */
    @Query("UPDATE lesson_sessions SET status = 'CANCELLED' WHERE id = :sessionId")
    suspend fun cancelSession(sessionId: String)

    /**
     * Ambil semua sesi untuk backup JSON.
     */
    @Query("SELECT * FROM lesson_sessions ORDER BY session_date ASC, start_time ASC")
    suspend fun getAllSessionsForBackup(): List<LessonSessionEntity>

    /** Ambil semua aturan jadwal untuk backup JSON. */
    @Query("SELECT * FROM schedule_rules ORDER BY weekday ASC, start_time ASC")
    suspend fun getAllScheduleRulesForBackup(): List<ScheduleRuleEntity>
}
