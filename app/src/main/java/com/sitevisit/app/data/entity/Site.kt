package com.sitevisit.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A physical location / client site that visits, quotations, and payments are attached to.
 */
@Entity(tableName = "sites")
data class Site(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val latitude: Double,
    val longitude: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
