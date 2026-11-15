package edu.lespri.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utilitas tanggal dan waktu standar ISO-8601 ("YYYY-MM-DD") dan jam 24-jam ("HH:mm").
 * Kompatibel untuk semua level API Android tanpa dependensi external desugaring.
 */
object DateTimeUtil {

    private val idLocale = Locale.forLanguageTag("id-ID")
    private val isoDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val isoMonthFormatter = SimpleDateFormat("yyyy-MM", Locale.US)
    private val time24Formatter = SimpleDateFormat("HH:mm", Locale.US)
    private val displayDateFormatter = SimpleDateFormat("d MMMM yyyy", idLocale)
    private val displayDayDateFormatter = SimpleDateFormat("EEEE, d MMM yyyy", idLocale)
    private val shortDayDateFormatter = SimpleDateFormat("EEE, d MMM", idLocale)

    fun getTodayIsoDate(): String {
        return isoDateFormatter.format(Date())
    }

    fun getCurrentMonthIso(): String {
        return isoMonthFormatter.format(Date())
    }

    fun getCurrentTime24(): String {
        return time24Formatter.format(Date())
    }

    fun getIsoDatePlusDays(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return isoDateFormatter.format(cal.time)
    }

    fun formatDisplayDate(isoDate: String): String {
        return runCatching {
            val date = isoDateFormatter.parse(isoDate)
            date?.let { displayDateFormatter.format(it) } ?: isoDate
        }.getOrDefault(isoDate)
    }

    fun formatDisplayDayDate(isoDate: String): String {
        return runCatching {
            val date = isoDateFormatter.parse(isoDate)
            date?.let { displayDayDateFormatter.format(it) } ?: isoDate
        }.getOrDefault(isoDate)
    }

    fun formatShortDayDate(isoDate: String): String {
        return runCatching {
            val date = isoDateFormatter.parse(isoDate)
            date?.let { shortDayDateFormatter.format(it) } ?: isoDate
        }.getOrDefault(isoDate)
    }

    fun getDayOfWeek(isoDate: String): Int {
        return runCatching {
            val date = isoDateFormatter.parse(isoDate) ?: return 1
            val cal = Calendar.getInstance().apply { time = date }
            val calDow = cal.get(Calendar.DAY_OF_WEEK)
            if (calDow == Calendar.SUNDAY) 7 else calDow - 1
        }.getOrDefault(1)
    }
}
