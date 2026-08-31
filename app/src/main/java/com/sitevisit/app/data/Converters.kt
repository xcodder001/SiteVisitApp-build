package com.sitevisit.app.data

import androidx.room.TypeConverter
import com.sitevisit.app.data.entity.PaymentType
import com.sitevisit.app.data.entity.QuotationStatus
import com.sitevisit.app.data.entity.VisitStatus

class Converters {
    @TypeConverter
    fun fromVisitStatus(value: VisitStatus): String = value.name

    @TypeConverter
    fun toVisitStatus(value: String): VisitStatus = VisitStatus.valueOf(value)

    @TypeConverter
    fun fromQuotationStatus(value: QuotationStatus): String = value.name

    @TypeConverter
    fun toQuotationStatus(value: String): QuotationStatus = QuotationStatus.valueOf(value)

    @TypeConverter
    fun fromPaymentType(value: PaymentType): String = value.name

    @TypeConverter
    fun toPaymentType(value: String): PaymentType = PaymentType.valueOf(value)
}
