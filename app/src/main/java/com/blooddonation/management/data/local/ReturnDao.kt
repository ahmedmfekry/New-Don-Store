package com.blooddonation.management.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.blooddonation.management.data.models.ReturnItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ReturnDao {
    @Insert
    suspend fun insertReturn(returnItem: ReturnItem): Long

    @Update
    suspend fun updateReturn(returnItem: ReturnItem)

    @Delete
    suspend fun deleteReturn(returnItem: ReturnItem)

    @Query("SELECT * FROM returns WHERE id = :id")
    suspend fun getReturnById(id: Int): ReturnItem?

    @Query("SELECT * FROM returns ORDER BY returnDate DESC")
    fun getAllReturns(): Flow<List<ReturnItem>>

    @Query("SELECT * FROM returns WHERE categoryName LIKE '%' || :query || '%'")
    fun searchReturns(query: String): Flow<List<ReturnItem>>

    @Query("SELECT * FROM returns WHERE returnDate BETWEEN :startDate AND :endDate")
    fun getReturnsByDateRange(startDate: Long, endDate: Long): Flow<List<ReturnItem>>
}
