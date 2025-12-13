package com.blooddonation.management.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blooddonation.management.data.models.Category
import com.blooddonation.management.data.models.InventoryItem
import com.blooddonation.management.data.models.Distribution
import com.blooddonation.management.data.models.ReturnItem

@Database(
    entities = [
        Category::class,
        InventoryItem::class,
        Distribution::class,
        ReturnItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun distributionDao(): DistributionDao
    abstract fun returnDao(): ReturnDao
}
