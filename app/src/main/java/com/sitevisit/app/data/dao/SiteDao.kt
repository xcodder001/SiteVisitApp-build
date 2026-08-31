package com.sitevisit.app.data.dao

import androidx.room.*
import com.sitevisit.app.data.entity.Site
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Site>>

    @Query("SELECT * FROM sites WHERE id = :id")
    fun getById(id: Long): Flow<Site?>

    @Query("SELECT * FROM sites WHERE id = :id")
    suspend fun getByIdOnce(id: Long): Site?

    @Insert
    suspend fun insert(site: Site): Long

    @Update
    suspend fun update(site: Site)

    @Delete
    suspend fun delete(site: Site)
}
