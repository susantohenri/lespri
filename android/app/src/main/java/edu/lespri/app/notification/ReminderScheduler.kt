package edu.lespri.app.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import edu.lespri.app.ui.MainActivity

/**
 * Penjadwal notifikasi lokal untuk pengingat jadwal, tagihan, attendance, dan paket hampir habis.
 * Menggunakan AlarmManager + NotificationManager.
 *
 * Channel notifikasi:
 * - SCHEDULE: pengingat jadwal pelajaran
 * - BILLING: pengingat tagihan jatuh tempo
 * - ATTENDANCE: pengingat pencatatan kehadiran
 * - PACKAGE: peringatan saldo paket hampir habis
 */
object ReminderScheduler {

    internal const val CHANNEL_SCHEDULE = "schedule_reminder"
    private const val CHANNEL_BILLING = "billing_reminder"
    private const val CHANNEL_ATTENDANCE = "attendance_reminder"
    private const val CHANNEL_PACKAGE = "package_reminder"

    internal const val EXTRA_TITLE = "extra_title"
    internal const val EXTRA_MESSAGE = "extra_message"
    internal const val EXTRA_CHANNEL_ID = "extra_channel_id"
    internal const val EXTRA_REQUEST_ID = "extra_request_id"

    private const val ACTION_SHOW_REMINDER = "edu.lespri.app.action.SHOW_REMINDER"

    private const val REQUEST_CODE_BASE = 1000

    /**
     * Inisialisasi semua notification channel.
     * Harus dipanggil sekali saat aplikasi dimulai (misal di Application.onCreate).
     */
    fun initChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val scheduleChannel = NotificationChannel(
            CHANNEL_SCHEDULE,
            "Pengingat Jadwal",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifikasi untuk jadwal pelajaran yang akan datang"
        }

        val billingChannel = NotificationChannel(
            CHANNEL_BILLING,
            "Pengingat Tagihan",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi untuk tagihan yang akan jatuh tempo"
        }

        val attendanceChannel = NotificationChannel(
            CHANNEL_ATTENDANCE,
            "Pengingat Kehadiran",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifikasi untuk pencatatan kehadiran yang belum dilakukan"
        }

        val packageChannel = NotificationChannel(
            CHANNEL_PACKAGE,
            "Peringatan Paket",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifikasi saat saldo paket sesi hampir habis"
        }

        manager.createNotificationChannels(
            listOf(scheduleChannel, billingChannel, attendanceChannel, packageChannel)
        )
    }

    /**
     * Jadwalkan notifikasi sekali untuk jadwal pelajaran.
     *
     * @param triggerMillis Waktu trigger dalam epoch milliseconds.
     * @param sessionId ID sesi pelajaran.
     * @param className Nama kelas.
     * @param startTime Waktu mulai sesi.
     */
    fun scheduleSessionReminder(
        context: Context,
        triggerMillis: Long,
        sessionId: String,
        className: String,
        startTime: String
    ) {
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_SCHEDULE,
            title = "Pengingat Jadwal",
            message = "Sesi $className dimulai pukul $startTime",
            requestId = sessionId
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hashRequestId("session_$sessionId"),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            triggerMillis,
            pendingIntent
        )
    }

    /**
     * Jadwalkan notifikasi pengingat tagihan.
     *
     * @param triggerMillis Waktu trigger dalam epoch milliseconds.
     * @param chargeId ID tagihan.
     * @param studentName Nama siswa.
     * @param amount Nominal tagihan.
     */
    fun scheduleBillingReminder(
        context: Context,
        triggerMillis: Long,
        chargeId: String,
        studentName: String,
        amount: Long
    ) {
        val amountStr = formatRupiah(amount)
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_BILLING,
            title = "Pengingat Tagihan",
            message = "Tagihan $studentName: $amountStr",
            requestId = chargeId
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hashRequestId("billing_$chargeId"),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            triggerMillis,
            pendingIntent
        )
    }

    /**
     * Jadwalkan notifikasi pengingat pencatatan kehadiran.
     */
    fun scheduleAttendanceReminder(
        context: Context,
        triggerMillis: Long,
        sessionId: String,
        className: String
    ) {
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_ATTENDANCE,
            title = "Pengingat Kehadiran",
            message = "Catat kehadiran untuk $className",
            requestId = sessionId
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hashRequestId("attendance_$sessionId"),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            triggerMillis,
            pendingIntent
        )
    }

    /**
     * Jadwalkan notifikasi peringatan saldo paket hampir habis.
     */
    fun schedulePackageLowBalanceReminder(
        context: Context,
        triggerMillis: Long,
        enrollmentId: String,
        studentName: String,
        remainingSessions: Int
    ) {
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_PACKAGE,
            title = "Peringatan Paket",
            message = "Saldo paket $studentName tersisa $remainingSessions sesi",
            requestId = enrollmentId
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hashRequestId("package_$enrollmentId"),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            triggerMillis,
            pendingIntent
        )
    }

    /**
     * Batalkan notifikasi yang dijadwalkan.
     */
    fun cancelReminder(context: Context, requestId: String) {
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_SCHEDULE,
            title = "",
            message = "",
            requestId = requestId
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hashRequestId(requestId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Batalkan semua notifikasi yang dijadwalkan.
     */
    fun cancelAllReminders(context: Context) {
        val intent = createNotificationIntent(
            context = context,
            channelId = CHANNEL_SCHEDULE,
            title = "",
            message = "",
            requestId = ""
        )
        for (code in REQUEST_CODE_BASE until REQUEST_CODE_BASE + 10000) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    /**
     * Tampilkan notifikasi langsung (tanpa penjadwalan).
     */
    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        requestId: String
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            hashRequestId(requestId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(hashRequestId(requestId), notification)
    }

    // ─── Private helpers ───

    private fun createNotificationIntent(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        requestId: String
    ): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SHOW_REMINDER
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_REQUEST_ID, requestId)
        }
    }

    private fun hashRequestId(requestId: String): Int {
        return requestId.hashCode() and 0x7FFFFFFF
    }

    private fun formatRupiah(amount: Long): String {
        return "Rp ${String.format("%,d", amount).replace(',', '.')} "
    }
}
