package com.fadlurahmanfdev.example.service

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.fadlurahmanfdev.example.constant.AppConfig
import com.fadlurahmanfdev.example.receiver.AppAlarmReceiver
import com.fadlurahmanfdev.whispr.Whispr
import com.fadlurahmanfdev.whispr.WhisprAlarmManager
import com.fadlurahmanfdev.whispr.receiver.WhisprDeviceRebootReceiver

class AppDeviceRebootReceiver : WhisprDeviceRebootReceiver() {
    private var whispr: Whispr? = null

    override fun onDeviceRebootCompleted(context: Context?, intent: Intent) {
        Log.d(this::class.java.simpleName, "App-Whispr-LOG %%% - device reboot completed triggered")
        if (context == null) return
        if (whispr == null) {
            whispr = Whispr(context)
        }

        whispr!!.setExact(
            type = AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis = SystemClock.elapsedRealtime() + 30000L,
            pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
                context = context,
                notificationId = AppConfig.ALARM_NOTIFICATION_ID_FOR_ELAPSED,
                requestCode = AppConfig.ALARM_ELAPSED_PENDING_INTENT_REQUEST_CODE,
                bundle = null,
                title = "Elapsed Alarm From Boot Receiver",
                text = "Elapsed Alarm From Boot Receiver",
                clazz = AppAlarmReceiver::class.java,
            )
        )
    }
}