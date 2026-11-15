package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.TutorEntity
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.model.TutorStatus

/**
 * Mapper dua arah antara [TutorEntity] (Room Data Layer) dan [Tutor] (Domain Layer).
 */
fun TutorEntity.toDomain(): Tutor {
    return Tutor(
        id = id,
        name = name,
        phone = phone,
        expertise = expertise,
        notes = notes,
        status = runCatching { TutorStatus.valueOf(status) }.getOrDefault(TutorStatus.ACTIVE),
        createdAt = createdAt
    )
}

fun Tutor.toEntity(): TutorEntity {
    return TutorEntity(
        id = id,
        name = name,
        phone = phone,
        expertise = expertise,
        notes = notes,
        status = status.name,
        createdAt = createdAt
    )
}
