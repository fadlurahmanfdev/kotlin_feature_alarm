package com.fadlurahmanfdev.whispr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class WhisprDeviceRebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            onDeviceRebootCompleted(context, intent)
        }
    }

    abstract fun onDeviceRebootCompleted(context: Context?, intent: Intent)
}