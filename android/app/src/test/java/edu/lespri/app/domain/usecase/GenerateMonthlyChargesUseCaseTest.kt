package edu.lespri.app.domain.usecase

import edu.lespri.app.domain.repository.FinanceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test murni untuk [GenerateMonthlyChargesUseCase] (Fase 4 - Task 4.1).
 * Memverifikasi delegasi penuh ke [FinanceRepository] tanpa dependensi Android/Room.
 */
class GenerateMonthlyChargesUseCaseTest {

    private lateinit var financeRepository: FinanceRepository
    private lateinit var useCase: GenerateMonthlyChargesUseCase

    @Before
    fun setUp() {
        financeRepository = mockk()
        useCase = GenerateMonthlyChargesUseCase(financeRepository)
    }

    @Test
    fun `invoke delegates to financeRepository generateMonthlyCharges`() = runBlocking {
        val targetMonth = "2026-11"
        coEvery { financeRepository.generateMonthlyCharges(targetMonth) } returns Result.success(5)

        val result = useCase(targetMonth)

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull())
        coVerify(exactly = 1) { financeRepository.generateMonthlyCharges(targetMonth) }
    }
}
