package com.sitevisit.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class VisitStatus { SCHEDULED, COMPLETED, CANCELLED }

@Entity(
    tableName = "site_visits",
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
data class SiteVisit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val title: String,
    val visitDateTime: Long, // epoch millis
    val notes: String = "",
    val status: VisitStatus = VisitStatus.SCHEDULED,
    val reminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 60,
    val reminderRequestCode: Int = 0
)
