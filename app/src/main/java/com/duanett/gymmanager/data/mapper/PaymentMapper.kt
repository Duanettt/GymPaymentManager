package com.duanett.gymmanager.data.mapper

import com.duanett.gymmanager.data.local.entity.PaymentEntity
import com.duanett.gymmanager.domain.model.Payment
import com.duanett.gymmanager.domain.model.PaymentType

fun PaymentEntity.toDomain(): Payment = Payment(
    id = id,
    customerId = customerId,
    amount = amount,
    paymentDate = paymentDate,
    paymentType = PaymentType.valueOf(paymentType),
    notes = notes,
    firestoreId = firestoreId
)

fun Payment.toEntity(): PaymentEntity = PaymentEntity(
    id = id,
    customerId = customerId,
    amount = amount,
    paymentDate = paymentDate,
    paymentType = paymentType.name,
    notes = notes,
    firestoreId = firestoreId
)

fun List<PaymentEntity>.toDomain(): List<Payment> = map { it.toDomain() }
