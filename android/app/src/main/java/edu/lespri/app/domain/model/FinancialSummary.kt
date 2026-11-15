package edu.lespri.app.domain.model

/**
 * Model domain ringkasan keuangan bimbel untuk dashboard dan analitik.
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class FinancialSummary(
    val totalOutstandingDebt: Long = 0L,
    val currentMonthIncome: Long = 0L,
    val unpaidChargesCount: Int = 0,
    val overdueChargesCount: Int = 0
)
