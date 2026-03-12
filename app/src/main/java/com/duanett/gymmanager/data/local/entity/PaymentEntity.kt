package com.duanett.gymmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room database entity for payment records.
 *
 * Foreign key links each payment to its customer.
 * CASCADE delete means removing a customer also removes their payment history.
 * The index on customerId speeds up queries like "all payments for customer X".
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    /** Stored as a string ("MONTHLY" / "DROP_IN"). */
    val paymentType: String,
    val notes: String = "",
    val firestoreId: String = ""
)
