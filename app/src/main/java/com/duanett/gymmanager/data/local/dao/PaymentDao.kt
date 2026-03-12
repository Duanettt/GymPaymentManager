package com.duanett.gymmanager.data.local.dao

import androidx.room.*
import com.duanett.gymmanager.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    /** All payments for a specific customer, newest first. */
    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY paymentDate DESC")
    fun getPaymentsForCustomer(customerId: Int): Flow<List<PaymentEntity>>

    /** All payments across every customer, newest first (useful for a global history screen). */
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    /** Total amount paid by a customer — handy for a summary card. */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE customerId = :customerId")
    fun getTotalPaidByCustomer(customerId: Int): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)
}
