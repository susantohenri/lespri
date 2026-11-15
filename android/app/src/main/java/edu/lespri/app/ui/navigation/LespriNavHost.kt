package edu.lespri.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.lespri.app.ui.components.EmptyStateView
import edu.lespri.app.ui.components.LespriScaffold
import edu.lespri.app.ui.dashboard.DashboardScreen
import edu.lespri.app.ui.setup.FirstLaunchSetupScreen

/**
 * Host Navigasi Pusat aplikasi Lespri.
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
                onNavigateToSession = { sessionId ->
                    navController.navigate(NavRoutes.SessionDetail.createRoute(sessionId))
                }
            )
        }

        // --- P03: Daftar Siswa (Placeholder untuk Milestone 4) ---
        composable(NavRoutes.Students.route) {
            PlaceholderScreen(
                title = "Daftar Siswa",
                route = NavRoutes.Students.route,
                navController = navController
            )
        }

        // --- P06: Daftar Kelas (Placeholder untuk Milestone 4) ---
        composable(NavRoutes.Classes.route) {
            PlaceholderScreen(
                title = "Daftar Kelas",
                route = NavRoutes.Classes.route,
                navController = navController
            )
        }

        // --- P12: Jadwal (Placeholder untuk Milestone 5) ---
        composable(NavRoutes.Schedule.route) {
            PlaceholderScreen(
                title = "Jadwal & Agenda",
                route = NavRoutes.Schedule.route,
                navController = navController
            )
        }

        // --- P16: Keuangan (Placeholder untuk Milestone 6) ---
        composable(NavRoutes.Finance.route) {
            PlaceholderScreen(
                title = "Keuangan Bimbel",
                route = NavRoutes.Finance.route,
                navController = navController
            )
        }

        // --- Menu Drawer Sekunder ---
        composable(NavRoutes.Tutors.route) {
            PlaceholderScreen(title = "Daftar Tutor", route = NavRoutes.Tutors.route, navController = navController)
        }
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
    navController: NavHostController
) {
    LespriScaffold(
        currentRoute = route,
        onNavigateToRoute = { dest -> navController.navigate(dest) },
        title = title
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
