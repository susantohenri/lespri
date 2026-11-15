package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.lespri.app.data.local.entity.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi pada tabel `attendance_records`.
 *
 * Desain kritis:
 * - upsertAttendanceBatch menggunakan REPLACE: jika record sudah ada (session+enrollment),
 *   row lama diganti dengan status baru. Ini adalah operasi yang diorkestrasi oleh
 *   AttendanceRepositoryImpl di dalam @Transaction bersama PackageLedger insert.
 * - Tidak ada metode DELETE — integritas audit trail dijaga penuh.
 */
@Dao
interface AttendanceDao {

    /**
     * Observasi daftar kehadiran untuk satu sesi.
     * Digunakan di AttendanceSheetScreen untuk menampilkan daftar siswa + status.
     */
    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId")
    fun getAttendanceForSession(sessionId: String): Flow<List<AttendanceRecordEntity>>

    /**
     * Ambil semua kehadiran dalam satu sesi (suspend, untuk @Transaction).
     */
    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId")
    suspend fun getAttendanceForSessionSync(sessionId: String): List<AttendanceRecordEntity>

    /**
     * Ambil satu record kehadiran berdasarkan session dan enrollment (suspend).
     * Null jika belum ada record (belum pernah diisi).
     */
    @Query("""
        SELECT * FROM attendance_records 
        WHERE session_id = :sessionId AND enrollment_id = :enrollmentId
        LIMIT 1
    """)
    suspend fun getAttendanceRecord(sessionId: String, enrollmentId: String): AttendanceRecordEntity?

    /**
     * Observasi riwayat kehadiran seorang siswa (via enrollment ID).
     */
    @Query("""
        SELECT * FROM attendance_records 
        WHERE enrollment_id = :enrollmentId 
        ORDER BY updated_at DESC
    """)
    fun getAttendanceForEnrollment(enrollmentId: String): Flow<List<AttendanceRecordEntity>>

    /**
     * Batch upsert semua record kehadiran dalam satu sesi.
     * REPLACE: row sudah ada → diganti, belum ada → di-insert.
     * WAJIB dijalankan di dalam @Transaction bersamaan dengan PackageLedger operations.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendanceBatch(records: List<AttendanceRecordEntity>)

    /**
     * Hitung jumlah sesi yang belum diisi kehadirannya s/d tanggal [todayDate].
     * Digunakan di DashboardScreen sebagai alert badge.
     * Sesi dianggap "belum diisi" jika tidak ada record kehadiran sama sekali,
     * atau semua recordnya masih UNRECORDED.
     */
    @Query("""
        SELECT COUNT(*) FROM lesson_sessions s
        LEFT JOIN attendance_records a ON s.id = a.session_id
        WHERE s.session_date <= :todayDate 
        AND s.status = 'SCHEDULED' 
        AND (a.id IS NULL OR a.status = 'UNRECORDED')
    """)
    fun countUnrecordedSessions(todayDate: String): Flow<Int>

    /**
     * Ambil semua record kehadiran untuk backup JSON.
     */
    @Query("SELECT * FROM attendance_records ORDER BY updated_at ASC")
    suspend fun getAllAttendanceForBackup(): List<AttendanceRecordEntity>
}
