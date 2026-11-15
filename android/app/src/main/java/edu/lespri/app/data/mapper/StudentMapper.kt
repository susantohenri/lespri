package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.StudentEntity
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.StudentStatus

/**
 * Mapper dua arah antara [StudentEntity] (Room Data Layer) dan [Student] (Domain Layer).
 */
fun StudentEntity.toDomain(): Student {
    return Student(
        id = id,
        name = name,
        guardianName = guardianName,
        guardianPhone = guardianPhone,
        schoolGrade = schoolGrade,
        notes = notes,
        status = runCatching { StudentStatus.valueOf(status) }.getOrDefault(StudentStatus.ACTIVE),
        joinedAt = joinedAt
    )
}

fun Student.toEntity(): StudentEntity {
    return StudentEntity(
        id = id,
        name = name,
        guardianName = guardianName,
        guardianPhone = guardianPhone,
        schoolGrade = schoolGrade,
        notes = notes,
        status = status.name,
        joinedAt = joinedAt
    )
}
