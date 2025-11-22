package com.fadlurahmanfdev.example.service

import android.app.NotificationManager
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.constant.AppConfig
import com.fadlurahmanfdev.example.notification.AppNotification
import com.fadlurahmanfdev.example.presentation.ExampleFullScreenIntentActivity
import com.fadlurahmanfdev.example.receiver.AppAlarmReceiver
import com.fadlurahmanfdev.whispr.WhisprAlarmManager
import com.fadlurahmanfdev.whispr.service.BaseWhisprAlarmService

class AppAlarmService : BaseWhisprAlarmService() {
    lateinit var appNotification: AppNotification

//    override var defaultUriRingtone: Uri =
//        Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/" + R.raw.adhan)

    override var defaultUriRingtone: Uri =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    override fun onWhisprAlarmServiceCreated() {
        appNotification = AppNotification(applicationContext)

        appNotification.createNotificationChannel(
            id = AppConfig.ALARM_NOTIFICATION_CHANNEL_ID,
            name = "Alarm Notification",
            description = "Alarm for notification",
            importance = NotificationManager.IMPORTANCE_MAX,
            sound = null,
        )
    }

    override fun onReceiveActionPlayAlarm(
        notificationId: Int,
        title: String?,
        text: String?,
        bundle: Bundle?
    ) {
        val fullScreenIntent = Intent(
            applicationContext,
            ExampleFullScreenIntentActivity::class.java
        ).apply {
            val newBundle = bundle ?: Bundle()

            newBundle.apply {
                putExtra("PARAM_NOTIFICATION_ID", notificationId)
                putExtra("PARAM_TITLE", title)
                putExtra("PARAM_TEXT", text)
            }

            putExtras(newBundle)
        }

        val fullScreenPendingIntent =
            WhisprAlarmManager.getPendingIntentFullScreen(
                applicationContext,
                requestCode = 0,
                fullScreenIntent
            )
        val snoozePendingIntent = WhisprAlarmManager.getPendingIntentSnoozeAlarm(
            applicationContext,
            notificationId = notificationId,
            requestCode = 1,
            intervalInSecond = 60000L,
            clazz = AppAlarmReceiver::class.java,
        )
        val dismissPendingIntent = WhisprAlarmManager.getPendingIntentDismissAlarm(
            applicationContext,
            notificationId = notificationId,
            requestCode = 2,
            clazz = AppAlarmReceiver::class.java,
        )
        val notificationBuilder = appNotification.getFullScreenNotificationBuilder(
            channelId = AppConfig.ALARM_NOTIFICATION_CHANNEL_ID,
            fullScreenIntent = fullScreenPendingIntent,
            title = title ?: "DEFAULT TITLE",
            text = text ?: "DEFAULT TEXT",
            icon = R.drawable.baseline_alarm_24,
            deletePendingIntent = dismissPendingIntent
        ).apply {
            addAction(
                NotificationCompat.Action.Builder(null, "Snooze", snoozePendingIntent).build()
            )
            addAction(
                NotificationCompat.Action.Builder(null, "Dismiss", dismissPendingIntent).build()
            )
        }

        val notification = notificationBuilder.build()

        appNotification.notify(notificationId, notification)
    }

    override fun onReceiveActionStopAlarm(notificationId: Int) {
        appNotification.cancel(notificationId)
    }
}