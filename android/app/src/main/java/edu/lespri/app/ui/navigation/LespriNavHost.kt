package edu.lespri.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.lespri.app.ui.classroom.ClassDetailScreen
import edu.lespri.app.ui.classroom.ClassFormScreen
import edu.lespri.app.ui.classroom.ClassListScreen
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.dashboard.DashboardScreen
import edu.lespri.app.ui.setup.FirstLaunchSetupScreen
import edu.lespri.app.ui.student.StudentDetailScreen
import edu.lespri.app.ui.student.StudentFormScreen
import edu.lespri.app.ui.student.StudentListScreen
import edu.lespri.app.ui.finance.ChargeDetailScreen
import edu.lespri.app.ui.finance.FinanceOverviewScreen
import edu.lespri.app.ui.finance.PaymentReceiptScreen
import edu.lespri.app.ui.finance.RecordPaymentScreen
import edu.lespri.app.ui.tutor.TutorFormScreen
import edu.lespri.app.ui.tutor.TutorListScreen

/**
 * Host Navigasi Pusat aplikasi Lespri (Milestone 4).
 */
@Composable
fun LespriNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- P01: Onboarding / Setup Awal ---
        composable(NavRoutes.FirstLaunchSetup.route) {
            FirstLaunchSetupScreen(
                onSetupComplete = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.FirstLaunchSetup.route) { inclusive = true }
                    }
                }
            )
        }

        // --- P02: Dashboard ---
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToSession = {
                    // Detail sesi baru tersedia pada Milestone 5.
                }
            )
        }

        // --- P03: Daftar Siswa ---
        composable(NavRoutes.Students.route) {
            StudentListScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onAddStudent = { navController.navigate(NavRoutes.StudentForm.createRoute()) },
                onOpenStudent = { studentId ->
                    navController.navigate(NavRoutes.StudentDetail.createRoute(studentId))
                }
            )
        }

        // --- P04: Form Siswa ---
        composable(
            route = NavRoutes.StudentForm.route,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            StudentFormScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P05: Detail Siswa ---
        composable(
            route = NavRoutes.StudentDetail.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) {
            StudentDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onEditStudent = { studentId ->
                    navController.navigate(NavRoutes.StudentForm.createRoute(studentId))
                },
                onOpenClass = { classId ->
                    navController.navigate(NavRoutes.ClassDetail.createRoute(classId))
                },
                onArchived = { navController.popBackStack() }
            )
        }

        // --- P06: Daftar Kelas ---
        composable(NavRoutes.Classes.route) {
            ClassListScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onAddClass = { navController.navigate(NavRoutes.ClassForm.createRoute()) },
                onOpenClass = { classId ->
                    navController.navigate(NavRoutes.ClassDetail.createRoute(classId))
                }
            )
        }

        // --- P07: Form Kelas ---
        composable(
            route = NavRoutes.ClassForm.route,
            arguments = listOf(
                navArgument("classId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ClassFormScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P08: Detail Kelas ---
        composable(
            route = NavRoutes.ClassDetail.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) {
            ClassDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onEditClass = { classId ->
                    navController.navigate(NavRoutes.ClassForm.createRoute(classId))
                },
                onOpenStudent = { studentId ->
                    navController.navigate(NavRoutes.StudentDetail.createRoute(studentId))
                },
                onOpenSession = {
                    // Detail sesi baru tersedia pada Milestone 5.
                },
                onArchived = { navController.popBackStack() }
            )
        }

        // --- P09: Daftar Tutor ---
        composable(NavRoutes.Tutors.route) {
            TutorListScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onAddTutor = { navController.navigate(NavRoutes.TutorForm.createRoute()) },
                onEditTutor = { tutorId ->
                    navController.navigate(NavRoutes.TutorForm.createRoute(tutorId))
                }
            )
        }

        // --- P10: Form Tutor ---
        composable(
            route = NavRoutes.TutorForm.route,
            arguments = listOf(
                navArgument("tutorId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            TutorFormScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P11: Detail Tutor (alias form edit) ---
        composable(
            route = NavRoutes.TutorDetail.route,
            arguments = listOf(navArgument("tutorId") { type = NavType.StringType })
        ) {
            TutorFormScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSaved = { navController.popBackStack() }
            )
        }

        // --- P12: Jadwal & Agenda (Milestone 5) ---
        composable(NavRoutes.Schedule.route) {
            PlaceholderScreen(
                title = "Jadwal & Agenda",
                route = NavRoutes.Schedule.route,
                navController = navController
            )
        }

        // --- P13-P15: Form, Detail Sesi & Presensi (Milestone 5) ---
        composable(
            route = NavRoutes.SessionForm.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("classId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            PlaceholderScreen(
                title = "Form Jadwal",
                route = NavRoutes.SessionForm.route,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            PlaceholderScreen(
                title = "Detail Sesi",
                route = NavRoutes.SessionDetail.route,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.AttendanceSheet.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            PlaceholderScreen(
                title = "Pencatatan Kehadiran",
                route = NavRoutes.AttendanceSheet.route,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // --- P16: Keuangan (Milestone 6) ---
        composable(NavRoutes.Finance.route) {
            FinanceOverviewScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P17: Detail Tagihan (Milestone 6) ---
        composable(
            route = NavRoutes.ChargeDetail.route,
            arguments = listOf(navArgument("chargeId") { type = NavType.StringType })
        ) {
            ChargeDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P18: Catat Pembayaran (Milestone 6) ---
        composable(
            route = NavRoutes.RecordPayment.route,
            arguments = listOf(navArgument("chargeId") { type = NavType.StringType })
        ) {
            RecordPaymentScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- P19: Kuitansi Pembayaran (Milestone 6) ---
        composable(
            route = NavRoutes.PaymentReceipt.route,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) {
            PaymentReceiptScreen(
                onBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // --- Menu Drawer Sekunder ---
        composable(NavRoutes.Reports.route) {
            PlaceholderScreen(title = "Laporan Operasional", route = NavRoutes.Reports.route, navController = navController)
        }
        composable(NavRoutes.CsvImport.route) {
            PlaceholderScreen(title = "Impor CSV", route = NavRoutes.CsvImport.route, navController = navController)
        }
        composable(NavRoutes.CsvExport.route) {
            PlaceholderScreen(title = "Ekspor CSV", route = NavRoutes.CsvExport.route, navController = navController)
        }
        composable(NavRoutes.BackupRestore.route) {
            PlaceholderScreen(title = "Backup & Restore", route = NavRoutes.BackupRestore.route, navController = navController)
        }
        composable(NavRoutes.Settings.route) {
            PlaceholderScreen(title = "Pengaturan", route = NavRoutes.Settings.route, navController = navController)
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    route: String,
    navController: NavHostController,
    onBack: (() -> Unit)? = null
) {
    LespriScaffold(
        currentRoute = route,
        onNavigateToRoute = { dest -> navController.navigate(dest) },
        title = title,
        onBack = onBack
    ) {
        EmptyStateView(
            title = title,
            description = "Fitur ini akan segera diaktifkan pada milestone berikutnya.",
            actionLabel = "Kembali ke Beranda",
            onActionClick = {
                navController.navigate(NavRoutes.Dashboard.route) {
                    popUpTo(NavRoutes.Dashboard.route) { inclusive = true }
                }
            }
        )
    }
}
