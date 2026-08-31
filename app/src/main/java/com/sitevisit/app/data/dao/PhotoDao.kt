package com.sitevisit.app.data.dao

import androidx.room.*
import com.sitevisit.app.data.entity.SitePhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM site_photos WHERE siteId = :siteId ORDER BY takenAt DESC")
    fun getForSite(siteId: Long): Flow<List<SitePhoto>>

    @Query("SELECT * FROM site_photos WHERE visitId = :visitId ORDER BY takenAt DESC")
    fun getForVisit(visitId: Long): Flow<List<SitePhoto>>

    @Insert
    suspend fun insert(photo: SitePhoto): Long

    @Delete
    suspend fun delete(photo: SitePhoto)
}
