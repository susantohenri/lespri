package edu.lespri.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.ui.navigation.LespriNavHost
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.LespriTheme
import javax.inject.Inject

/**
 * Main Activity aplikasi Lespri.
 * Mengarahkan ke FirstLaunchSetupScreen (P01) atau DashboardScreen (P02) berdasarkan profil pemilik.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingDao: SettingDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LespriTheme {
                val ownerProfile by settingDao.getOwnerProfile().collectAsState(initial = null)
                val navController = rememberNavController()

                val startDestination = if (ownerProfile != null && ownerProfile?.businessName?.isNotBlank() == true) {
                    NavRoutes.Dashboard.route
                } else {
                    NavRoutes.FirstLaunchSetup.route
                }

                LespriNavHost(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}
