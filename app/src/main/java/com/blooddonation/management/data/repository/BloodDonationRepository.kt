package com.blooddonation.management.data.repository

import com.blooddonation.management.data.local.CategoryDao
import com.blooddonation.management.data.local.InventoryDao
import com.blooddonation.management.data.local.DistributionDao
import com.blooddonation.management.data.local.ReturnDao
import com.blooddonation.management.data.models.Category
import com.blooddonation.management.data.models.InventoryItem
import com.blooddonation.management.data.models.Distribution
import com.blooddonation.management.data.models.ReturnItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloodDonationRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val inventoryDao: InventoryDao,
    private val distributionDao: DistributionDao,
    private val returnDao: ReturnDao
) {
    // Category Operations
    suspend fun addCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    fun searchCategories(query: String): Flow<List<Category>> = categoryDao.searchCategories(query)

    // Inventory Operations
    suspend fun addInventoryItem(item: InventoryItem) = inventoryDao.insertItem(item)
    suspend fun updateInventoryItem(item: InventoryItem) = inventoryDao.updateItem(item)
    suspend fun deleteInventoryItem(item: InventoryItem) = inventoryDao.deleteItem(item)
    fun getAllInventoryItems(): Flow<List<InventoryItem>> = inventoryDao.getAllItems()
    fun getInventoryByCategory(categoryId: Int): Flow<List<InventoryItem>> = 
        inventoryDao.getItemsByCategory(categoryId)
    fun searchInventory(query: String): Flow<List<InventoryItem>> = inventoryDao.searchItems(query)
    fun getExpiringItems(futureDate: Long): Flow<List<InventoryItem>> = 
        inventoryDao.getExpiringItems(futureDate)

    // Distribution Operations
    suspend fun addDistribution(distribution: Distribution) = 
        distributionDao.insertDistribution(distribution)
    suspend fun updateDistribution(distribution: Distribution) = 
        distributionDao.updateDistribution(distribution)
    suspend fun deleteDistribution(distribution: Distribution) = 
        distributionDao.deleteDistribution(distribution)
    fun getAllDistributions(): Flow<List<Distribution>> = distributionDao.getAllDistributions()
    fun searchDistributions(query: String): Flow<List<Distribution>> = 
        distributionDao.searchDistributions(query)

    // Return Operations
    suspend fun addReturn(returnItem: ReturnItem) = returnDao.insertReturn(returnItem)
    suspend fun updateReturn(returnItem: ReturnItem) = returnDao.updateReturn(returnItem)
    suspend fun deleteReturn(returnItem: ReturnItem) = returnDao.deleteReturn(returnItem)
    fun getAllReturns(): Flow<List<ReturnItem>> = returnDao.getAllReturns()
    fun searchReturns(query: String): Flow<List<ReturnItem>> = returnDao.searchReturns(query)
}
