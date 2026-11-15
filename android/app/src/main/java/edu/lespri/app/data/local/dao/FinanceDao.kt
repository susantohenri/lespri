package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.local.entity.PackageLedgerEntity
import edu.lespri.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi keuangan: tabel `charges`, `payments`, dan `package_ledgers`.
 *
 * Desain kritis (Overpayment Prevention):
 * - [getTotalPaidForCharge] adalah suspend fun yang digunakan di dalam @Transaction
 *   sebelum insert payment baru. Repository wajib memeriksa:
 *   newPayment.amount <= (charge.amount - totalAlreadyPaid)
 * - Jika kondisi dilanggar → return Result.failure (BUKAN insert ke DB).
 */
@Dao
interface FinanceDao {

    // ─────────────────────── Charges ───────────────────────

    /**
     * Observasi semua tagihan yang tidak dibatalkan, terbaru dulu.
     */
    @Query("SELECT * FROM charges WHERE status != 'CANCELLED' ORDER BY created_at DESC")
    fun getAllActiveCharges(): Flow<List<ChargeEntity>>

    /**
     * Observasi tagihan berdasarkan status tertentu.
     */
    @Query("SELECT * FROM charges WHERE status = :status ORDER BY created_at DESC")
    fun getChargesByStatus(status: String): Flow<List<ChargeEntity>>

    /**
     * Observasi semua tagihan untuk satu enrollment.
     */
    @Query("SELECT * FROM charges WHERE enrollment_id = :enrollmentId ORDER BY created_at DESC")
    fun getChargesForEnrollment(enrollmentId: String): Flow<List<ChargeEntity>>

    /**
     * Ambil semua tagihan satu enrollment secara langsung (suspend, bukan Flow).
     * Digunakan di dalam @Transaction untuk reversal/cascade pembatalan tagihan.
     */
    @Query("SELECT * FROM charges WHERE enrollment_id = :enrollmentId ORDER BY created_at DESC")
    suspend fun getChargesForEnrollmentSync(enrollmentId: String): List<ChargeEntity>

    /**
     * Observasi detail satu tagihan berdasarkan ID.
     */
    @Query("SELECT * FROM charges WHERE id = :chargeId")
    fun getChargeById(chargeId: String): Flow<ChargeEntity?>

    /**
     * Ambil satu tagihan (suspend) untuk operasi @Transaction pembayaran.
     */
    @Query("SELECT * FROM charges WHERE id = :chargeId")
    suspend fun getChargeByIdSync(chargeId: String): ChargeEntity?

    /**
     * Insert tagihan baru.
     * ABORT: gagal jika ID sudah ada (tidak boleh overwrite tagihan).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCharge(charge: ChargeEntity)

    /**
     * Update status tagihan (UNPAID → PARTIALLY_PAID → PAID).
     * Dipanggil di dalam @Transaction setelah payment berhasil di-insert.
     */
    @Update
    suspend fun updateCharge(charge: ChargeEntity)

    /**
     * Hitung total tagihan UNPAID + PARTIALLY_PAID untuk dashboard.
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM charges 
        WHERE status IN ('UNPAID', 'PARTIALLY_PAID')
    """)
    fun getTotalOutstandingDebt(): Flow<Long>

    /**
     * Hitung total pemasukan bulan ini dari tagihan PAID.
     * [monthPrefix] format: "YYYY-MM" (e.g., "2026-11").
     */
    @Query("""
        SELECT COALESCE(SUM(c.amount), 0) FROM charges c
        WHERE c.status = 'PAID'
        AND substr(c.created_at, 1, 7) = :monthPrefix
    """)
    fun getMonthlyRevenue(monthPrefix: String): Flow<Long>

    /**
     * Hitung sisa tagihan (amount - total pembayaran) untuk satu enrollment.
     * COALESCE memastikan hasil 0 jika tidak ada tagihan/pembayaran.
     */
    @Query("""
        SELECT COALESCE(SUM(
            c.amount - COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.charge_id = c.id), 0)
        ), 0)
        FROM charges c
        WHERE c.enrollment_id = :enrollmentId
        AND c.status NOT IN ('CANCELLED', 'PAID')
    """)
    suspend fun getOutstandingForEnrollment(enrollmentId: String): Long

    /**
     * Hitung total sisa tagihan seluruh enrollment aktif milik seorang siswa.
     */
    @Query("""
        SELECT COALESCE(SUM(
            c.amount - COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.charge_id = c.id), 0)
        ), 0)
        FROM charges c
        INNER JOIN enrollments e ON c.enrollment_id = e.id
        WHERE e.student_id = :studentId
        AND e.status = 'ACTIVE'
        AND c.status NOT IN ('CANCELLED', 'PAID')
    """)
    suspend fun getOutstandingForStudent(studentId: String): Long

    /**
     * Ambil semua tagihan untuk backup JSON.
     */
    @Query("SELECT * FROM charges ORDER BY created_at ASC")
    suspend fun getAllChargesForBackup(): List<ChargeEntity>

    // ─────────────────────── Payments ───────────────────────

    /**
     * Observasi daftar pembayaran untuk satu tagihan, urut kronologis.
     */
    @Query("SELECT * FROM payments WHERE charge_id = :chargeId ORDER BY paid_at ASC")
    fun getPaymentsForCharge(chargeId: String): Flow<List<PaymentEntity>>

    /**
     * Insert pembayaran baru.
     * WAJIB dijalankan di dalam @Transaction bersama updateCharge.
     * ABORT: pembayaran tidak boleh di-overwrite.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentEntity)

    /**
     * Hitung total yang sudah dibayar untuk satu tagihan.
     * Digunakan dalam @Transaction SEBELUM insert payment baru
     * untuk mencegah overpayment.
     * COALESCE memastikan return 0L jika belum ada pembayaran.
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE charge_id = :chargeId")
    suspend fun getTotalPaidForCharge(chargeId: String): Long

    /**
     * Ambil semua pembayaran untuk backup JSON.
     */
    @Query("SELECT * FROM payments ORDER BY paid_at ASC")
    suspend fun getAllPaymentsForBackup(): List<PaymentEntity>

    // ─────────────────────── Package Ledger ───────────────────────

    /**
     * Observasi seluruh riwayat ledger paket untuk satu enrollment, urut kronologis.
     */
    @Query("SELECT * FROM package_ledgers WHERE enrollment_id = :enrollmentId ORDER BY created_at ASC")
    fun getPackageLedger(enrollmentId: String): Flow<List<PackageLedgerEntity>>

    /**
     * Insert satu entri ledger baru (append-only — tidak ada UPDATE/DELETE).
     * WAJIB dijalankan di dalam @Transaction bersama attendance upsert.
     * ABORT: ID ganda tidak diizinkan.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLedgerEntry(entry: PackageLedgerEntity)

    /**
     * Observasi saldo paket aktual (SUM dinamis) untuk satu enrollment.
     * COALESCE memastikan return 0 jika belum ada ledger entry.
     */
    @Query("SELECT COALESCE(SUM(delta_sessions), 0) FROM package_ledgers WHERE enrollment_id = :enrollmentId")
    fun getRemainingSessions(enrollmentId: String): Flow<Int>

    /**
     * Ambil saldo paket (suspend) untuk logika bisnis dalam @Transaction.
     */
    @Query("SELECT COALESCE(SUM(delta_sessions), 0) FROM package_ledgers WHERE enrollment_id = :enrollmentId")
    suspend fun getRemainingSessionsSync(enrollmentId: String): Int

    /**
     * Hitung jumlah siswa dengan saldo paket rendah (≤ threshold) untuk dashboard alert.
     */
    @Query("""
        SELECT COUNT(*) FROM (
            SELECT pl.enrollment_id 
            FROM package_ledgers pl
            INNER JOIN enrollments e ON pl.enrollment_id = e.id
            WHERE e.billing_type = 'PACKAGE' AND e.status = 'ACTIVE'
            GROUP BY pl.enrollment_id
            HAVING SUM(pl.delta_sessions) <= :threshold
        )
    """)
    fun countLowBalanceEnrollments(threshold: Int): Flow<Int>

    /**
     * Ambil semua ledger untuk backup JSON.
     */
    @Query("SELECT * FROM package_ledgers ORDER BY created_at ASC")
    suspend fun getAllLedgerEntriesForBackup(): List<PackageLedgerEntity>
}
