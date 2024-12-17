package com.fadlurahmanfdev.example.core.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.fadlurahmanfdev.example.BuildConfig
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.repository.AlarmNotificationRepositoryImpl
import com.fadlurahmanfdev.example.presentation.ExampleFullScreenIntentActivity
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmReceiver
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmService
import com.fadlurahmanfdev.feature_alarm.data.dto.FeatureAlarmNotificationAction

class AlarmService : FeatureAlarmService() {
    lateinit var notificationRepository: AlarmNotificationRepositoryImpl

    override var defaultUriRingtone: Uri =
        Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/" + R.raw.adhan)

    override fun onFeatureAlarmServiceCreate() {
        notificationRepository = AlarmNotificationRepositoryImpl(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationRepository.createNotificationChannel(
                id = "ALARM",
                name = "Alarm",
                description = "Alarm Notification",
                importance = NotificationManager.IMPORTANCE_HIGH,
                sound = null,
            )
        }
    }

    override fun onAlarmNotification(context: Context, bundle: Bundle?): Notification {
        val fullScreenIntent = FeatureAlarmReceiver.getFullScreenIntent(
            context,
            ExampleFullScreenIntentActivity::class.java
        ).apply {
            putExtra("TITLE", "INI TITLE")
            putExtra("DESC", "INI DESC")
        }
        val fullScreenPendingIntent =
            FeatureAlarmReceiver.getFullScreenPendingIntent(context, 0, fullScreenIntent)
        val dismissPendingIntent = FeatureAlarmReceiver.getPendingIntentDismissAlarm(
            context,
            1,
            AlarmReceiver::class.java,
        )
        val notificationBuilder = notificationRepository.getFullScreenNotificationBuilder(
            channelId = "ALARM",
            fullScreenIntent = fullScreenPendingIntent,
            title = "Tes Alarm",
            text = "Tes Alarm",
            icon = R.drawable.baseline_developer_mode_24,
            actions = listOf(
                FeatureAlarmNotificationAction(
                    icon = R.drawable.baseline_developer_mode_24,
                    action = dismissPendingIntent,
                    textAction = "Dismiss"
                )
            ),
        )
        return notificationBuilder.build()
    }
}