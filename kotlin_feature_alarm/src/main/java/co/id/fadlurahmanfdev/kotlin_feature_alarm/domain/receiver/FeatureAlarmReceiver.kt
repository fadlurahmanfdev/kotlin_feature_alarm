package co.id.fadlurahmanfdev.kotlin_feature_alarm.domain.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

abstract class FeatureAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SET_ALARM = "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_SET_ALARM"

        fun <T : FeatureAlarmReceiver> getPendingIntent(
            context: Context,
            requestCode: Int,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = ACTION_SET_ALARM
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
                onReceiveAlarm(context, intent)
            }
        }
    }

    abstract fun onReceiveAlarm(context: Context, intent: Intent)
}