package edu.lespri.app.domain.model

/**
 * Kumpulan model agregasi & presentasi UI (UI Presentation & Composite Models).
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */

/**
 * Detail kelas lengkap dengan data relasional untuk layar ClassDetailScreen (P08).
 */
data class ClassGroupDetail(
    val classGroup: ClassGroup,
    val assignedTutor: Tutor? = null,
    val enrolledStudents: List<EnrolledStudentItem> = emptyList(),
    val upcomingSessions: List<LessonSession> = emptyList()
)

/**
 * Detail sesi pelajaran konkret dengan daftar kehadiran untuk AttendanceSheetScreen (P15) & SessionDetailScreen (P14).
 */
data class LessonSessionDetail(
    val session: LessonSession,
    val className: String,
    val tutorName: String? = null,
    val attendanceRecords: List<StudentAttendanceItem> = emptyList()
)

/**
 * Tagihan dengan status pelunasan dan saldo sisa tagihan.
 */
data class ChargeWithBalance(
    val charge: Charge,
    val totalPaid: Long,
    val remainingBalance: Long,
    val studentName: String,
    val className: String
)

/**
 * Item baris ringkas untuk daftar siswa pada StudentListScreen (P03).
 */
data class StudentListItem(
    val id: String,
    val name: String,
    val guardianPhone: String? = null,
    val schoolGrade: String? = null,
    val activeClassesCount: Int = 0,
    val totalDebt: Long = 0L,
    val status: StudentStatus = StudentStatus.ACTIVE
)

/**
 * Ringkasan kelas yang diikuti seorang siswa untuk StudentDetailScreen (P05).
 */
data class StudentClassItem(
    val enrollmentId: String,
    val classId: String,
    val className: String,
    val classType: ClassType,
    val subject: String? = null,
    val tutorName: String? = null,
    val billingType: BillingType,
    val feeAmount: Long,
    val remainingPackageSessions: Int? = null,
    val currentDebt: Long = 0L
)

/**
 * Sesi mendatang yang relevan bagi seorang siswa (StudentDetailScreen P05).
 */
data class StudentUpcomingSessionItem(
    val sessionId: String,
    val classId: String,
    val className: String,
    val sessionDate: String,
    val startTime: String,
    val status: SessionStatus
)

/**
 * Item baris ringkas untuk daftar tutor pada TutorListScreen (P09).
 */
data class TutorListItem(
    val id: String,
    val name: String,
    val phone: String? = null,
    val expertise: String? = null,
    val activeClassesCount: Int = 0,
    val status: TutorStatus = TutorStatus.ACTIVE
)

/**
 * Item baris ringkas untuk daftar kelas pada ClassListScreen (P06).
 */
data class ClassListItem(
    val id: String,
    val name: String,
    val classType: ClassType,
    val subject: String? = null,
    val tutorName: String? = null,
    val enrolledStudentsCount: Int = 0,
    val nextSessionDate: String? = null,
    val nextSessionTime: String? = null,
    val status: String = "ACTIVE"
)

/**
 * Siswa yang terdaftar pada suatu kelas (digunakan di daftar enrollment kelas).
 */
data class EnrolledStudentItem(
    val enrollmentId: String,
    val studentId: String,
    val studentName: String,
    val billingType: BillingType,
    val feeAmount: Long,
    val remainingPackageSessions: Int? = null,
    val currentDebt: Long = 0L
)

/**
 * Item sesi pada agenda jadwal (P12) yang sudah diperkaya nama kelas dan tutor.
 */
data class AgendaSessionItem(
    val session: LessonSession,
    val className: String,
    val tutorName: String? = null
)

/**
 * Item presensi siswa dalam satu sesi pelajaran.
 */
data class StudentAttendanceItem(
    val enrollmentId: String,
    val studentId: String,
    val studentName: String,
    val status: AttendanceStatus,
    val note: String? = null,
    val billingType: BillingType,
    val remainingPackageSessions: Int? = null
)

/**
 * Item riwayat pembayaran untuk FinanceOverviewScreen (P16).
 */
data class PaymentListItem(
    val id: String,
    val chargeId: String,
    val studentName: String,
    val className: String,
    val amount: Long,
    val paidAt: Long,
    val method: PaymentMethod,
    val referenceNote: String? = null
)

/**
 * Status saldo paket sesi per siswa untuk tab Paket di FinanceOverviewScreen (P16).
 */
data class PackageStatusItem(
    val enrollmentId: String,
    val studentName: String,
    val className: String,
    val totalPurchasedSessions: Int,
    val remainingSessions: Int
)

/**
 * Ringkasan hasil impor data CSV pada CsvImportScreen (P21).
 */
data class ImportSummary(
    val totalRows: Int,
    val importedCount: Int,
    val failedCount: Int,
    val errors: List<String> = emptyList()
)
