package com.sitevisit.app.ui.navigation

object Dest {
    const val SITES = "sites"
    const val MAP = "map"
    const val SCHEDULE = "schedule"

    const val SITE_ADD = "site_add"
    const val SITE_EDIT = "site_edit/{siteId}"
    const val SITE_DETAIL = "site_detail/{siteId}"
    const val VISIT_ADD = "visit_add/{siteId}"
    const val VISIT_EDIT = "visit_edit/{visitId}"
    const val QUOTATION_ADD = "quotation_add/{siteId}"
    const val QUOTATION_DETAIL = "quotation_detail/{quotationId}"
    const val PAYMENT_ADD = "payment_add/{siteId}"
    const val PHOTO_GALLERY = "photo_gallery/{siteId}"

    fun siteEdit(id: Long) = "site_edit/$id"
    fun siteDetail(id: Long) = "site_detail/$id"
    fun visitAdd(siteId: Long) = "visit_add/$siteId"
    fun visitEdit(visitId: Long) = "visit_edit/$visitId"
    fun quotationAdd(siteId: Long) = "quotation_add/$siteId"
    fun quotationDetail(quotationId: Long) = "quotation_detail/$quotationId"
    fun paymentAdd(siteId: Long) = "payment_add/$siteId"
    fun photoGallery(siteId: Long) = "photo_gallery/$siteId"
}
