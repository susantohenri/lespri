package edu.lespri.app.data.repository

import edu.lespri.app.data.local.LespriDatabase
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.data.local.dao.FinanceDao
import edu.lespri.app.data.local.dao.StudentDao
import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.model.PaymentMethod
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test untuk [FinanceRepositoryImpl] dengan fokus pada proteksi Overpayment (Critical Path 4)
 * dan transisi status tagihan atomik.
 */
class FinanceRepositoryImplTest {

    private lateinit var database: LespriDatabase
    private lateinit var financeDao: FinanceDao
    private lateinit var classDao: ClassDao
    private lateinit var studentDao: StudentDao
    private lateinit var repository: FinanceRepositoryImpl

    @Before
    fun setUp() {
        database = mockk(relaxed = true)
        financeDao = mockk(relaxed = true)
        classDao = mockk(relaxed = true)
        studentDao = mockk(relaxed = true)
        repository = FinanceRepositoryImpl(database, financeDao, classDao, studentDao).apply {
            // Bypass Room SQLite internal native lock in unit test
            transactionRunner = { block -> block() }
        }
    }

    @Test
    fun `recordPayment with amount exceeding remaining balance fails with overpayment error`() = runBlocking {
        // Tagihan Rp 100.000, sudah dibayar Rp 70.000, sisa Rp 30.000
        val charge = ChargeEntity(
            id = "charge-1",
            enrollmentId = "enr-1",
            chargeType = "MONTHLY_FEE",
            amount = 100_000L,
            status = ChargeStatus.PARTIALLY_PAID.name,
            createdAt = System.currentTimeMillis()
        )
        coEvery { financeDao.getChargeByIdSync("charge-1") } returns charge
        coEvery { financeDao.getTotalPaidForCharge("charge-1") } returns 70_000L

        // Coba bayar Rp 50.000 (melebihi sisa Rp 30.000)
        val payment = Payment(
            id = "pay-1",
            chargeId = "charge-1",
            amount = 50_000L,
            method = PaymentMethod.TRANSFER
        )

        val result = repository.recordPayment(payment)

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message ?: ""
        assertTrue(message.contains("melebihi sisa tagihan"))
        coVerify(exactly = 0) { financeDao.insertPayment(any()) }
    }

    @Test
    fun `recordPayment with zero or negative amount fails`() = runBlocking {
        val charge = ChargeEntity(
            id = "charge-1",
            enrollmentId = "enr-1",
            chargeType = "MONTHLY_FEE",
            amount = 100_000L,
            status = ChargeStatus.UNPAID.name,
            createdAt = System.currentTimeMillis()
        )
        coEvery { financeDao.getChargeByIdSync("charge-1") } returns charge
        coEvery { financeDao.getTotalPaidForCharge("charge-1") } returns 0L

        val zeroPayment = Payment(
            id = "pay-zero",
            chargeId = "charge-1",
            amount = 0L
        )

        val result = repository.recordPayment(zeroPayment)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { financeDao.insertPayment(any()) }
    }

    @Test
    fun `recordPayment with valid partial payment updates status to PARTIALLY_PAID`() = runBlocking {
        val charge = ChargeEntity(
            id = "charge-1",
            enrollmentId = "enr-1",
            chargeType = "MONTHLY_FEE",
            amount = 100_000L,
            status = ChargeStatus.UNPAID.name,
            createdAt = System.currentTimeMillis()
        )
        coEvery { financeDao.getChargeByIdSync("charge-1") } returns charge
        coEvery { financeDao.getTotalPaidForCharge("charge-1") } returns 0L

        val payment = Payment(
            id = "pay-partial",
            chargeId = "charge-1",
            amount = 40_000L
        )

        val result = repository.recordPayment(payment)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { financeDao.insertPayment(any()) }
        coVerify(exactly = 1) {
            financeDao.updateCharge(match { it.status == ChargeStatus.PARTIALLY_PAID.name })
        }
    }

    @Test
    fun `recordPayment with full payment updates status to PAID`() = runBlocking {
        val charge = ChargeEntity(
            id = "charge-1",
            enrollmentId = "enr-1",
            chargeType = "MONTHLY_FEE",
            amount = 100_000L,
            status = ChargeStatus.PARTIALLY_PAID.name,
            createdAt = System.currentTimeMillis()
        )
        coEvery { financeDao.getChargeByIdSync("charge-1") } returns charge
        coEvery { financeDao.getTotalPaidForCharge("charge-1") } returns 60_000L

        val payment = Payment(
            id = "pay-full",
            chargeId = "charge-1",
            amount = 40_000L
        )

        val result = repository.recordPayment(payment)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { financeDao.insertPayment(any()) }
        coVerify(exactly = 1) {
            financeDao.updateCharge(match { it.status == ChargeStatus.PAID.name })
        }
    }
}
