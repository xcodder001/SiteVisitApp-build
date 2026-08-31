package com.sitevisit.app.reminders

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.sitevisit.app.MainActivity
import com.sitevisit.app.SiteVisitApplication

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val visitId = intent.getLongExtra(EXTRA_VISIT_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Site visit"
        val siteName = intent.getStringExtra(EXTRA_SITE_NAME) ?: ""

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingContentIntent = PendingIntent.getActivity(
            context, visitId.toInt(), contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SiteVisitApplication.REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming visit: $title")
            .setContentText(if (siteName.isNotBlank()) "at $siteName" else "Reminder")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingContentIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(visitId.toInt(), notification)
    }

    companion object {
        const val EXTRA_VISIT_ID = "extra_visit_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SITE_NAME = "extra_site_name"
    }
}
