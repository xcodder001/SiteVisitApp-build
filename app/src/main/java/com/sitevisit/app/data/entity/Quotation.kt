package com.sitevisit.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class QuotationStatus { DRAFT, SENT, ACCEPTED, REJECTED }

@Entity(
    tableName = "quotations",
    foreignKeys = [
        ForeignKey(
            entity = Site::class,
            parentColumns = ["id"],
            childColumns = ["siteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("siteId")]
)
data class Quotation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val title: String,
    val taxPercent: Double = 0.0,
    val discount: Double = 0.0,
    val status: QuotationStatus = QuotationStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "quotation_items",
    foreignKeys = [
        ForeignKey(
            entity = Quotation::class,
            parentColumns = ["id"],
            childColumns = ["quotationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quotationId")]
)
data class QuotationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quotationId: Long,
    val description: String,
    val quantity: Double,
    val unitPrice: Double
)
