package com.fadlurahmanfdev.feature_alarm.core.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.fadlurahmanfdev.feature_alarm.core.constant.FeatureAlarmConstant
import com.fadlurahmanfdev.feature_alarm.presentation.BaseFullScreenIntentActivity
import java.util.Calendar

abstract class FeatureAlarmReceiver : BroadcastReceiver() {

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
//            date: Date,
            clazz: Class<T>
        ): PendingIntent {
            val calendar = Calendar.getInstance()
//            calendar.time = date
            val intent = Intent(context, clazz).apply {
                action = FeatureAlarmConstant.ACTION_SNOOZE_ALARM
//                putExtra(FeatureAlarmConstant.PARAM_YEAR, calendar.get(Calendar.YEAR))
//                putExtra(FeatureAlarmConstant.PARAM_MONTH, calendar.get(Calendar.MONTH))
//                putExtra(FeatureAlarmConstant.PARAM_DAY, calendar.get(Calendar.DAY_OF_MONTH))
//                putExtra(FeatureAlarmConstant.PARAM_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
//                putExtra(FeatureAlarmConstant.PARAM_MINUTE, calendar.get(Calendar.MINUTE))
//                putExtra(FeatureAlarmConstant.PARAM_SECOND, calendar.get(Calendar.SECOND))
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

        when (intent?.action) {
            FeatureAlarmConstant.ACTION_SET_ALARM -> {
                onReceiveActionSetAlarm(context, intent, intent.extras)
            }

            FeatureAlarmConstant.ACTION_SNOOZE_ALARM -> {
                val year = intent.getIntExtra(FeatureAlarmConstant.PARAM_YEAR, -1)
                val month = intent.getIntExtra(FeatureAlarmConstant.PARAM_MONTH, -1)
                val day = intent.getIntExtra(FeatureAlarmConstant.PARAM_DAY, -1)
                val hour = intent.getIntExtra(FeatureAlarmConstant.PARAM_HOUR, -1)
                val minute = intent.getIntExtra(FeatureAlarmConstant.PARAM_MINUTE, -1)
                val second = intent.getIntExtra(FeatureAlarmConstant.PARAM_SECOND, -1)
                val snoozeTime = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, second)
                }.time
                onReceiveActionSnoozeAlarm(context, intent)
            }

            FeatureAlarmConstant.ACTION_DISMISS_ALARM -> {
                onReceiveActionDismissAlarm(context, intent)
            }
        }
    }

    abstract fun onReceiveActionSetAlarm(context: Context, intent: Intent, extras: Bundle?)

    abstract fun onReceiveActionDismissAlarm(context: Context, intent: Intent)

    abstract fun onReceiveActionSnoozeAlarm(context: Context, intent: Intent)
}