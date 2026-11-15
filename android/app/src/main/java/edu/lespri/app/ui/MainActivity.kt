package edu.lespri.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                val ownerProfileState = settingDao.getOwnerProfile().collectAsState(initial = "LOADING")
                val navController = rememberNavController()

                when (val profile = ownerProfileState.value) {
                    "LOADING" -> {
                        // Tampilkan loading sebentar saat database Room sedang query di thread IO
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        val owner = profile as? edu.lespri.app.data.local.entity.OwnerProfileEntity
                        val startDestination = if (owner != null && owner.businessName.isNotBlank()) {
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
    }
}
