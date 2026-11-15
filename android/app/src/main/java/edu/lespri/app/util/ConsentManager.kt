package edu.lespri.app.util

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.lespri.app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manajer GDPR / Privacy Consent menggunakan Google User Messaging Platform (UMP) SDK.
 * Mengelola alur pengumpulan persetujuan privasi pengguna sesuai kepatuhan Google AdMob & Play Store.
 */
@Singleton
class ConsentManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val consentInformation: ConsentInformation by lazy {
        UserMessagingPlatform.getConsentInformation(context)
    }

    /**
     * Memeriksa apakah aplikasi diizinkan meminta iklan berdasarkan status consent saat ini.
     */
    fun canRequestAds(): Boolean {
        return consentInformation.canRequestAds()
    }

    /**
     * Memeriksa apakah tombol opsi privasi (Privacy Options) wajib ditampilkan kepada pengguna.
     */
    fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    /**
     * Memulai alur pengumpulan consent saat startup aplikasi.
     * Mengambil info consent terbaru dari server Google dan menampilkan form jika diperlukan.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGathered: (canRequestAds: Boolean) -> Unit
    ) {
        val debugSettingsBuilder = ConsentDebugSettings.Builder(activity)
        if (BuildConfig.DEBUG) {
            // Aktifkan emulasi geografi EEA untuk pengujian GDPR di mode debug jika diperlukan
            // debugSettingsBuilder.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        }

        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettingsBuilder.build())
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Berhasil memperbarui info consent, muat dan tampilkan form jika diperlukan
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        // Form error dicatat atau diabaikan secara aman
                    }
                    onConsentGathered(canRequestAds())
                }
            },
            { requestConsentError ->
                // Jika gagal mengambil info consent (misal offline), periksa status lokal yang ada
                onConsentGathered(canRequestAds())
            }
        )
    }

    /**
     * Menampilkan form pengaturan privasi / consent (dipanggil dari menu Settings).
     */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onDismissed: (formError: FormError?) -> Unit = {}
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            onDismissed(formError)
        }
    }

    /**
     * Mereset consent lokal (khusus untuk keperluan debugging & testing).
     */
    fun resetConsent() {
        consentInformation.reset()
    }
}
