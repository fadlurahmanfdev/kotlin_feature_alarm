package co.id.fadlurahmanfdev.kotlin_feature_alarm.domain.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar
import java.util.Date

abstract class FeatureAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SET_ALARM = "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_SET_ALARM"
        const val ACTION_DISMISS_ALARM =
            "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_DISMISS_ALARM"
        const val ACTION_SNOOZE_ALARM =
            "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_SNOOZE_ALARM"
        const val PARAM_YEAR = "PARAM_YEAR"
        const val PARAM_MONTH = "PARAM_MONTH"
        const val PARAM_DAY = "PARAM_DAY"
        const val PARAM_HOUR = "PARAM_HOUR"
        const val PARAM_MINUTE = "PARAM_MINUTE"
        const val PARAM_SECOND = "PARAM_SECOND"

        fun <T : FeatureAlarmReceiver> getPendingIntentSetAlarm(
            context: Context,
            requestCode: Int,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = ACTION_SET_ALARM
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
        }

        fun <T : FeatureAlarmReceiver> getPendingIntentDismissAlarm(
            context: Context,
            requestCode: Int,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = ACTION_DISMISS_ALARM
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
        }

        fun <T : FeatureAlarmReceiver> getPendingIntentSnoozeAlarm(
            context: Context,
            requestCode: Int,
            date: Date,
            clazz: Class<T>
        ): PendingIntent {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val intent = Intent(context, clazz).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra(PARAM_YEAR, calendar.get(Calendar.YEAR))
                putExtra(PARAM_MONTH, calendar.get(Calendar.MONTH))
                putExtra(PARAM_DAY, calendar.get(Calendar.DAY_OF_MONTH))
                putExtra(PARAM_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                putExtra(PARAM_MINUTE, calendar.get(Calendar.MINUTE))
                putExtra(PARAM_SECOND, calendar.get(Calendar.SECOND))
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, getFlagPendingIntent())
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
            ACTION_SET_ALARM -> {
                onReceiveActionSetAlarm(context, intent)
            }

            ACTION_SNOOZE_ALARM -> {
                val year = intent.getIntExtra(PARAM_YEAR, -1)
                val month = intent.getIntExtra(PARAM_MONTH, -1)
                val day = intent.getIntExtra(PARAM_DAY, -1)
                val hour = intent.getIntExtra(PARAM_HOUR, -1)
                val minute = intent.getIntExtra(PARAM_MINUTE, -1)
                val second = intent.getIntExtra(PARAM_SECOND, -1)
                val snoozeTime = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, second)
                }.time
                onReceiveActionSnoozeAlarm(context, intent, snoozeTime)
            }

            ACTION_DISMISS_ALARM -> {
                onReceiveActionDismissAlarm(context, intent)
            }
        }
    }

    abstract fun onReceiveActionSetAlarm(context: Context, intent: Intent)

    abstract fun onReceiveActionDismissAlarm(context: Context, intent: Intent)

    abstract fun onReceiveActionSnoozeAlarm(context: Context, intent: Intent, snoozeTime: Date)
}