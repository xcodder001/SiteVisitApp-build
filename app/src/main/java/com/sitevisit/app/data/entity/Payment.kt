package com.sitevisit.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class PaymentType { CHARGE, PAYMENT }
// CHARGE = amount billed/owed (increases balance owed by client)
// PAYMENT = amount received from client (decreases balance owed)

@Entity(
    tableName = "payments",
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
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val type: PaymentType,
    val amount: Double,
    val description: String = "",
    val date: Long = System.currentTimeMillis()
)
