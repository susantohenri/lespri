package edu.lespri.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import edu.lespri.app.notification.ReminderScheduler

/**
 * Entry point aplikasi Lespri dengan inisialisasi Dagger-Hilt.
 */
@HiltAndroidApp
class LespriApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi channel notifikasi lokal
        ReminderScheduler.initChannels(this)
    }
}
