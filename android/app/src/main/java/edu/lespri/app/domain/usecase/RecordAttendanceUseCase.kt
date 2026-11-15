package edu.lespri.app.domain.usecase

import edu.lespri.app.domain.model.AttendanceRecord
import edu.lespri.app.domain.repository.AttendanceRepository
import javax.inject.Inject

/**
 * Use Case untuk merekam atau memperbarui presensi kehadiran sesi pelajaran.
 * Menjamin eksekusi pemotongan/pembalikan saldo paket secara atomik di Data Layer.
 */
class RecordAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        records: List<AttendanceRecord>
    ): Result<Unit> {
        if (sessionId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID sesi tidak boleh kosong"))
        }
        if (records.isEmpty()) {
            return Result.success(Unit)
        }
        return attendanceRepository.submitSessionAttendance(sessionId, records)
    }
}
