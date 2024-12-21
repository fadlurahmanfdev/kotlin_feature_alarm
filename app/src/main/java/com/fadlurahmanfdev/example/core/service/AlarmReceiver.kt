package com.fadlurahmanfdev.example.core.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmReceiver
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmService

class AlarmReceiver : FeatureAlarmReceiver() {
    override fun onReceiveActionSetAlarm(context: Context, intent: Intent, extras: Bundle?) {
        Log.d(this::class.java.simpleName, "on receive action set alarm")
        FeatureAlarmService.startPlayingAlarm(context, 1, extras, AlarmService::class.java)
    }

    override fun onReceiveActionDismissAlarm(context: Context, intent: Intent) {
        Log.d(this::class.java.simpleName, "on receive action dismiss alarm")
        FeatureAlarmService.stopPlayingAlarm(context, AlarmService::class.java)
    }

    override fun onReceiveActionSnoozeAlarm(context: Context, intent: Intent, intervalInSecond: Int) {
        Log.d(this::class.java.simpleName, "on receive action snooze alarm")
    }
}