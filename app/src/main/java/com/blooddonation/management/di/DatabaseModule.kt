package com.blooddonation.management.di

import android.content.Context
import androidx.room.Room
import com.blooddonation.management.data.local.AppDatabase
import com.blooddonation.management.data.local.CategoryDao
import com.blooddonation.management.data.local.InventoryDao
import com.blooddonation.management.data.local.DistributionDao
import com.blooddonation.management.data.local.ReturnDao
import com.blooddonation.management.data.repository.BloodDonationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "blood_donation_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideInventoryDao(database: AppDatabase): InventoryDao = database.inventoryDao()

    @Provides
    fun provideDistributionDao(database: AppDatabase): DistributionDao = database.distributionDao()

    @Provides
    fun provideReturnDao(database: AppDatabase): ReturnDao = database.returnDao()

    @Singleton
    @Provides
    fun provideBloodDonationRepository(
        categoryDao: CategoryDao,
        inventoryDao: InventoryDao,
        distributionDao: DistributionDao,
        returnDao: ReturnDao,
        authManager: com.blooddonation.management.data.firebase.FirebaseAuthManager
    ): BloodDonationRepository {
        return BloodDonationRepository(categoryDao, inventoryDao, distributionDao, returnDao, authManager)
    }
}
