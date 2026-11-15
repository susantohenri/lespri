package edu.lespri.app.domain.usecase

import edu.lespri.app.domain.repository.FinanceRepository
import javax.inject.Inject

/**
 * Use Case untuk menghasilkan tagihan SPP bulanan otomatis (Monthly Fee Generation)
 * bagi seluruh siswa dengan skema pembayaran MONTHLY pada bulan yang ditentukan.
 *
 * Murni Kotlin — ZERO Android / Room dependencies.
 */
class GenerateMonthlyChargesUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    /**
     * Men-generate tagihan SPP bulanan.
     * @param targetMonth Format bulan "YYYY-MM" (contoh: "2026-11").
     * @return Jumlah tagihan baru yang berhasil diterbitkan.
     */
    suspend operator fun invoke(targetMonth: String): Result<Int> {
        return financeRepository.generateMonthlyCharges(targetMonth)
    }
}
