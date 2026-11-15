package edu.lespri.app.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.lespri.app.domain.model.AppLanguage
import edu.lespri.app.domain.model.AppSettingKey
import edu.lespri.app.domain.model.AppTheme
import edu.lespri.app.domain.model.OwnerProfile
import edu.lespri.app.domain.repository.SettingRepository
import edu.lespri.app.ui.navigation.LespriNavHost
import edu.lespri.app.ui.navigation.NavRoutes
import edu.lespri.app.ui.theme.LespriTheme
import edu.lespri.app.util.AdManager
import edu.lespri.app.util.AnalyticsLogger
import edu.lespri.app.util.ConsentManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Activity aplikasi Lespri.
 * Menangani inisialisasi UMP Consent, integrasi AdMob, tema dinamis,
 * bahasa aplikasi (i18n), dan tracking otomatis navigasi screen ke Firebase Analytics.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var adManager: AdManager

    @Inject
    lateinit var consentManager: ConsentManager

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Kumpulkan GDPR Consent via UMP SDK sebelum inisialisasi MobileAds
        consentManager.gatherConsent(this) { canRequestAds ->
            if (canRequestAds) {
                adManager.initialize()
            }
        }

        // 2. Sinkronisasi status iklan dari Firebase Remote Config
        lifecycleScope.launch {
            adManager.fetchAdsEnabledFromRemoteConfig()
        }

        // 3. Listener preferensi bahasa untuk AppCompatDelegate (Android 13+ & backwards-compatible)
        lifecycleScope.launch {
            settingRepository.getSetting(AppSettingKey.LANGUAGE).collect { langPref ->
                val appLang = AppLanguage.fromStorageValue(langPref)
                val targetLocales = if (appLang == AppLanguage.SYSTEM) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(appLang.localeTag)
                }

                if (AppCompatDelegate.getApplicationLocales() != targetLocales) {
                    AppCompatDelegate.setApplicationLocales(targetLocales)
                }
            }
        }

        setContent {
            // Amati preferensi tema tampilan dari repository secara reaktif
            val themePrefState by settingRepository.getSetting(AppSettingKey.THEME).collectAsState(initial = "system")
            val appTheme = AppTheme.fromStorageValue(themePrefState)

            LespriTheme(appTheme = appTheme) {
                val ownerProfileState = settingRepository.getOwnerProfile().collectAsState(initial = "LOADING")
                val navController = rememberNavController()

                // Pasang DestinationChangedListener untuk mencatat screen view otomatis
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        val route = destination.route ?: "unknown"
                        analyticsLogger.logScreenView(screenName = route, screenClass = "MainActivity")
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                when (val profile = ownerProfileState.value) {
                    "LOADING" -> {
                        // Tampilkan loading sebentar saat database Room sedang query di thread IO
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        val owner = profile as? OwnerProfile
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
