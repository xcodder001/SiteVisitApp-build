package com.sitevisit.app.data.dao

import androidx.room.*
import com.sitevisit.app.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAll(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE siteId = :siteId ORDER BY date DESC")
    fun getForSite(siteId: Long): Flow<List<Payment>>

    @Insert
    suspend fun insert(payment: Payment): Long

    @Update
    suspend fun update(payment: Payment)

    @Delete
    suspend fun delete(payment: Payment)
}
