package com.duanett.gymmanager.domain.model

/**
 * Pure domain model — no framework annotations.
 * This is the object the UI and business logic work with.
 * Kept separate from CustomerEntity so the presentation layer
 * never depends on Room implementation details.
 */
data class Customer(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val membershipType: MembershipType,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    /** Firestore document ID — empty until synced to the cloud. */
    val firestoreId: String = ""
)

enum class MembershipType {
    MONTHLY,
    DROP_IN;

    fun displayName(): String = when (this) {
        MONTHLY -> "Monthly Member"
        DROP_IN  -> "Drop-In"
    }
}
