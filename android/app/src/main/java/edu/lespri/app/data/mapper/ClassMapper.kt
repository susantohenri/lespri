package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.ClassGroupEntity
import edu.lespri.app.data.local.entity.EnrollmentEntity
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.Enrollment
import edu.lespri.app.domain.model.EnrollmentStatus

/**
 * Mapper dua arah antara entitas Room ([ClassGroupEntity], [EnrollmentEntity])
 * dan model domain ([ClassGroup], [Enrollment]).
 */
fun ClassGroupEntity.toDomain(): ClassGroup {
    return ClassGroup(
        id = id,
        name = name,
        classType = runCatching { ClassType.valueOf(classType) }.getOrDefault(ClassType.GROUP),
        subject = subject,
        tutorId = tutorId,
        locationNote = locationNote,
        status = status,
        createdAt = createdAt
    )
}

fun ClassGroup.toEntity(): ClassGroupEntity {
    return ClassGroupEntity(
        id = id,
        name = name,
        classType = classType.name,
        subject = subject,
        tutorId = tutorId,
        locationNote = locationNote,
        status = status,
        createdAt = createdAt
    )
}

fun EnrollmentEntity.toDomain(): Enrollment {
    return Enrollment(
        id = id,
        studentId = studentId,
        classId = classId,
        billingType = runCatching { BillingType.valueOf(billingType) }.getOrDefault(BillingType.MONTHLY),
        feeAmount = feeAmount,
        dueDay = dueDay,
        packageSessionCount = packageSessionCount,
        status = runCatching { EnrollmentStatus.valueOf(status) }.getOrDefault(EnrollmentStatus.ACTIVE),
        createdAt = createdAt
    )
}

fun Enrollment.toEntity(): EnrollmentEntity {
    return EnrollmentEntity(
        id = id,
        studentId = studentId,
        classId = classId,
        billingType = billingType.name,
        feeAmount = feeAmount,
        dueDay = dueDay,
        packageSessionCount = packageSessionCount,
        status = status.name,
        createdAt = createdAt
    )
}
