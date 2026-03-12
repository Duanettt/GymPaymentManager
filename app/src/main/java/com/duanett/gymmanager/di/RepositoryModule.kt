package com.duanett.gymmanager.di

import com.duanett.gymmanager.data.local.dao.CustomerDao
import com.duanett.gymmanager.data.repository.CustomerRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideCustomerRepository(
        customerDao: CustomerDao,
        firestore: FirebaseFirestore
    ): CustomerRepository = CustomerRepository(customerDao, firestore)
}
