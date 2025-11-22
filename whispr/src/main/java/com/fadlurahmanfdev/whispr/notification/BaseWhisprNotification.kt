package com.fadlurahmanfdev.whispr.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat

abstract class BaseWhisprNotification(private val context: Context) {
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getUriSoundFromResources(
        @RawRes resourceFile: Int,
    ): Uri {
        return Uri.parse(
            "android.resource://${context.packageName}/raw/${
                context.resources.getResourceEntryName(
                    resourceFile
                )
            }"
        )
    }

    fun getIconCompatFromResources(
        @DrawableRes resourceFile: Int,
    ): IconCompat {
        return IconCompat.createWithContentUri(Uri.parse("android.resource://${context.packageName}/" + resourceFile))
    }

    /**
     * @param id The id of the channel. Must be unique per package. The value may be truncated if it is too long.
     * @param name The user visible name of the channel. You can rename this channel when the system locale changes by listening for the Intent. ACTION_LOCALE_CHANGED broadcast. The recommended maximum length is 40 characters; the value may be truncated if it is too long.
     * @param importance The importance of the channel. This controls how interruptive notifications posted to this channel are. e.g., [NotificationManager.IMPORTANCE_HIGH]
     * @param sound Sets the sound that should be played for notifications posted to this channel and its audio attributes. Notification channels with an importance of at least NotificationManager. IMPORTANCE_DEFAULT should have a sound. Only modifiable before the channel is submitted to NotificationManager. createNotificationChannel(NotificationChannel).
     * */
    open fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        importance: Int,
        sound: Uri?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val isChannelExist = notificationManager.notificationChannels.firstOrNull { channel ->
                channel.id == id
            } != null
            if (isChannelExist) {
                Log.i(this::class.java.simpleName, "notification channel with id $id already exist")
                return
            }

            Log.i(this::class.java.simpleName, "creating notification channel with id $id")

            val channel = NotificationChannel(id, name, importance).apply {
                this.description = description
                setSound(sound, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    open fun getFullScreenNotificationBuilder(
        channelId: String,
        @DrawableRes icon: Int,
        title: String,
        text: String,
        fullScreenIntent: PendingIntent? = null,
        deletePendingIntent: PendingIntent? = null,
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId).apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentTitle(title)
            setContentText(text)
            setOngoing(true)
            setAutoCancel(false)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setSmallIcon(icon).setCategory(Notification.CATEGORY_ALARM)

            if (fullScreenIntent != null) {
                setFullScreenIntent(fullScreenIntent, true)
            }

            if (deletePendingIntent != null) {
                setDeleteIntent(deletePendingIntent)
            }

        }
        return builder
    }

    fun notify(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}