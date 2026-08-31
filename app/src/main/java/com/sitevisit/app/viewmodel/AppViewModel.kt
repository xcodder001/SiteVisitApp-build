package com.sitevisit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sitevisit.app.SiteVisitApplication
import com.sitevisit.app.data.entity.*
import com.sitevisit.app.reminders.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as SiteVisitApplication).repository

    val sites: StateFlow<List<Site>> = repo.allSites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisits: StateFlow<List<SiteVisit>> = repo.allVisits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingVisits: StateFlow<List<SiteVisit>> = repo.upcomingVisits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuotations: StateFlow<List<Quotation>> = repo.allQuotations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Payment>> = repo.allPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---- Sites ----
    fun addSite(name: String, address: String, contactName: String, contactPhone: String,
                lat: Double, lng: Double, notes: String, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.addSite(
                Site(
                    name = name, address = address, contactName = contactName,
                    contactPhone = contactPhone, latitude = lat, longitude = lng, notes = notes
                )
            )
            onDone(id)
        }
    }

    fun updateSite(site: Site) = viewModelScope.launch { repo.updateSite(site) }
    fun deleteSite(site: Site) = viewModelScope.launch { repo.deleteSite(site) }

    fun siteFlow(id: Long) = repo.site(id)
    fun visitsForSite(siteId: Long) = repo.visitsForSite(siteId)
    fun quotationsForSite(siteId: Long) = repo.quotationsForSite(siteId)
    fun paymentsForSite(siteId: Long) = repo.paymentsForSite(siteId)
    fun photosForSite(siteId: Long) = repo.photosForSite(siteId)
    fun quotationItems(quotationId: Long) = repo.quotationItems(quotationId)
    fun quotationFlow(id: Long) = repo.quotation(id)

    // ---- Visits + reminders ----
    fun addVisit(
        siteId: Long, siteName: String, title: String, dateTime: Long, notes: String,
        reminderEnabled: Boolean, reminderMinutesBefore: Int
    ) {
        viewModelScope.launch {
            val requestCode = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val visit = SiteVisit(
                siteId = siteId, title = title, visitDateTime = dateTime, notes = notes,
                reminderEnabled = reminderEnabled, reminderMinutesBefore = reminderMinutesBefore,
                reminderRequestCode = requestCode
            )
            val id = repo.addVisit(visit)
            if (reminderEnabled) {
                ReminderScheduler.schedule(getApplication(), visit.copy(id = id), siteName)
            }
        }
    }

    fun updateVisit(visit: SiteVisit, siteName: String) {
        viewModelScope.launch {
            repo.updateVisit(visit)
            ReminderScheduler.schedule(getApplication(), visit, siteName)
        }
    }

    fun deleteVisit(visit: SiteVisit) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), visit)
            repo.deleteVisit(visit)
        }
    }

    fun setVisitStatus(visit: SiteVisit, status: VisitStatus) {
        viewModelScope.launch { repo.updateVisit(visit.copy(status = status)) }
    }

    // ---- Quotations ----
    fun addQuotation(siteId: Long, title: String, taxPercent: Double, discount: Double,
                      items: List<Triple<String, Double, Double>>, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val qId = repo.addQuotation(Quotation(siteId = siteId, title = title, taxPercent = taxPercent, discount = discount))
            items.forEach { (desc, qty, price) ->
                repo.addQuotationItem(QuotationItem(quotationId = qId, description = desc, quantity = qty, unitPrice = price))
            }
            onDone(qId)
        }
    }

    fun addQuotationItem(quotationId: Long, description: String, quantity: Double, unitPrice: Double) {
        viewModelScope.launch {
            repo.addQuotationItem(QuotationItem(quotationId = quotationId, description = description, quantity = quantity, unitPrice = unitPrice))
        }
    }

    fun deleteQuotationItem(item: QuotationItem) = viewModelScope.launch { repo.deleteQuotationItem(item) }
    fun updateQuotationStatus(q: Quotation, status: QuotationStatus) =
        viewModelScope.launch { repo.updateQuotation(q.copy(status = status)) }
    fun deleteQuotation(q: Quotation) = viewModelScope.launch { repo.deleteQuotation(q) }

    // ---- Payments / balances ----
    fun addPayment(siteId: Long, type: PaymentType, amount: Double, description: String) {
        viewModelScope.launch {
            repo.addPayment(Payment(siteId = siteId, type = type, amount = amount, description = description))
        }
    }

    fun deletePayment(payment: Payment) = viewModelScope.launch { repo.deletePayment(payment) }

    // ---- Photos ----
    fun addPhoto(siteId: Long, visitId: Long?, uri: String, caption: String) {
        viewModelScope.launch {
            repo.addPhoto(SitePhoto(siteId = siteId, visitId = visitId, uri = uri, caption = caption))
        }
    }

    fun deletePhoto(photo: SitePhoto) = viewModelScope.launch { repo.deletePhoto(photo) }
}

/** Balance owed by a site = total charges - total payments received. */
fun List<Payment>.balanceFor(siteId: Long): Double =
    this.filter { it.siteId == siteId }.sumOf { if (it.type == PaymentType.CHARGE) it.amount else -it.amount }

fun List<QuotationItem>.subtotal(): Double = sumOf { it.quantity * it.unitPrice }

fun Quotation.total(items: List<QuotationItem>): Double {
    val subtotal = items.subtotal()
    val afterDiscount = subtotal - discount
    val tax = afterDiscount * (taxPercent / 100.0)
    return afterDiscount + tax
}
