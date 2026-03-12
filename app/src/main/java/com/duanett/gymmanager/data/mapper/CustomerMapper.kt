package com.duanett.gymmanager.data.mapper

import com.duanett.gymmanager.data.local.entity.CustomerEntity
import com.duanett.gymmanager.domain.model.Customer
import com.duanett.gymmanager.domain.model.MembershipType

/**
 * Converts between the Room entity (DB layer) and the domain model (UI/business layer).
 * Keeps each layer ignorant of the other's structure.
 */
fun CustomerEntity.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phone = phone,
    email = email,
    membershipType = MembershipType.valueOf(membershipType),
    isActive = isActive,
    createdAt = createdAt,
    firestoreId = firestoreId
)

fun Customer.toEntity(): CustomerEntity = CustomerEntity(
    id = id,
    name = name,
    phone = phone,
    email = email,
    membershipType = membershipType.name,
    isActive = isActive,
    createdAt = createdAt,
    firestoreId = firestoreId
)

fun List<CustomerEntity>.toDomain(): List<Customer> = map { it.toDomain() }
