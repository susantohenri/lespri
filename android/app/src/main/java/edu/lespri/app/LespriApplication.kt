package edu.lespri.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import edu.lespri.app.notification.ReminderScheduler
import edu.lespri.app.util.AnalyticsLogger
import javax.inject.Inject

/**
 * Entry point aplikasi Lespri dengan inisialisasi Dagger-Hilt, Firebase, Crashlytics,
 * dan Channel Notifikasi.
 */
@HiltAndroidApp
class LespriApplication : Application() {

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate() {
        super.onCreate()

        // Pastikan FirebaseApp terinisialisasi
        FirebaseApp.initializeApp(this)

        // Konfigurasi Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
        }

        // Set User Property awal
        analyticsLogger.setUserProperty("app_version", BuildConfig.VERSION_NAME)

        // Inisialisasi channel notifikasi lokal
        ReminderScheduler.initChannels(this)
    }
}
