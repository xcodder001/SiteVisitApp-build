package com.sitevisit.app.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Alarms scheduled with AlarmManager are cleared when the device reboots, so we
 * re-schedule every future, reminder-enabled visit via a background worker.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val request = OneTimeWorkRequestBuilder<RescheduleRemindersWorker>().build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("reschedule_reminders", ExistingWorkPolicy.REPLACE, request)
        }
    }
}
