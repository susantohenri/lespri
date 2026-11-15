package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.AttendanceRecordEntity
import edu.lespri.app.domain.model.AttendanceRecord
import edu.lespri.app.domain.model.AttendanceStatus

/**
 * Mapper dua arah antara [AttendanceRecordEntity] (Room Data Layer) dan [AttendanceRecord] (Domain Layer).
 */
fun AttendanceRecordEntity.toDomain(): AttendanceRecord {
    return AttendanceRecord(
        id = id,
        sessionId = sessionId,
        enrollmentId = enrollmentId,
        status = runCatching { AttendanceStatus.valueOf(status) }.getOrDefault(AttendanceStatus.UNRECORDED),
        note = note,
        updatedAt = updatedAt
    )
}

fun AttendanceRecord.toEntity(): AttendanceRecordEntity {
    return AttendanceRecordEntity(
        id = id,
        sessionId = sessionId,
        enrollmentId = enrollmentId,
        status = status.name,
        note = note,
        updatedAt = updatedAt
    )
}
