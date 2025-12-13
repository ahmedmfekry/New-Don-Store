package com.blooddonation.management.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.blooddonation.management.data.models.Distribution
import kotlinx.coroutines.flow.Flow

@Dao
interface DistributionDao {
    @Insert
    suspend fun insertDistribution(distribution: Distribution): Long

    @Update
    suspend fun updateDistribution(distribution: Distribution)

    @Delete
    suspend fun deleteDistribution(distribution: Distribution)

    @Query("SELECT * FROM distributions WHERE id = :id")
    suspend fun getDistributionById(id: Int): Distribution?

    @Query("SELECT * FROM distributions ORDER BY distributionDate DESC")
    fun getAllDistributions(): Flow<List<Distribution>>

    @Query("SELECT * FROM distributions WHERE campaignName LIKE '%' || :campaignName || '%' ORDER BY distributionDate DESC")
    fun getDistributionsByCampaign(campaignName: String): Flow<List<Distribution>>

    @Query("SELECT * FROM distributions WHERE categoryName LIKE '%' || :query || '%'")
    fun searchDistributions(query: String): Flow<List<Distribution>>

    @Query("SELECT * FROM distributions WHERE distributionDate BETWEEN :startDate AND :endDate")
    fun getDistributionsByDateRange(startDate: Long, endDate: Long): Flow<List<Distribution>>
}
