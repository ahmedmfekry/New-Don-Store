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
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloodDonationRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val inventoryDao: InventoryDao,
    private val distributionDao: DistributionDao,
    private val returnDao: ReturnDao,
    private val authManager: com.blooddonation.management.data.firebase.FirebaseAuthManager
) {

    // Category Operations
    suspend fun addCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    fun searchCategories(query: String): Flow<List<Category>> = categoryDao.searchCategories(query)

    // Inventory Operations
    suspend fun addInventoryItem(item: InventoryItem) {
        inventoryDao.insertItem(item)
        syncInventory()
    }
    suspend fun updateInventoryItem(item: InventoryItem) {
        inventoryDao.updateItem(item)
        syncInventory()
    }
    suspend fun deleteInventoryItem(item: InventoryItem) {
        inventoryDao.deleteItem(item)
        syncInventory()
    }
    fun getAllInventoryItems(): Flow<List<InventoryItem>> = inventoryDao.getAllItems()
    fun getInventoryByCategory(categoryId: Int): Flow<List<InventoryItem>> = 
        inventoryDao.getItemsByCategory(categoryId)
    fun searchInventory(query: String): Flow<List<InventoryItem>> = inventoryDao.searchItems(query)
    fun getExpiringItems(futureDate: Long): Flow<List<InventoryItem>> = 
        inventoryDao.getExpiringItems(futureDate)

    // Distribution Operations
    suspend fun addDistribution(distribution: Distribution) {
        distributionDao.insertDistribution(distribution)
        syncDistributions()
    }
    suspend fun updateDistribution(distribution: Distribution) {
        distributionDao.updateDistribution(distribution)
        syncDistributions()
    }
    suspend fun deleteDistribution(distribution: Distribution) {
        distributionDao.deleteDistribution(distribution)
        syncDistributions()
    }
    fun getAllDistributions(): Flow<List<Distribution>> = distributionDao.getAllDistributions()
    fun searchDistributions(query: String): Flow<List<Distribution>> = 
        distributionDao.searchDistributions(query)

    // Return Operations
    suspend fun addReturn(returnItem: ReturnItem) {
        returnDao.insertReturn(returnItem)
        syncReturns()
    }
    suspend fun updateReturn(returnItem: ReturnItem) {
        returnDao.updateReturn(returnItem)
        syncReturns()
    }
    suspend fun deleteReturn(returnItem: ReturnItem) {
        returnDao.deleteReturn(returnItem)
        syncReturns()
    }
    fun getAllReturns(): Flow<List<ReturnItem>> = returnDao.getAllReturns()
    fun searchReturns(query: String): Flow<List<ReturnItem>> = returnDao.searchReturns(query)

    // Sync Logic
    private suspend fun syncInventory() {
        if (authManager.isUserLoggedIn()) {
            val user = authManager.getCurrentUser()
            if (user != null) {
                try {
                    val items = inventoryDao.getAllItems().first()
                    val data = items.map { 
                        mapOf(
                            "id" to it.id,
                            "categoryName" to it.categoryName,
                            "quantity" to it.quantity,
                            "unit" to it.unit,
                            "lotNumber" to it.lotNumber,
                            "expireDate" to it.expireDate,
                            "notes" to it.notes
                        ) 
                    }
                    authManager.syncInventoryToFirebase(user.uid, data)
                } catch (e: Exception) {
                    // Log or handle sync error silently
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun syncDistributions() {
        if (authManager.isUserLoggedIn()) {
            val user = authManager.getCurrentUser()
            if (user != null) {
                try {
                    val items = distributionDao.getAllDistributions().first()
                    val data = items.map { 
                        mapOf(
                            "id" to it.id,
                            "itemName" to it.itemName,
                            "quantity" to it.quantity,
                            "recipient" to it.recipient,
                            "notes" to it.notes,
                            "date" to it.date
                        ) 
                    }
                    authManager.syncDistributionsToFirebase(user.uid, data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun syncReturns() {
        if (authManager.isUserLoggedIn()) {
            val user = authManager.getCurrentUser()
            if (user != null) {
                try {
                    val items = returnDao.getAllReturns().first()
                    val data = items.map { 
                        mapOf(
                            "id" to it.id,
                            "itemName" to it.itemName,
                            "quantity" to it.quantity,
                            "reason" to it.reason,
                            "date" to it.date
                        ) 
                    }
                    authManager.syncReturnsToFirebase(user.uid, data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
