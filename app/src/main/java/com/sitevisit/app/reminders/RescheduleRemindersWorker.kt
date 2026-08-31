package com.sitevisit.app.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sitevisit.app.SiteVisitApplication
import kotlinx.coroutines.flow.first

class RescheduleRemindersWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = (applicationContext as SiteVisitApplication).repository
        val visits = repo.upcomingVisits().first()
        for (visit in visits) {
            if (visit.reminderEnabled) {
                val site = repo.siteOnce(visit.siteId)
                ReminderScheduler.schedule(applicationContext, visit, site?.name ?: "")
            }
        }
        return Result.success()
    }
}
