package edu.lespri.app.domain.model

/**
 * Model domain pendaftaran siswa ke kelas berserta kesepakatan penagihan (billing agreement).
 * Murni Kotlin data class tanpa dependensi platform Android atau database Room.
 */
data class Enrollment(
    val id: String,
    val studentId: String,
    val classId: String,
    val billingType: BillingType,
    val feeAmount: Long,
    val dueDay: Int? = null,
    val packageSessionCount: Int? = null,
    val status: EnrollmentStatus = EnrollmentStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)
