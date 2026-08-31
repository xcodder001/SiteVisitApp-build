package com.sitevisit.app.data.dao

import androidx.room.*
import com.sitevisit.app.data.entity.SiteVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteVisitDao {
    @Query("SELECT * FROM site_visits ORDER BY visitDateTime ASC")
    fun getAll(): Flow<List<SiteVisit>>

    @Query("SELECT * FROM site_visits WHERE siteId = :siteId ORDER BY visitDateTime DESC")
    fun getForSite(siteId: Long): Flow<List<SiteVisit>>

    @Query("SELECT * FROM site_visits WHERE id = :id")
    suspend fun getByIdOnce(id: Long): SiteVisit?

    @Query("SELECT * FROM site_visits WHERE visitDateTime >= :fromTime ORDER BY visitDateTime ASC")
    fun getUpcoming(fromTime: Long): Flow<List<SiteVisit>>

    @Insert
    suspend fun insert(visit: SiteVisit): Long

    @Update
    suspend fun update(visit: SiteVisit)

    @Delete
    suspend fun delete(visit: SiteVisit)
}
