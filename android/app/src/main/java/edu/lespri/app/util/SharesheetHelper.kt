package edu.lespri.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

/**
 * Helper untuk membagikan pesan teks melalui Android Sharesheet dan WhatsApp.
 * Tidak mengirim otomatis — pengguna tetap harus memilih aplikasi dan konfirmasi.
 */
object SharesheetHelper {

    /**
     * Buka Android Sharesheet generik dengan teks yang sudah disiapkan.
     * Pengguna bebas memilih aplikasi (WhatsApp, Telegram, Email, dll.).
     */
    fun shareGeneric(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Bagikan melalui")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(context, chooser, null)
        } catch (e: Exception) {
            // Tidak ada aplikasi yang bisa menangani intent
        }
    }

    /**
     * Coba buka WhatsApp langsung dengan teks yang sudah disiapkan.
     * Jika WhatsApp tidak terinstal, fallback ke Android Sharesheet generik.
     *
     * @param phone Nomor telepon opsional (tanpa kode negara). Jika diisi,
     *              WhatsApp akan mencoba membuka chat dengan nomor tersebut.
     */
    fun shareViaWhatsApp(
        context: Context,
        message: String,
        phone: String? = null
    ) {
        try {
            val whatsappPackage = "com.whatsapp"
            val pm = context.packageManager

            if (pm.getPackageInfo(whatsappPackage, 0) != null) {
                val intent = if (phone != null && phone.isNotBlank()) {
                    // WhatsApp URI scheme untuk chat langsung ke nomor
                    Intent(Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${android.net.Uri.encode(message)}")
                        setPackage(whatsappPackage)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                } else {
                    // WhatsApp share generik
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        setPackage(whatsappPackage)
                        putExtra(Intent.EXTRA_TEXT, message)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                startActivity(context, intent, null)
            } else {
                // WhatsApp tidak terinstal — fallback ke sharesheet
                shareGeneric(context, "Bagikan Pesan", message)
            }
        } catch (e: Exception) {
            // Gagal membuka WhatsApp — fallback ke sharesheet
            shareGeneric(context, "Bagikan Pesan", message)
        }
    }

    /**
     * Buat template pesan tagihan untuk dibagikan ke wali siswa.
     */
    fun formatChargeMessage(
        bimbelName: String,
        studentName: String,
        className: String,
        chargeAmount: Long,
        remainingBalance: Long,
        dueDate: String?
    ): String {
        val amountStr = formatRupiah(chargeAmount)
        val remainingStr = formatRupiah(remainingBalance)
        val dateStr = dueDate?.let { "| Jatuh tempo: $it" } ?: ""

        return """
            Halo, berikut informasi tagihan dari $bimbelName:
            
            Siswa: $studentName
            Kelas: $className
            Tagihan: $amountStr
            Sisa belum lunas: $remainingStr
            $dateStr
            
            Terima kasih atas kerjasamanya.
        """.trimIndent()
    }

    /**
     * Buat template pesan kuitansi pembayaran untuk dibagikan.
     */
    fun formatReceiptMessage(
        bimbelName: String,
        studentName: String,
        className: String,
        paymentAmount: Long,
        chargeAmount: Long,
        remainingBalance: Long,
        paymentDate: String,
        method: String
    ): String {
        val paidStr = formatRupiah(paymentAmount)
        val totalStr = formatRupiah(chargeAmount)
        val remainingStr = formatRupiah(remainingBalance)

        return """
            KUITANSI PEMBAYARAN
            $bimbelName
            
            Tanggal: $paymentDate
            Siswa: $studentName
            Kelas: $className
            Metode: $method
            
            Tagihan: $totalStr
            Dibayar: $paidStr
            Sisa: $remainingStr
            
            Terima kasih atas pembayarannya.
        """.trimIndent()
    }

    /**
     * Format Long (Rupiah) ke string yang mudah dibaca.
     */
    private fun formatRupiah(amount: Long): String {
        return "Rp ${String.format("%,d", amount).replace(',', '.')} "
    }
}
