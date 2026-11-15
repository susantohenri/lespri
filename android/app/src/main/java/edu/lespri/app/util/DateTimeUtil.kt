package edu.lespri.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    /** Formatter UTC khusus konversi millis dari DatePicker Compose ke tanggal ISO. */
    private val utcIsoDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

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

    /**
     * Memformat timestamp epoch (ms) menjadi tanggal tampilan "d MMMM yyyy".
     */
    fun formatDisplayTimestamp(timestamp: Long): String {
        return displayDateFormatter.format(Date(timestamp))
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

    /** Konversi tanggal ISO ke epoch millis UTC (untuk Material3 DatePicker). */
    fun isoDateToUtcMillis(isoDate: String): Long? {
        return runCatching { utcIsoDateFormatter.parse(isoDate)?.time }.getOrNull()
    }

    /** Konversi epoch millis UTC dari DatePicker ke tanggal ISO "YYYY-MM-DD". */
    fun utcMillisToIsoDate(millis: Long): String {
        return utcIsoDateFormatter.format(Date(millis))
    }

    /** Geser tanggal ISO sebanyak [days] hari (negatif untuk mundur). */
    fun addDaysIso(isoDate: String, days: Int): String {
        return runCatching {
            val calendar = Calendar.getInstance().apply {
                time = isoDateFormatter.parse(isoDate) ?: Date()
                add(Calendar.DAY_OF_YEAR, days)
            }
            isoDateFormatter.format(calendar.time)
        }.getOrDefault(isoDate)
    }

    /** Tanggal Senin pada minggu yang sama dengan [isoDate]. */
    fun getWeekStartIso(isoDate: String): String {
        return addDaysIso(isoDate, -(getDayOfWeek(isoDate) - 1))
    }

    /** Tujuh tanggal ISO (Senin s/d Minggu) untuk minggu yang memuat [isoDate]. */
    fun getWeekDatesIso(isoDate: String): List<String> {
        val weekStart = getWeekStartIso(isoDate)
        return (0..6).map { addDaysIso(weekStart, it) }
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
