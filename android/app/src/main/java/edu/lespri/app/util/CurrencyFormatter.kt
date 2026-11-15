package edu.lespri.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utilitas pemformatan mata uang Rupiah (IDR).
 * Menjamin representasi nilai moneter bersih tanpa floating-point imprecision.
 */
object CurrencyFormatter {

    private val idLocale = Locale.forLanguageTag("id-ID")

    private val symbols = DecimalFormatSymbols(idLocale).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val rupiahFormatter = DecimalFormat("#,###", symbols)

    /**
     * Memformat nilai moneter [amount] (Long) ke format standar Rupiah.
     * Contoh: 150000L -> "Rp 150.000", 0L -> "Rp 0".
     */
    fun formatRupiah(amount: Long): String {
        return "Rp ${rupiahFormatter.format(amount)}"
    }

    /**
     * Memformat ke versi ringkas untuk kartu/badge kecil (misal: "Rp 2,5 jt", "Rp 150 rb").
     */
    fun formatCompactRupiah(amount: Long): String {
        return when {
            amount >= 1_000_000_000L -> String.format(idLocale, "Rp %.1f M", amount / 1_000_000_000.0)
            amount >= 1_000_000L -> String.format(idLocale, "Rp %.1f jt", amount / 1_000_000.0).replace(",0", "")
            amount >= 1_000L -> String.format(idLocale, "Rp %d rb", amount / 1_000L)
            else -> "Rp $amount"
        }
    }

    /**
     * Membersihkan input string dari karakter non-numerik dan mengonversinya ke [Long].
     */
    fun parseRupiah(input: String): Long {
        val cleanDigits = input.filter { it.isDigit() }
        return cleanDigits.toLongOrNull() ?: 0L
    }
}
