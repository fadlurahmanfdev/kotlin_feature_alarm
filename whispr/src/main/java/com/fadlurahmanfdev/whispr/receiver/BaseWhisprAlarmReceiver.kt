package com.fadlurahmanfdev.whispr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fadlurahmanfdev.whispr.Whispr
import com.fadlurahmanfdev.whispr.constant.WhisprConfig
import com.fadlurahmanfdev.whispr.constant.WhisprKey

abstract class BaseWhisprAlarmReceiver : BroadcastReceiver() {
    lateinit var whispr: Whispr

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (!this::whispr.isInitialized) {
            whispr = Whispr(context)
        }

        when (intent?.action) {
            WhisprConfig.ACTION_SET_ALARM -> {
                val notificationId = intent.getIntExtra(WhisprKey.PARAM_NOTIFICATION_ID, -1)
                val notificationTitle = intent.getStringExtra(WhisprKey.PARAM_NOTIFICATION_TITLE)
                val notificationText = intent.getStringExtra(WhisprKey.PARAM_NOTIFICATION_TEXT)
                if (notificationId < 0) {
                    Log.i(
                        this::class.java.simpleName,
                        "Whispr-LOG %%% - notification id: $notificationId is not valid"
                    )
                    return
                }
                onReceivedAlarm(
                    context,
                    intent,
                    notificationId,
                    notificationTitle,
                    notificationText,
                    intent.extras
                )
            }

            WhisprConfig.ACTION_SNOOZE_ALARM -> {
                val notificationId = intent.getIntExtra(WhisprKey.PARAM_NOTIFICATION_ID, -1)
                val intervalInSecond =
                    intent.getIntExtra(WhisprKey.PARAM_INTERVAL_IN_SECOND, 0)
                if (notificationId < 0) {
                    Log.i(
                        this::class.java.simpleName,
                        "Whispr-LOG %%% - notification id: $notificationId is not valid"
                    )
                    return
                }

                onReceiveActionSnoozeAlarm(context, intent, notificationId, intervalInSecond)
            }

            WhisprConfig.ACTION_DISMISS_ALARM -> {
                val notificationId = intent.getIntExtra(WhisprKey.PARAM_NOTIFICATION_ID, -1)
                if (notificationId < 0) {
                    Log.i(
                        this::class.java.simpleName,
                        "Whispr-LOG %%% - notification id: $notificationId is not valid"
                    )
                    return

                }

                onReceiveActionDismissAlarm(context, intent, notificationId)
            }

            else -> {
                onReceivedOtherActions(context, intent)
            }
        }
    }

    abstract fun onReceivedAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        title: String?,
        text: String?,
        extras: Bundle?
    )

    open fun onReceiveActionDismissAlarm(context: Context, intent: Intent, notificationId: Int) {}

    open fun onReceiveActionSnoozeAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        intervalInSecond: Int
    ) {
    }

    open fun onReceivedOtherActions(context: Context, intent: Intent?) {}
}