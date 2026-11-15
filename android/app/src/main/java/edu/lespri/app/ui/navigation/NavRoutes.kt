package edu.lespri.app.ui.navigation

/**
 * Definisi rute navigasi aplikasi Lespri sesuai SCREEN-MAP.md.
 */
sealed class NavRoutes(val route: String) {

    // --- Onboarding & Setup ---
    object FirstLaunchSetup : NavRoutes("first_launch_setup")

    // --- 5 Destinasi Utama Bottom Navigation ---
    object Dashboard : NavRoutes("dashboard")
    object Students : NavRoutes("students")
    object Classes : NavRoutes("classes")
    object Schedule : NavRoutes("schedule")
    object Finance : NavRoutes("finance")

    // --- Destinasi Drawer / Menu Sekunder ---
    object Tutors : NavRoutes("tutors")
    object Reports : NavRoutes("reports")
    object CsvImport : NavRoutes("import_csv")
    object CsvExport : NavRoutes("export_csv")
    object BackupRestore : NavRoutes("backup_restore")
    object Settings : NavRoutes("settings")

    // --- Form & Detail Flow ---
    object StudentForm : NavRoutes("student_form?studentId={studentId}") {
        fun createRoute(studentId: String? = null): String {
            return if (studentId != null) "student_form?studentId=$studentId" else "student_form"
        }
    }

    object StudentDetail : NavRoutes("student_detail/{studentId}") {
        fun createRoute(studentId: String): String = "student_detail/$studentId"
    }

    object ClassForm : NavRoutes("class_form?classId={classId}") {
        fun createRoute(classId: String? = null): String {
            return if (classId != null) "class_form?classId=$classId" else "class_form"
        }
    }

    object ClassDetail : NavRoutes("class_detail/{classId}") {
        fun createRoute(classId: String): String = "class_detail/$classId"
    }

    object TutorForm : NavRoutes("tutor_form?tutorId={tutorId}") {
        fun createRoute(tutorId: String? = null): String {
            return if (tutorId != null) "tutor_form?tutorId=$tutorId" else "tutor_form"
        }
    }

    object TutorDetail : NavRoutes("tutor_detail/{tutorId}") {
        fun createRoute(tutorId: String): String = "tutor_detail/$tutorId"
    }

    object SessionForm : NavRoutes("session_form?sessionId={sessionId}&classId={classId}") {
        fun createRoute(sessionId: String? = null, classId: String? = null): String {
            return "session_form?sessionId=${sessionId.orEmpty()}&classId=${classId.orEmpty()}"
        }
    }

    object SessionDetail : NavRoutes("session_detail/{sessionId}") {
        fun createRoute(sessionId: String): String = "session_detail/$sessionId"
    }

    object AttendanceSheet : NavRoutes("attendance_sheet/{sessionId}") {
        fun createRoute(sessionId: String): String = "attendance_sheet/$sessionId"
    }

    object ChargeDetail : NavRoutes("charge_detail/{chargeId}") {
        fun createRoute(chargeId: String): String = "charge_detail/$chargeId"
    }

    object RecordPayment : NavRoutes("record_payment/{chargeId}") {
        fun createRoute(chargeId: String): String = "record_payment/$chargeId"
    }

    object PaymentReceipt : NavRoutes("payment_receipt/{paymentId}") {
        fun createRoute(paymentId: String): String = "payment_receipt/$paymentId"
    }
}
