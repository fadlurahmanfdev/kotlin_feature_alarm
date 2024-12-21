package com.fadlurahmanfdev.example.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class DeviceRebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            onDeviceRebootCompleted(context, intent)
        }
    }

    abstract fun onDeviceRebootCompleted(context: Context?, intent: Intent)
}