package edu.lespri.app.util

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import edu.lespri.app.domain.model.LespriConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manajer iklan AdMob dan integrasi Firebase Remote Config.
 *
 * Tanggung jawab:
 * - Inisialisasi MobileAds saat aplikasi pertama kali dijalankan
 * - Menyediakan Banner Ad Unit ID (test ID untuk development)
 * - Membaca toggle iklan dari Remote Config / AppSetting (ADS_ENABLED)
 * - Menyediakan status apakah banner iklan boleh ditampilkan
 */
@Singleton
class AdManager @Inject constructor(
    private val application: Application
) {

    private val _adsEnabled = MutableStateFlow(false)
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    /** Apakah inisialisasi AdMob telah dilakukan. */
    var isInitialized: Boolean = false
        private set

    /**
     * Inisialisasi MobileAds. Harus dipanggil sekali saat aplikasi mulai
     * (biasanya di [LespriApplication.onCreate] atau [MainActivity.onCreate]).
     */
    fun initialize() {
        if (isInitialized) return
        MobileAds.initialize(application) { }
        isInitialized = true
    }

    /**
     * Banner Ad Unit ID. Menggunakan test ID dari [LespriConstants.ADMOB_TEST_BANNER_ID]
     * selama development. Ganti dengan unit ID produksi sebelum release.
     */
    val bannerAdUnitId: String
        get() = LespriConstants.ADMOB_TEST_BANNER_ID

    /**
     * App ID AdMob (digunakan di AndroidManifest via manifestPlaceholders).
     */
    val admobAppId: String
        get() = LespriConstants.ADMOB_TEST_APP_ID

    /**
     * Mengaktifkan mode tes untuk perangkat yang terdaftar (termasuk emulator).
     * Agar iklan yang ditampilkan adalah iklan tes, bukan iklan produksi.
     */
    fun enableTestMode(testDeviceIds: List<String>) {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
        )
    }

    /**
     * ID tes emulator Android yang dapat digunakan dengan [enableTestMode].
     */
    companion object {
        const val TEST_DEVICE_EMULATOR = "EMULATOR"
    }

    /**
     * Memperbarui status tampilan iklan berdasarkan nilai Remote Config / pengaturan.
     * Dipanggil setelah fetch Remote Config atau saat pengguna mengubah pengaturan.
     */
    fun updateAdsEnabled(enabled: Boolean) {
        _adsEnabled.value = enabled
    }

    /**
     * Mengambil status iklan dari Firebase Remote Config.
     * Jika Remote Config tidak tersedia atau gagal, tetap menggunakan nilai default.
     */
    suspend fun fetchAdsEnabledFromRemoteConfig() {
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            remoteConfig.setDefaultsAsync(mapOf("ads_enabled" to false))
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val enabled = remoteConfig.getBoolean("ads_enabled")
                    updateAdsEnabled(enabled)
                } else {
                    updateAdsEnabled(false)
                }
            }
        } catch (e: Exception) {
            updateAdsEnabled(false)
        }
    }
}
