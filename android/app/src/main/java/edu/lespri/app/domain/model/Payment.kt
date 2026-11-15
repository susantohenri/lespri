package edu.lespri.app.domain.model

/**
 * Model domain transaksi pembayaran tagihan.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class Payment(
    val id: String,
    val chargeId: String,
    val amount: Long, // Nominal dalam Rupiah (Long)
    val paidAt: Long = System.currentTimeMillis(),
    val method: PaymentMethod = PaymentMethod.CASH,
    val referenceNote: String? = null
)
