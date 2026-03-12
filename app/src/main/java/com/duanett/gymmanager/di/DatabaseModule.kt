package com.duanett.gymmanager.di

import android.content.Context
import androidx.room.Room
import com.duanett.gymmanager.data.local.dao.CustomerDao
import com.duanett.gymmanager.data.local.dao.PaymentDao
import com.duanett.gymmanager.data.local.database.GymDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 *
 * @InstallIn(SingletonComponent) means these bindings live for the entire
 * app lifetime — the database and DAOs are created once and reused everywhere.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGymDatabase(@ApplicationContext context: Context): GymDatabase =
        Room.databaseBuilder(
            context,
            GymDatabase::class.java,
            "gym_database"
        )
        // Remove before shipping — write proper Migration objects instead.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    // DAOs are provided separately so classes can inject just the DAO they need,
    // without taking a dependency on the whole database object.

    @Provides
    @Singleton
    fun provideCustomerDao(db: GymDatabase): CustomerDao = db.customerDao()

    @Provides
    @Singleton
    fun providePaymentDao(db: GymDatabase): PaymentDao = db.paymentDao()
}
