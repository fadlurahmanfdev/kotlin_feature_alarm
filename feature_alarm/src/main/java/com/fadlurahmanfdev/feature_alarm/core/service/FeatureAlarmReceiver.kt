package com.fadlurahmanfdev.feature_alarm.core.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.fadlurahmanfdev.feature_alarm.core.constant.FeatureAlarmConstant
import com.fadlurahmanfdev.feature_alarm.presentation.BaseFullScreenIntentActivity

abstract class FeatureAlarmReceiver : BroadcastReceiver() {
    lateinit var alarmManager: AlarmManager

    companion object {
        fun <T : FeatureAlarmReceiver> getPendingIntentSetAlarm(
            context: Context,
            requestCode: Int,
            bundle: Bundle?,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = FeatureAlarmConstant.ACTION_SET_ALARM
                if (bundle != null) {
                    putExtras(bundle)
                }
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
        }

        fun <T : FeatureAlarmReceiver> getPendingIntentDismissAlarm(
            context: Context,
            requestCode: Int,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = FeatureAlarmConstant.ACTION_DISMISS_ALARM
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
        }

        fun <T : FeatureAlarmReceiver> sendBroadcastSendAlarm(
            context: Context,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz).apply {
                action = FeatureAlarmConstant.ACTION_DISMISS_ALARM
            }
            context.sendBroadcast(intent)
        }

        fun <T : FeatureAlarmReceiver> getPendingIntentSnoozeAlarm(
            context: Context,
            requestCode: Int,
            intervalInSecond: Int,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = FeatureAlarmConstant.ACTION_SNOOZE_ALARM
                putExtra(FeatureAlarmConstant.PARAM_INTERVAL_IN_SECOND, intervalInSecond)
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
        }

        fun <T : BaseFullScreenIntentActivity> getFullScreenIntent(
            context: Context,
            clazz: Class<T>
        ): Intent {
            return Intent(context, clazz)
        }

        fun getFullScreenPendingIntent(
            context: Context,
            requestCode: Int,
            fullScreenIntent: Intent,
        ): PendingIntent {
            return PendingIntent.getActivity(
                context,
                requestCode,
                fullScreenIntent,
                getFlagPendingIntent()
            )
        }


        private fun getFlagPendingIntent(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (!this::alarmManager.isInitialized) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        when (intent?.action) {
            FeatureAlarmConstant.ACTION_SET_ALARM -> {
                onReceiveActionSetAlarm(context, intent, intent.extras)
            }

            FeatureAlarmConstant.ACTION_SNOOZE_ALARM -> {
                val intervalInSecond =
                    intent.getIntExtra(FeatureAlarmConstant.PARAM_INTERVAL_IN_SECOND, 0)
                if (intervalInSecond != -1) {
                    onReceiveActionSnoozeAlarm(context, intent, intervalInSecond)
                }
            }

            FeatureAlarmConstant.ACTION_DISMISS_ALARM -> {
                onReceiveActionDismissAlarm(context, intent)
            }
        }
    }

    abstract fun onReceiveActionSetAlarm(context: Context, intent: Intent, extras: Bundle?)

    abstract fun onReceiveActionDismissAlarm(context: Context, intent: Intent)

    abstract fun onReceiveActionSnoozeAlarm(context: Context, intent: Intent, intervalInSecond: Int)
}