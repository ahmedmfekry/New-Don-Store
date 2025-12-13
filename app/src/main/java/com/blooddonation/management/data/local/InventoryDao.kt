package com.blooddonation.management.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.blooddonation.management.data.models.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert
    suspend fun insertItem(item: InventoryItem): Long

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getItemById(id: Int): InventoryItem?

    @Query("SELECT * FROM inventory ORDER BY addedDate DESC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE categoryId = :categoryId ORDER BY addedDate DESC")
    fun getItemsByCategory(categoryId: Int): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE status = :status ORDER BY addedDate DESC")
    fun getItemsByStatus(status: String): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE categoryName LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE expireDate < :futureDate AND status = 'متاح'")
    fun getExpiringItems(futureDate: Long): Flow<List<InventoryItem>>

    @Query("SELECT SUM(quantity) FROM inventory WHERE categoryId = :categoryId AND status = 'متاح'")
    fun getCategoryTotalQuantity(categoryId: Int): Flow<Int?>
}
