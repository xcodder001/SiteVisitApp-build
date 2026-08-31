package com.sitevisit.app.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sitevisit.app.data.entity.SiteVisit

/**
 * Schedules / cancels an exact alarm that fires [SiteVisit.reminderMinutesBefore]
 * minutes before a visit's [SiteVisit.visitDateTime].
 */
object ReminderScheduler {

    fun schedule(context: Context, visit: SiteVisit, siteName: String) {
        if (!visit.reminderEnabled) {
            cancel(context, visit)
            return
        }
        val triggerAt = visit.visitDateTime - visit.reminderMinutesBefore * 60_000L
        if (triggerAt <= System.currentTimeMillis()) return // don't schedule for the past

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_VISIT_ID, visit.id)
            putExtra(ReminderReceiver.EXTRA_TITLE, visit.title)
            putExtra(ReminderReceiver.EXTRA_SITE_NAME, siteName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            visit.reminderRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                } else {
                    // Fall back to inexact if the user hasn't granted exact-alarm permission
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context, visit: SiteVisit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            visit.reminderRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
