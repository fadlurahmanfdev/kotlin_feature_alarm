package com.fadlurahmanfdev.whispr

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import java.util.Calendar

class Whispr(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * set exact alarm for specific time.
     *
     * @param [type] type of alarm, [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] is using actually time (e.g., schedule alarm at 22:00)
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] is using how long from now to ring it (e.g., schedule 20 minutes from now)
     * @param [triggerAtMillis] time of the alarm will be triggered, for [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] usually using [Calendar], for
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] usually using [SystemClock]
     * @param [pendingIntent] intent of operation, call from [WhisprAlarmManager]
     * */
    fun setExact(
        type: Int = AlarmManager.RTC_WAKEUP,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                type,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                type,
                triggerAtMillis,
                pendingIntent
            )
        }
        Log.d(this::class.java.simpleName, "Whispr-LOG %%% - successfully set exact alarm")
    }

    /**
     * set exact repeating alarm
     *
     * @param [type] type of alarm, [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] is using actually time (e.g., schedule alarm at 22:00)
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] is using how long from now to ring it (e.g., schedule 20 minutes from now)
     * @param [triggerAtMillis] time of the alarm will be triggered, for [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] usually using [Calendar], for
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] usually using [SystemClock]
     * @param [pendingIntent] intent of operation, call from [WhisprAlarmManager]
     * */
    fun setRepeating(
        type: Int = AlarmManager.RTC_WAKEUP,
        triggerAtMillis: Long,
        intervalMillis: Long,
        pendingIntent: PendingIntent,
    ) {
        alarmManager.setRepeating(
            type,
            triggerAtMillis,
            intervalMillis,
            pendingIntent,
        )
        Log.d(this::class.java.simpleName, "Whispr-LOG %%% - successfully set exact repeating alarm")
    }

    /**
     * set inexact repeating alarm
     *
     * @param [type] type of alarm, [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] is using actually time (e.g., schedule alarm at 22:00)
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] is using how long from now to ring it (e.g., schedule 20 minutes from now)
     * @param [triggerAtMillis] time of the alarm will be triggered, for [AlarmManager.RTC] or [AlarmManager.RTC_WAKEUP] usually using [Calendar], for
     * [AlarmManager.ELAPSED_REALTIME] or [AlarmManager.ELAPSED_REALTIME_WAKEUP] usually using [SystemClock]
     * @param [pendingIntent] intent of operation, call from [WhisprAlarmManager]
     * */
    fun setInexactRepeating(
        type: Int = AlarmManager.RTC_WAKEUP,
        triggerAtMillis: Long,
        intervalMillis: Long,
        pendingIntent: PendingIntent,
    ) {
        alarmManager.setInexactRepeating(
            type,
            triggerAtMillis,
            intervalMillis,
            pendingIntent,
        )
        Log.d(this::class.java.simpleName, "Whispr-LOG %%% - successfully set inexact repeating alarm")
    }

    /**
     * cancel existing alarm, the request code from pending intent must be matched with the one existing
     *
     * @param [pendingIntent] intent of operation, call from [WhisprAlarmManager]
     * */
    fun cancel(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}