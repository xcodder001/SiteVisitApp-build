package com.sitevisit.app.data.dao

import androidx.room.*
import com.sitevisit.app.data.entity.Quotation
import com.sitevisit.app.data.entity.QuotationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationDao {
    @Query("SELECT * FROM quotations ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Quotation>>

    @Query("SELECT * FROM quotations WHERE siteId = :siteId ORDER BY createdAt DESC")
    fun getForSite(siteId: Long): Flow<List<Quotation>>

    @Query("SELECT * FROM quotations WHERE id = :id")
    fun getById(id: Long): Flow<Quotation?>

    @Insert
    suspend fun insert(quotation: Quotation): Long

    @Update
    suspend fun update(quotation: Quotation)

    @Delete
    suspend fun delete(quotation: Quotation)

    @Query("SELECT * FROM quotation_items WHERE quotationId = :quotationId")
    fun getItems(quotationId: Long): Flow<List<QuotationItem>>

    @Insert
    suspend fun insertItem(item: QuotationItem): Long

    @Update
    suspend fun updateItem(item: QuotationItem)

    @Delete
    suspend fun deleteItem(item: QuotationItem)
}
