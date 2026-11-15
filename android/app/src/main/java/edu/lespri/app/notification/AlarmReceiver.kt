package edu.lespri.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver yang menerima alarm dari [ReminderScheduler].
 *
 * Sejak Android 10 (API 29), Activity tidak dapat diluncurkan dari background,
 * sehingga PendingIntent alarm harus berupa broadcast ke receiver ini.
 * Receiver kemudian menampilkan notifikasi lokal melalui NotificationManager.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_TITLE) ?: "Lespri Bimbel"
        val message = intent.getStringExtra(ReminderScheduler.EXTRA_MESSAGE)
            ?: "Ada agenda bimbingan belajar."
        val channelId = intent.getStringExtra(ReminderScheduler.EXTRA_CHANNEL_ID)
            ?: ReminderScheduler.CHANNEL_SCHEDULE
        val requestId = intent.getStringExtra(ReminderScheduler.EXTRA_REQUEST_ID)
            ?: System.currentTimeMillis().toString()

        ReminderScheduler.showNotification(
            context = context,
            channelId = channelId,
            title = title,
            message = message,
            requestId = requestId
        )
    }
}
