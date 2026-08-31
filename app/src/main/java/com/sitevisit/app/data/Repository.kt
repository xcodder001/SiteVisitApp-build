package com.sitevisit.app.data

import android.content.Context
import com.sitevisit.app.data.entity.Payment
import com.sitevisit.app.data.entity.Quotation
import com.sitevisit.app.data.entity.QuotationItem
import com.sitevisit.app.data.entity.Site
import com.sitevisit.app.data.entity.SitePhoto
import com.sitevisit.app.data.entity.SiteVisit
import kotlinx.coroutines.flow.Flow

/**
 * Single access point for all data operations. Screens/ViewModels talk to this,
 * not to the DAOs directly.
 */
class Repository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val siteDao = db.siteDao()
    private val visitDao = db.siteVisitDao()
    private val quotationDao = db.quotationDao()
    private val paymentDao = db.paymentDao()
    private val photoDao = db.photoDao()

    // Sites
    fun allSites(): Flow<List<Site>> = siteDao.getAll()
    fun site(id: Long): Flow<Site?> = siteDao.getById(id)
    suspend fun siteOnce(id: Long): Site? = siteDao.getByIdOnce(id)
    suspend fun addSite(site: Site): Long = siteDao.insert(site)
    suspend fun updateSite(site: Site) = siteDao.update(site)
    suspend fun deleteSite(site: Site) = siteDao.delete(site)

    // Visits
    fun allVisits(): Flow<List<SiteVisit>> = visitDao.getAll()
    fun visitsForSite(siteId: Long): Flow<List<SiteVisit>> = visitDao.getForSite(siteId)
    fun upcomingVisits(from: Long = System.currentTimeMillis()): Flow<List<SiteVisit>> =
        visitDao.getUpcoming(from)
    suspend fun visitOnce(id: Long): SiteVisit? = visitDao.getByIdOnce(id)
    suspend fun addVisit(visit: SiteVisit): Long = visitDao.insert(visit)
    suspend fun updateVisit(visit: SiteVisit) = visitDao.update(visit)
    suspend fun deleteVisit(visit: SiteVisit) = visitDao.delete(visit)

    // Quotations
    fun allQuotations(): Flow<List<Quotation>> = quotationDao.getAll()
    fun quotationsForSite(siteId: Long): Flow<List<Quotation>> = quotationDao.getForSite(siteId)
    fun quotation(id: Long): Flow<Quotation?> = quotationDao.getById(id)
    suspend fun addQuotation(q: Quotation): Long = quotationDao.insert(q)
    suspend fun updateQuotation(q: Quotation) = quotationDao.update(q)
    suspend fun deleteQuotation(q: Quotation) = quotationDao.delete(q)

    fun quotationItems(quotationId: Long): Flow<List<QuotationItem>> =
        quotationDao.getItems(quotationId)
    suspend fun addQuotationItem(item: QuotationItem): Long = quotationDao.insertItem(item)
    suspend fun updateQuotationItem(item: QuotationItem) = quotationDao.updateItem(item)
    suspend fun deleteQuotationItem(item: QuotationItem) = quotationDao.deleteItem(item)

    // Payments
    fun allPayments(): Flow<List<Payment>> = paymentDao.getAll()
    fun paymentsForSite(siteId: Long): Flow<List<Payment>> = paymentDao.getForSite(siteId)
    suspend fun addPayment(payment: Payment): Long = paymentDao.insert(payment)
    suspend fun updatePayment(payment: Payment) = paymentDao.update(payment)
    suspend fun deletePayment(payment: Payment) = paymentDao.delete(payment)

    // Photos
    fun photosForSite(siteId: Long): Flow<List<SitePhoto>> = photoDao.getForSite(siteId)
    fun photosForVisit(visitId: Long): Flow<List<SitePhoto>> = photoDao.getForVisit(visitId)
    suspend fun addPhoto(photo: SitePhoto): Long = photoDao.insert(photo)
    suspend fun deletePhoto(photo: SitePhoto) = photoDao.delete(photo)
}
