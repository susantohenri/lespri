package edu.lespri.app.domain.usecase

import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.FinanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use Case untuk menghitung total akumulasi tunggakan/hutang seorang siswa
 * dari seluruh pendaftaran kelas (enrollment) yang dimilikinya.
 *
 * Murni Domain — ZERO Room / Android dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CalculateStudentDebtUseCase @Inject constructor(
    private val classRepository: ClassRepository,
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(studentId: String): Flow<Long> {
        return classRepository.getActiveEnrollmentsForStudent(studentId).flatMapLatest { enrollments ->
            if (enrollments.isEmpty()) {
                flowOf(0L)
            } else {
                financeRepository.getCharges(null).map { chargesWithBalance ->
                    val enrollmentIdSet = enrollments.map { it.id }.toSet()
                    chargesWithBalance
                        .filter {
                            it.charge.enrollmentId in enrollmentIdSet &&
                                it.charge.status != ChargeStatus.CANCELLED &&
                                it.charge.status != ChargeStatus.PAID
                        }
                        .sumOf { it.remainingBalance }
                }
            }
        }
    }
}
