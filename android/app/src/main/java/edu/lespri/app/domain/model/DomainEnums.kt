package edu.lespri.app.domain.model

// =============================================================================
// LESPRI — Domain Enums & Invariant Constants
// =============================================================================
// All enums are pure Kotlin — ZERO Android/Room imports.
// Room stores these as String via @TypeConverter in LespriDatabase.
// =============================================================================

// ---------------------------------------------------------------------------
// 0. ENTITY STATUSES — status umum entitas domain
// ---------------------------------------------------------------------------

/**
 * Status siswa (Student).
 */
enum class StudentStatus {
    ACTIVE,
    ARCHIVED
}

/**
 * Status tutor / pengajar.
 */
enum class TutorStatus {
    ACTIVE,
    ARCHIVED
}

/**
 * Jenis kelas bimbel.
 */
enum class ClassType {
    PRIVATE,
    GROUP
}

/**
 * Skema penagihan kelas (Billing Type).
 */
enum class BillingType {
    MONTHLY,
    PACKAGE,
    PER_SESSION
}

/**
 * Metode pembayaran tagihan.
 */
enum class PaymentMethod {
    CASH,
    TRANSFER,
    OTHER
}

/**
 * Tab navigasi di layar Keuangan (P16).
 */
enum class FinanceTab {
    CHARGES,
    PAYMENTS,
    PACKAGES
}

// ---------------------------------------------------------------------------
// 1. ENROLLMENT — status seorang siswa dalam suatu kelas
// ---------------------------------------------------------------------------

/**
 * Status pendaftaran (enrollment) seorang siswa ke sebuah kelas.
 *
 * State machine:
 *   ACTIVE ──▶ COMPLETED   (sesi paket habis atau admin menutup)
 *   ACTIVE ──▶ WITHDRAWN   (siswa mengundurkan diri)
 *   ACTIVE ──▶ SUSPENDED   (sementara tidak aktif, bisa aktif kembali)
 *   SUSPENDED ──▶ ACTIVE
 */
enum class EnrollmentStatus {
    /** Siswa aktif mengikuti kelas — saldo paket bisa terpotong. */
    ACTIVE,

    /** Paket sesi habis atau admin menyelesaikan enrollment. */
    COMPLETED,

    /** Siswa mengundurkan diri. Soft-delete, data tetap tersimpan. */
    WITHDRAWN,

    /** Enrollment ditangguhkan sementara (misal: izin panjang). */
    SUSPENDED
}

// ---------------------------------------------------------------------------
// 2. ATTENDANCE — status kehadiran per sesi per siswa
// ---------------------------------------------------------------------------

/**
 * Status kehadiran seorang siswa pada satu sesi pelajaran.
 *
 * Hanya status [PRESENT] yang men-trigger pemotongan saldo paket.
 * Perubahan dari [PRESENT] ke status lain meng-insert reversal di ledger.
 */
enum class AttendanceStatus {
    /**
     * Belum diisi — status awal saat sesi dibuat.
     * Tidak memotong saldo paket.
     */
    UNRECORDED,

    /**
     * Hadir — satu sesi paket terpotong dari ledger.
     */
    PRESENT,

    /**
     * Izin terencana (libur resmi, kegiatan sekolah, dll.).
     * Tidak memotong saldo.
     */
    EXCUSED,

    /**
     * Sakit — dilengkapi surat dokter jika perlu.
     * Tidak memotong saldo.
     */
    SICK,

    /**
     * Absen tanpa keterangan.
     * Tidak memotong saldo, namun dicatat untuk pelaporan.
     */
    ABSENT
}

// ---------------------------------------------------------------------------
// 3. SESSION — status sesi pelajaran
// ---------------------------------------------------------------------------

/**
 * Status sebuah sesi pelajaran (LessonSession).
 *
 * State machine:
 *   SCHEDULED ──▶ COMPLETED   (sesi berlangsung normal)
 *   SCHEDULED ──▶ CANCELLED   (sesi dibatalkan → attendance reversal cascade)
 *   CANCELLED ──▶ SCHEDULED   (rescheduled / dibuka kembali)
 */
enum class SessionStatus {
    /** Sesi terjadwal, belum berlangsung. */
    SCHEDULED,

    /** Sesi telah selesai berlangsung. */
    COMPLETED,

    /**
     * Sesi dibatalkan.
     * Cascade: semua AttendanceRecord sesi ini dibalikkan (reversal),
     * saldo paket siswa yang sudah terpotong dikembalikan secara atomik.
     */
    CANCELLED
}

// ---------------------------------------------------------------------------
// 4. CHARGE — tipe dan status tagihan keuangan
// ---------------------------------------------------------------------------

/**
 * Tipe tagihan yang dikenakan kepada siswa atau paket enrollment.
 */
enum class ChargeType {
    /** Biaya bulanan tetap. */
    MONTHLY_FEE,

    /** Biaya pendaftaran awal. */
    REGISTRATION_FEE,

    /** Pembelian paket sesi (misal: 8 sesi, 12 sesi). */
    SESSION_PACKAGE,

    /** Tagihan lain yang tidak masuk kategori di atas. */
    OTHER
}

/**
 * Status pembayaran sebuah tagihan (Charge).
 */
enum class ChargeStatus {
    /** Tagihan draf, belum diterbitkan secara resmi. */
    DRAFT,

    /** Tagihan belum dibayar sama sekali. */
    UNPAID,

    /** Tagihan dibayar sebagian (sisa masih ada). */
    PARTIALLY_PAID,

    /** Tagihan sudah lunas. */
    PAID,

    /** Tagihan dibatalkan. */
    CANCELLED
}

// ---------------------------------------------------------------------------
// 5. LEDGER — tipe entri buku besar saldo paket sesi
// ---------------------------------------------------------------------------

/**
 * Tipe entri di [PackageLedgerEntity].
 * Ledger bersifat append-only — tidak ada UPDATE/DELETE, hanya INSERT.
 *
 * Saldo aktual dihitung dengan: `SUM(delta_sessions)` per enrollment_id.
 */
enum class LedgerEntryType {
    /**
     * Top-up saldo — terjadi saat enrollment paket baru dibuat.
     * delta_sessions = +N (jumlah sesi dalam paket).
     */
    PACKAGE_PURCHASE,

    /**
     * Pemotongan saldo — terjadi saat status kehadiran diubah ke PRESENT.
     * delta_sessions = -1.
     */
    ATTENDANCE_DEDUCTION,

    /**
     * Pengembalian saldo — terjadi saat status diubah dari PRESENT ke
     * status lain, atau saat sesi dibatalkan (cascade).
     * delta_sessions = +1.
     */
    ATTENDANCE_REVERSAL,

    /**
     * Penyesuaian manual oleh admin (misal: koreksi kesalahan).
     * delta_sessions = nilai positif atau negatif.
     */
    MANUAL_ADJUSTMENT
}

// ---------------------------------------------------------------------------
// 6. NOTIFICATION — tipe pengingat
// ---------------------------------------------------------------------------

/**
 * Tipe notifikasi / pengingat yang dapat dijadwalkan.
 */
enum class NotificationType {
    /** Pengingat tagihan jatuh tempo. */
    PAYMENT_DUE,

    /** Pengingat sesi akan segera berlangsung (H-1 atau H-pagi). */
    SESSION_REMINDER,

    /** Paket sesi hampir habis (threshold: misal 2 sesi tersisa). */
    LOW_SESSION_BALANCE
}

// ---------------------------------------------------------------------------
// 7. SCHEDULE RULE — hari berulang
// ---------------------------------------------------------------------------

/**
 * Representasi hari dalam seminggu untuk aturan jadwal berulang.
 * Nilai ordinal selaras dengan [java.util.Calendar.DAY_OF_WEEK] — 1 = Sunday, dst.
 * Gunakan [displayName] untuk tampilan UI berbahasa Indonesia.
 */
enum class DayOfWeek(val displayName: String) {
    MONDAY("Senin"),
    TUESDAY("Selasa"),
    WEDNESDAY("Rabu"),
    THURSDAY("Kamis"),
    FRIDAY("Jumat"),
    SATURDAY("Sabtu"),
    SUNDAY("Minggu")
}

// ---------------------------------------------------------------------------
// 8. APP SETTINGS — tipe konfigurasi aplikasi
// ---------------------------------------------------------------------------

/**
 * Kunci pengaturan yang disimpan di [AppSettingEntity].
 * Menggunakan enum agar type-safe dan mudah di-refactor.
 */
enum class AppSettingKey {
    /** Nama bimbingan belajar (string). */
    BIMBEL_NAME,

    /** Nama pemilik / admin (string). */
    OWNER_NAME,

    /** Nomor WhatsApp untuk share pesan (string E.164, misal "+6281234567890"). */
    WHATSAPP_NUMBER,

    /** Apakah banner AdMob ditampilkan ("true"/"false"). */
    ADS_ENABLED,

    /** Threshold sisa sesi untuk memunculkan notifikasi low balance (integer string). */
    LOW_SESSION_THRESHOLD,

    /** Jam pengingat tagihan harian dalam format "HH:mm" (string). */
    REMINDER_TIME,

    /** Preferensi bahasa aplikasi ("system", "id", "en"). */
    LANGUAGE,

    /** Preferensi tema tampilan ("system", "light", "dark"). */
    THEME
}

/**
 * Mode tema aplikasi.
 */
enum class AppTheme(val storageValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorageValue(value: String?): AppTheme {
            return entries.firstOrNull { it.storageValue.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}

/**
 * Pilihan bahasa aplikasi.
 */
enum class AppLanguage(val storageValue: String, val localeTag: String) {
    SYSTEM("system", ""),
    INDONESIAN("id", "in"),
    ENGLISH("en", "en");

    companion object {
        fun fromStorageValue(value: String?): AppLanguage {
            return entries.firstOrNull { it.storageValue.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}

// =============================================================================
// INVARIANT CONSTANTS
// =============================================================================

/**
 * Konstanta bisnis yang bersifat tetap dan tidak boleh diubah sembarangan.
 * Semua nilai numeris bisnis HARUS diambil dari sini, bukan hardcoded di tempat lain.
 */
object LespriConstants {

    // --- Sesi & Paket ---

    /** Threshold sisa sesi paket untuk memunculkan peringatan "hampir habis". */
    const val DEFAULT_LOW_SESSION_THRESHOLD = 2

    /** Jumlah sesi maksimum per paket yang bisa dibeli dalam satu transaksi. */
    const val MAX_SESSIONS_PER_PACKAGE = 100

    /** Minimum sesi paket yang bisa dibeli. */
    const val MIN_SESSIONS_PER_PACKAGE = 1

    // --- Keuangan ---

    /** Nilai minimum pembayaran yang diterima (dalam Rupiah). */
    const val MIN_PAYMENT_AMOUNT = 1_000L

    /** Nilai maksimum tagihan per entri (guard terhadap input error). */
    const val MAX_CHARGE_AMOUNT = 100_000_000L // 100 juta rupiah

    // --- Jadwal ---

    /** Jangka waktu (hari) ke depan untuk auto-generate sesi dari aturan jadwal. */
    const val SCHEDULE_GENERATION_WINDOW_DAYS = 30

    // --- Notifikasi ---

    /** Channel ID untuk notifikasi tagihan. */
    const val NOTIFICATION_CHANNEL_PAYMENT = "lespri_payment_reminders"

    /** Channel ID untuk notifikasi sesi. */
    const val NOTIFICATION_CHANNEL_SESSION = "lespri_session_reminders"

    /** Channel ID untuk notifikasi saldo paket rendah. */
    const val NOTIFICATION_CHANNEL_LOW_BALANCE = "lespri_low_balance"

    // --- Database ---

    /** Nama file database SQLite. */
    const val DATABASE_NAME = "lespri.db"

    /** Versi database saat ini. Naikkan bersama migrasi Room setiap ada perubahan skema. */
    const val DATABASE_VERSION = 1

    // --- AdMob (Test IDs — ganti saat release produksi) ---

    /** Test AdMob App ID. */
    const val ADMOB_TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    /** Test Banner Ad Unit ID. */
    const val ADMOB_TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

    // --- UI ---

    /** Tinggi banner AdMob dalam dp (adaptive banner safety margin). */
    const val BANNER_AD_HEIGHT_DP = 72

    /** Jeda debounce (ms) untuk field pencarian agar tidak spam query. */
    const val SEARCH_DEBOUNCE_MS = 300L
}
