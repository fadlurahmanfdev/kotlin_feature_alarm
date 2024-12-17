package com.fadlurahmanfdev.feature_alarm.data.dto

import android.app.PendingIntent
import androidx.annotation.DrawableRes

data class FeatureAlarmNotificationAction(
    @DrawableRes val icon: Int,
//    val packageName: String,
    val textAction: String,
    val action: PendingIntent,
)
