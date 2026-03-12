package com.duanett.gymmanager.data.repository

import com.duanett.gymmanager.data.local.dao.CustomerDao
import com.duanett.gymmanager.data.mapper.toDomain
import com.duanett.gymmanager.data.mapper.toEntity
import com.duanett.gymmanager.domain.model.Customer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Single source of truth for customer data.
 *
 * Strategy:
 *  - Room is the primary source — the UI always reads from Room via Flow.
 *  - Firestore is a write-through backup — on every insert/update we also push
 *    to Firestore so data survives device loss or is shared across devices.
 *
 * In a larger app you'd extract an interface and inject implementations,
 * but for Day 1 a concrete class keeps things readable.
 */
class CustomerRepository(
    private val customerDao: CustomerDao,
    private val firestore: FirebaseFirestore
) {
    private val firestoreCollection = firestore.collection("customers")

    /** Reactive stream of all customers — emits whenever the Room table changes. */
    fun getAllCustomers(): Flow<List<Customer>> =
        customerDao.getAllCustomers().map { it.toDomain() }

    fun getActiveCustomers(): Flow<List<Customer>> =
        customerDao.getActiveCustomers().map { it.toDomain() }

    fun searchCustomers(query: String): Flow<List<Customer>> =
        customerDao.searchCustomers(query).map { it.toDomain() }

    suspend fun getCustomerById(id: Int): Customer? =
        customerDao.getCustomerById(id)?.toDomain()

    /**
     * Save to Room first (instant local feedback), then sync to Firestore.
     * Returns the saved customer with its generated local ID.
     */
    suspend fun saveCustomer(customer: Customer): Customer {
        val localId = customerDao.insertCustomer(customer.toEntity()).toInt()
        val savedCustomer = customer.copy(id = localId)

        // Fire-and-forget Firestore sync — failures here don't block the UI.
        // In a real app, queue failed syncs for retry.
        runCatching {
            val firestoreId = firestoreCollection
                .add(savedCustomer.toFirestoreMap())
                .await()
                .id
            // Update the local record with the Firestore document ID for future syncs.
            customerDao.updateCustomer(savedCustomer.copy(firestoreId = firestoreId).toEntity())
        }

        return savedCustomer
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer.toEntity())

        runCatching {
            if (customer.firestoreId.isNotEmpty()) {
                firestoreCollection
                    .document(customer.firestoreId)
                    .set(customer.toFirestoreMap())
                    .await()
            }
        }
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer.toEntity())

        runCatching {
            if (customer.firestoreId.isNotEmpty()) {
                firestoreCollection.document(customer.firestoreId).delete().await()
            }
        }
    }

    /** Map used when writing to Firestore. Keep in sync with domain model fields. */
    private fun Customer.toFirestoreMap(): Map<String, Any> = mapOf(
        "name"           to name,
        "phone"          to phone,
        "email"          to email,
        "membershipType" to membershipType.name,
        "isActive"       to isActive,
        "createdAt"      to createdAt
    )
}
