package com.duanett.gymmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duanett.gymmanager.data.local.dao.CustomerDao
import com.duanett.gymmanager.data.local.dao.PaymentDao
import com.duanett.gymmanager.data.local.entity.CustomerEntity
import com.duanett.gymmanager.data.local.entity.PaymentEntity

/**
 * Room database declaration.
 *
 * Construction and lifecycle are managed by Hilt via DatabaseModule —
 * there's no need for a manual singleton here.
 *
 * When you change the schema, bump [version] and provide a Migration object
 * in DatabaseModule. Remove fallbackToDestructiveMigration() before shipping.
 */
@Database(
    entities = [CustomerEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun paymentDao(): PaymentDao
}
