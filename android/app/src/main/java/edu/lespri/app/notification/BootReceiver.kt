package edu.lespri.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver yang menerima event device reboot.
 * Tugasnya: memastikan notification channel tetap terdaftar
 * dan menjadwalkan ulang alarm yang perlu aktif setelah boot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {

            // Pastikan notification channel tersedia
            ReminderScheduler.initChannels(context)
        }
    }
}
