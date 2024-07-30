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
import androidx.core.graphics.drawable.IconCompat
import co.id.fadlurahmanfdev.kotlin_feature_alarm.data.dto.FeatureAlarmNotificationAction

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
        sound: Uri?
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
    }

    fun getFullScreenNotification(
        context: Context,
        channelId: String,
        @DrawableRes icon: Int,
        title: String,
        text: String,
        fullScreenIntent: PendingIntent,
        actions: List<FeatureAlarmNotificationAction>,
    ): Notification {
        val notification = NotificationCompat.Builder(context, channelId).apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentTitle(title)
            setContentText(text)
            setAutoCancel(false)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setSmallIcon(icon)
            setFullScreenIntent(fullScreenIntent, true)

            if (actions.isNotEmpty()) {
                val newActions = if (actions.size > 2) actions.take(2) else actions
                newActions.forEach { element ->
                    addAction(
                        NotificationCompat.Action.Builder(
                            element.icon,
                            element.textAction,
                            element.action
                        ).build()
                    )
                }
            }
        }
        return notification.build()
    }

    private fun getIconCompatFromAsset(action: FeatureAlarmNotificationAction): IconCompat {
        return IconCompat.createWithContentUri(Uri.parse("android.resource://${action.packageName}/" + action.icon))
    }

}