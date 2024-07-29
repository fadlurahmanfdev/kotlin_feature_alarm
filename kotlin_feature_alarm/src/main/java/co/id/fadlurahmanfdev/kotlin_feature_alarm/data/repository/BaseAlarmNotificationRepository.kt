package co.id.fadlurahmanfdev.kotlin_feature_alarm.data.repository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

abstract class BaseAlarmNotificationRepository {
    private lateinit var notificationManager: NotificationManager
    private fun initNotificationManager(context: Context) {
        if (!::notificationManager.isInitialized) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    fun createNotificationChannel(
        context: Context,
        id: String,
        name: String,
        description: String,
        sound: Uri
    ) {
        initNotificationManager(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    this.description = description
                    setSound(sound, null)
                }
            )
        }

        fun showNotification(
            context: Context,
            id: Int,
            channelId: String,
            @DrawableRes icon: Int,
            title: String,
            text: String,
            pendingIntent: PendingIntent,
        ) {
            initNotificationManager(context)

            val notification = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle(title)
                setContentText(text)
                setAutoCancel(false)
                setOngoing(true)
                setPriority(NotificationCompat.PRIORITY_MAX)
                setSmallIcon(icon)
                setFullScreenIntent(pendingIntent, true)
            }
            showNotification(id, notification.build())
        }
    }

    fun showNotification(id: Int, notification: Notification) {
        return notificationManager.notify(id, notification)
    }

}