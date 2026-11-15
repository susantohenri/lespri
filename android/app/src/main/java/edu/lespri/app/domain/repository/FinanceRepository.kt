package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.Charge
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeWithBalance
import edu.lespri.app.domain.model.FinancialSummary
import edu.lespri.app.domain.model.Payment
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk operasi keuangan (tagihan, pembayaran, saldo paket).
 */
interface FinanceRepository {
    fun getFinancialSummary(): Flow<FinancialSummary>
    fun getCharges(statusFilter: ChargeStatus?): Flow<List<ChargeWithBalance>>
    suspend fun createCharge(charge: Charge): Result<Unit>
    suspend fun recordPayment(payment: Payment): Result<Unit>
    fun getRemainingPackageSessions(enrollmentId: String): Flow<Int>
    suspend fun generateMonthlyCharges(targetMonth: String): Result<Int>
    fun countLowBalanceEnrollments(threshold: Int): Flow<Int>
    suspend fun getOutstandingForStudent(studentId: String): Long
    suspend fun getOutstandingForEnrollment(enrollmentId: String): Long
}
