package com.sitevisit.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "site_photos",
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
data class SitePhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val visitId: Long? = null,
    val uri: String,
    val caption: String = "",
    val takenAt: Long = System.currentTimeMillis()
)
