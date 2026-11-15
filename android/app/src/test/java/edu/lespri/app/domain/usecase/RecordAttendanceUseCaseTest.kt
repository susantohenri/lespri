package edu.lespri.app.domain.usecase

import edu.lespri.app.domain.model.AttendanceRecord
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test murni untuk [RecordAttendanceUseCase] menggunakan Fake deterministic.
 * Zero dependency pada dynamic bytecode instrumentation sehingga instan (< 100ms) dan anti-hang di Java 21.
 */
class RecordAttendanceUseCaseTest {

    private lateinit var fakeRepository: FakeAttendanceRepository
    private lateinit var useCase: RecordAttendanceUseCase

    class FakeAttendanceRepository : AttendanceRepository {
        var callCount = 0
        var lastSessionId: String? = null
        var lastRecords: List<AttendanceRecord> = emptyList()
        var returnSuccess: Boolean = true

        override fun getSessionAttendance(sessionId: String): Flow<List<AttendanceRecord>> = emptyFlow()

        override fun countUnrecordedSessions(todayIso: String): Flow<Int> = emptyFlow()

        override suspend fun submitSessionAttendance(
            sessionId: String,
            records: List<AttendanceRecord>
        ): Result<Unit> {
            callCount++
            lastSessionId = sessionId
            lastRecords = records
            return if (returnSuccess) Result.success(Unit) else Result.failure(Exception("Submit error"))
        }
    }

    @Before
    fun setUp() {
        fakeRepository = FakeAttendanceRepository()
        useCase = RecordAttendanceUseCase(fakeRepository)
    }

    @Test
    fun `invoke with valid parameters calls repository and returns success`() = runBlocking {
        val records = listOf(
            AttendanceRecord(
                id = "att-1",
                sessionId = "sess-1",
                enrollmentId = "enr-1",
                status = AttendanceStatus.PRESENT
            )
        )

        val result = useCase("sess-1", records)

        assertTrue(result.isSuccess)
        assertEquals(1, fakeRepository.callCount)
        assertEquals("sess-1", fakeRepository.lastSessionId)
        assertEquals(records, fakeRepository.lastRecords)
    }

    @Test
    fun `invoke with blank sessionId returns failure without calling repository`() = runBlocking {
        val records = listOf(
            AttendanceRecord(
                id = "att-1",
                sessionId = "",
                enrollmentId = "enr-1",
                status = AttendanceStatus.PRESENT
            )
        )

        val result = useCase("", records)

        assertTrue(result.isFailure)
        assertEquals(0, fakeRepository.callCount)
    }

    @Test
    fun `invoke with empty records returns success without calling repository`() = runBlocking {
        val result = useCase("sess-1", emptyList())

        assertTrue(result.isSuccess)
        assertEquals(0, fakeRepository.callCount)
    }
}
