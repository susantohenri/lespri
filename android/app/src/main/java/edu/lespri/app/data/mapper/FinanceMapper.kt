package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.ChargeEntity
import edu.lespri.app.data.local.entity.PaymentEntity
import edu.lespri.app.domain.model.Charge
import edu.lespri.app.domain.model.ChargeStatus
import edu.lespri.app.domain.model.ChargeType
import edu.lespri.app.domain.model.Payment
import edu.lespri.app.domain.model.PaymentMethod

/**
 * Mapper dua arah antara entitas keuangan Room ([ChargeEntity], [PaymentEntity])
 * dan model domain ([Charge], [Payment]).
 */
fun ChargeEntity.toDomain(): Charge {
    val parsedType = when (chargeType) {
        "MONTHLY", "MONTHLY_FEE" -> ChargeType.MONTHLY_FEE
        "REGISTRATION", "REGISTRATION_FEE" -> ChargeType.REGISTRATION_FEE
        "PACKAGE", "SESSION_PACKAGE" -> ChargeType.SESSION_PACKAGE
        else -> runCatching { ChargeType.valueOf(chargeType) }.getOrDefault(ChargeType.OTHER)
    }

    val parsedStatus = runCatching { ChargeStatus.valueOf(status) }.getOrDefault(ChargeStatus.UNPAID)

    return Charge(
        id = id,
        enrollmentId = enrollmentId,
        chargeType = parsedType,
        periodStart = periodStart,
        periodEnd = periodEnd,
        amount = amount,
        dueDate = dueDate,
        status = parsedStatus,
        createdAt = createdAt
    )
}

fun Charge.toEntity(): ChargeEntity {
    return ChargeEntity(
        id = id,
        enrollmentId = enrollmentId,
        chargeType = chargeType.name,
        periodStart = periodStart,
        periodEnd = periodEnd,
        amount = amount,
        dueDate = dueDate,
        status = status.name,
        createdAt = createdAt
    )
}

fun PaymentEntity.toDomain(): Payment {
    return Payment(
        id = id,
        chargeId = chargeId,
        amount = amount,
        paidAt = paidAt,
        method = runCatching { PaymentMethod.valueOf(method) }.getOrDefault(PaymentMethod.CASH),
        referenceNote = referenceNote
    )
}

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        id = id,
        chargeId = chargeId,
        amount = amount,
        paidAt = paidAt,
        method = method.name,
        referenceNote = referenceNote
    )
}
