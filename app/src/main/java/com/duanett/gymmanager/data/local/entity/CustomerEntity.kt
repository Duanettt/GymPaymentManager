package com.duanett.gymmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for customers.
 * This is the DB representation — kept separate from the domain model
 * so schema changes don't ripple into the rest of the app.
 */
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    /** Stored as a string to keep Room schema simple ("MONTHLY" / "DROP_IN"). */
    val membershipType: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    /** Firestore document ID for cloud sync tracking. */
    val firestoreId: String = ""
)
