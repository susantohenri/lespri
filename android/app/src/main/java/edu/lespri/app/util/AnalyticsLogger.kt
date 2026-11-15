package edu.lespri.app.util

import android.content.Context
import android.os.Bundle
import com.google.android.gms.ads.AdValue
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper terpusat untuk Firebase Analytics dan Firebase Crashlytics.
 * Menangani pencatatan screen view, event aksi, impresi monetisasi AdMob,
 * User Properties, dan pelaporan error non-fatal.
 */
@Singleton
class AnalyticsLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    /**
     * Mencatat screen view secara eksplisit atau otomatis via NavController listener.
     */
    fun logScreenView(screenName: String, screenClass: String = "MainActivity") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Mencatat aksi pengguna atau event bisnis.
     */
    fun logEvent(eventName: String, params: Map<String, Any?> = emptyMap()) {
        val bundle = Bundle().apply {
            for ((key, value) in params) {
                when (value) {
                    null -> {}
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    /**
     * Mencatat event monetisasi iklan AdMob via OnPaidEventListener.
     * Mengikuti format standar Google Firebase Ad Revenue.
     */
    fun logAdImpression(
        adValue: AdValue,
        adUnitId: String,
        format: String = "BANNER",
        adSourceName: String? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.AD_PLATFORM, "AdMob")
            putString(FirebaseAnalytics.Param.AD_SOURCE, adSourceName ?: "AdMob")
            putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId)
            putString(FirebaseAnalytics.Param.AD_FORMAT, format)
            putDouble(FirebaseAnalytics.Param.VALUE, adValue.valueMicros / 1_000_000.0)
            putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
            putInt("precision_type", adValue.precisionType)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)
    }

    /**
     * Menetapkan User Property untuk segmentasi analitik.
     */
    fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    /**
     * Mencatat exception non-fatal ke Firebase Crashlytics.
     */
    fun logException(throwable: Throwable, message: String? = null) {
        if (!message.isNullOrBlank()) {
            crashlytics.log(message)
        }
        crashlytics.recordException(throwable)
    }

    /**
     * Menambahkan custom key/value ke log Crashlytics untuk mempermudah debugging crash.
     */
    fun setCrashlyticsCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}
