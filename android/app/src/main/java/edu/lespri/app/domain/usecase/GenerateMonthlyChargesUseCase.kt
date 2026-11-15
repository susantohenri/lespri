package edu.lespri.app.domain.usecase

import androidx.room.withTransaction
import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeType
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

/**
 * Use Case untuk menghasilkan tagihan SPP bulanan otomatis (Monthly Fee Generation)
 * bagi seluruh siswa dengan skema pembayaran MONTHLY pada bulan yang ditentukan.
 */
class GenerateMonthlyChargesUseCase @Inject constructor(
    private val database: LespriDatabase,
    private val classDao: ClassDao,
    private val financeDao: FinanceDao
) {
    /**
     * Men-generate tagihan SPP bulanan.
     * @param targetMonth Format bulan "YYYY-MM" (contoh: "2026-11").
     * @return Jumlah tagihan baru yang berhasil diterbitkan.
     */
    suspend operator fun invoke(targetMonth: String): Result<Int> {
        return runCatching {
            database.withTransaction {
                val allClasses = classDao.getClassesByStatus("ACTIVE").firstOrNull() ?: emptyList()
                var generatedCount = 0
                val now = System.currentTimeMillis()

                for (cls in allClasses) {
                    val enrollments = classDao.getActiveEnrollmentsForClass(cls.id).firstOrNull() ?: emptyList()

                    for (enrollment in enrollments) {
                        if (enrollment.billingType == BillingType.MONTHLY.name && enrollment.feeAmount > 0L) {
                            // Cek apakah tagihan untuk periode bulan ini sudah pernah diterbitkan
                            val existingCharges = financeDao.getChargesForEnrollment(enrollment.id).firstOrNull() ?: emptyList()
                            val alreadyBilled = existingCharges.any {
                                it.chargeType == ChargeType.MONTHLY_FEE.name &&
                                    it.periodStart?.startsWith(targetMonth) == true
                            }

                            if (!alreadyBilled) {
                                val dueDayFormatted = enrollment.dueDay?.let { day ->
                                    val safeDay = day.coerceIn(1, 28)
                                    "$targetMonth-${String.format(Locale.US, "%02d", safeDay)}"
                                }

                                val charge = ChargeEntity(
                                    id = UUID.randomUUID().toString(),
                                    enrollmentId = enrollment.id,
                                    chargeType = ChargeType.MONTHLY_FEE.name,
                                    periodStart = "$targetMonth-01",
                                    amount = enrollment.feeAmount,
                                    dueDate = dueDayFormatted,
                                    status = ChargeStatus.UNPAID.name,
                                    createdAt = now
                                )
                                financeDao.insertCharge(charge)
                                generatedCount++
                            }
                        }
                    }
                }
                generatedCount
            }
        }
    }
}
