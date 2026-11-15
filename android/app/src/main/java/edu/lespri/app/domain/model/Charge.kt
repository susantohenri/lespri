package edu.lespri.app.domain.model

/**
 * Model domain tagihan pembayaran (SPP bulanan, paket sesi, per sesi, dll.).
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class Charge(
    val id: String,
    val enrollmentId: String,
    val chargeType: ChargeType,
    val periodStart: String? = null,
    val periodEnd: String? = null,
    val amount: Long, // Nominal dalam Rupiah (Long)
    val dueDate: String? = null,
    val status: ChargeStatus = ChargeStatus.UNPAID,
    val createdAt: Long = System.currentTimeMillis()
)
