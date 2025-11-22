package com.fadlurahmanfdev.example.receiver

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.fadlurahmanfdev.example.service.AppAlarmService
import com.fadlurahmanfdev.whispr.WhisprAlarmManager
import com.fadlurahmanfdev.whispr.receiver.BaseWhisprAlarmReceiver

class AppAlarmReceiver : BaseWhisprAlarmReceiver() {
    override fun onReceivedAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        title: String?,
        text: String?,
        extras: Bundle?
    ) {
        Log.d(
            this::class.java.simpleName,
            "App-Whispr-LOG %%% - on received alarm title: $title, text: $text"
        )
        WhisprAlarmManager.startPlayingAlarm(
            context = context,
            notificationId = notificationId,
            title = title ?: "-",
            text = text ?: "-",
            clazz = AppAlarmService::class.java,
        )
    }

    override fun onReceiveActionDismissAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int
    ) {
        Log.d(this::class.java.simpleName, "on receive action dismiss alarm")
        WhisprAlarmManager.stopPlayingAlarm(
            context,
            notificationId = notificationId,
            AppAlarmService::class.java
        )
    }

    override fun onReceiveActionSnoozeAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        intervalInSecond: Int
    ) {
        WhisprAlarmManager.stopPlayingAlarm(
            context,
            notificationId = notificationId,
            AppAlarmService::class.java
        )
        whispr.setExact(
            type = AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis = SystemClock.elapsedRealtime() + intervalInSecond,
            pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
                context = context,
                notificationId = notificationId,
                requestCode = 1000,
                title = "Snoozed Pending Intent",
                text = "Snoozed Pending Intent Text",
                bundle = null,
                clazz = AppAlarmReceiver::class.java,
            )
        )
    }
}