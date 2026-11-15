package edu.lespri.app.domain.usecase

import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.domain.model.ChargeStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use Case untuk menghitung total akumulasi tunggakan/hutang seorang siswa
 * dari seluruh pendaftaran kelas (enrollment) yang dimilikinya.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CalculateStudentDebtUseCase @Inject constructor(
    private val classDao: ClassDao,
    private val financeDao: FinanceDao
) {
    operator fun invoke(studentId: String): Flow<Long> {
        return classDao.getActiveEnrollmentsForStudent(studentId).flatMapLatest { enrollments ->
            if (enrollments.isEmpty()) {
                flowOf(0L)
            } else {
                financeDao.getAllActiveCharges().map { allCharges ->
                    val enrollmentIdSet = enrollments.map { it.id }.toSet()
                    var totalDebt = 0L

                    for (charge in allCharges) {
                        if (charge.enrollmentId in enrollmentIdSet &&
                            charge.status != ChargeStatus.CANCELLED.name &&
                            charge.status != ChargeStatus.PAID.name
                        ) {
                            val paid = financeDao.getTotalPaidForCharge(charge.id)
                            val remaining = (charge.amount - paid).coerceAtLeast(0L)
                            totalDebt += remaining
                        }
                    }
                    totalDebt
                }
            }
        }
    }
}
