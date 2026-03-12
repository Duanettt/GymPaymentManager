package com.duanett.gymmanager.data.local.dao

import androidx.room.*
import com.duanett.gymmanager.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for customers.
 *
 * Functions that return Flow are NOT suspend — Room emits new values
 * automatically whenever the underlying table changes, so the caller
 * just collects the flow (great for reactive UI with Compose).
 *
 * Functions that write data ARE suspend — they do a single async operation
 * and complete, so the caller uses a coroutine scope to call them.
 */
@Dao
interface CustomerDao {

    /** Observe all customers, ordered alphabetically. Emits on every table change. */
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    /** Observe only active customers. */
    @Query("SELECT * FROM customers WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveCustomers(): Flow<List<CustomerEntity>>

    /** One-shot lookup by ID — null if not found. */
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerEntity?

    /** Live search — use with a search bar that debounces user input. */
    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    /**
     * Insert or update. REPLACE strategy handles the case where a Firestore
     * sync overwrites a local record with the same primary key.
     * Returns the new row ID (useful after inserting to get the generated ID).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)
}
