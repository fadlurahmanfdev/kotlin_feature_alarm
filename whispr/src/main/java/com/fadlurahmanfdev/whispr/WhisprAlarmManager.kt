package com.fadlurahmanfdev.whispr

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fadlurahmanfdev.whispr.constant.WhisprConfig
import com.fadlurahmanfdev.whispr.constant.WhisprKey
import com.fadlurahmanfdev.whispr.presentation.BaseWhisprFullScreenIntentActivity
import com.fadlurahmanfdev.whispr.receiver.BaseWhisprAlarmReceiver
import com.fadlurahmanfdev.whispr.service.BaseWhisprAlarmService

class WhisprAlarmManager {
    companion object {
        fun <T : BroadcastReceiver> getPendingIntentSetAlarm(
            context: Context,
            notificationId: Int,
            requestCode: Int,
            title: String,
            text: String,
            bundle: Bundle?,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_SET_ALARM
                val newBundle = bundle ?: Bundle()

                newBundle.apply {
                    putExtra(WhisprKey.PARAM_NOTIFICATION_ID, notificationId)
                    putExtra(WhisprKey.PARAM_NOTIFICATION_TITLE, title)
                    putExtra(WhisprKey.PARAM_NOTIFICATION_TEXT, text)
                }

                putExtras(newBundle)
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flags)
        }

        fun <T : BroadcastReceiver> getPendingIntentCancelAlarm(
            context: Context,
            requestCode: Int,
            flags: Int = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            clazz: Class<T>
        ): PendingIntent? {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_SET_ALARM
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flags)
        }

        fun getPendingIntentFullScreen(
            context: Context,
            requestCode: Int,
            intent: Intent,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        ): PendingIntent {
            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                flags
            )
        }

        fun <T : BaseWhisprAlarmReceiver> getPendingIntentSnoozeAlarm(
            context: Context,
            notificationId: Int,
            requestCode: Int,
            intervalInSecond: Long,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_SNOOZE_ALARM
                putExtra(WhisprKey.PARAM_NOTIFICATION_ID, notificationId)
                putExtra(WhisprKey.PARAM_INTERVAL_IN_SECOND, intervalInSecond)
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flags)
        }

        fun <T : BaseWhisprFullScreenIntentActivity> getFullScreenIntent(
            context: Context,
            clazz: Class<T>
        ): Intent {
            return Intent(context, clazz)
        }

        fun <T : BaseWhisprAlarmReceiver> getPendingIntentDismissAlarm(
            context: Context,
            notificationId: Int,
            requestCode: Int,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_DISMISS_ALARM
                putExtra(WhisprKey.PARAM_NOTIFICATION_ID, notificationId)
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flags)
        }

        fun <T : BaseWhisprAlarmService> startPlayingAlarm(
            context: Context,
            notificationId: Int,
            title: String,
            text: String,
            bundle: Bundle? = null,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_PLAY_ALARM
                putExtra(WhisprKey.PARAM_NOTIFICATION_ID, notificationId)

                val newBundle = bundle ?: Bundle()

                newBundle.apply {
                    putExtra(WhisprKey.PARAM_NOTIFICATION_TITLE, title)
                    putExtra(WhisprKey.PARAM_NOTIFICATION_TEXT, text)
                }

                putExtras(newBundle)
            }
            context.startService(intent)
        }

        fun <T : BaseWhisprAlarmService> stopPlayingAlarm(
            context: Context,
            notificationId: Int,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz).apply {
                action = WhisprConfig.ACTION_STOP_ALARM
                putExtra(WhisprKey.PARAM_NOTIFICATION_ID, notificationId)
            }
            context.startService(intent)
        }
    }
}