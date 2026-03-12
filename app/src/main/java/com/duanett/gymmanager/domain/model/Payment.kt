package com.duanett.gymmanager.domain.model

/**
 * Pure domain model for a payment record.
 * Represents one payment transaction linked to a customer.
 */
data class Payment(
    val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentType: PaymentType,
    val notes: String = "",
    /** Firestore document ID — empty until synced to the cloud. */
    val firestoreId: String = ""
)

enum class PaymentType {
    MONTHLY,
    DROP_IN;

    fun displayName(): String = when (this) {
        MONTHLY -> "Monthly Fee"
        DROP_IN  -> "Drop-In Fee"
    }
}
