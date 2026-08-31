package com.sitevisit.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sitevisit.app.data.dao.PaymentDao
import com.sitevisit.app.data.dao.PhotoDao
import com.sitevisit.app.data.dao.QuotationDao
import com.sitevisit.app.data.dao.SiteDao
import com.sitevisit.app.data.dao.SiteVisitDao
import com.sitevisit.app.data.entity.Payment
import com.sitevisit.app.data.entity.Quotation
import com.sitevisit.app.data.entity.QuotationItem
import com.sitevisit.app.data.entity.Site
import com.sitevisit.app.data.entity.SitePhoto
import com.sitevisit.app.data.entity.SiteVisit

@Database(
    entities = [
        Site::class,
        SiteVisit::class,
        Quotation::class,
        QuotationItem::class,
        Payment::class,
        SitePhoto::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteDao(): SiteDao
    abstract fun siteVisitDao(): SiteVisitDao
    abstract fun quotationDao(): QuotationDao
    abstract fun paymentDao(): PaymentDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sitevisit.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
