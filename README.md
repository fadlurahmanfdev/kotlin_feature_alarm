# Overview

Library to simplify alarm operation or whispering your android application from silent.

## Installation

```kotlin
implementation("com.fadlurahmanfdev.whispr:x.y.z")
```

## Initialization

```kotlin
val whispr = Whispr(context)
```

## Key Features

### Set Alarm

#### Creating Broadcaster Receiver

Creating a Custom Broadcast Receiver to handling received alarm.

```kotlin
class AppAlarmReceiver : BaseWhisprAlarmReceiver() {
    override fun onReceivedAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        title: String?,
        text: String?,
        extras: Bundle?
    ) {
        // triggered once alarm received
    }

    override fun onReceiveActionDismissAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int
    ) {
        // [OPTIONAL] triggered once dismiss action received, from  WhisprAlarmManager.getPendingIntentDismissAlarm(..)
    }

    override fun onReceiveActionSnoozeAlarm(
        context: Context,
        intent: Intent,
        notificationId: Int,
        intervalInSecond: Int
    ) {
        // triggered once snooze action received, from WhisprAlarmManager.getPendingIntentSnoozeAlarm(..)
    }

    override fun onReceivedOtherActions(context: Context, intent: Intent?) {
        // [OPTIONAL] triggered once other action received
    }
}
```

#### Creating Service

[Optional] Creating Custom Service to handling notification sound playing in background

```kotlin
class AppAlarmService : BaseWhisprAlarmService() {
    lateinit var appNotification: AppNotification

    override var defaultUriRingtone: Uri =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    override fun onWhisprAlarmServiceCreated() {
        // trigger once alarm service created
    }

    override fun onReceiveActionPlayAlarm(
        notificationId: Int,
        title: String?,
        text: String?,
        bundle: Bundle?
    ) {
        // triggered from WhisprAlarmManager.startPlayingAlarm(..)
    }

    override fun onReceiveActionStopAlarm(notificationId: Int) {
        // triggered from WhisprAlarmManager.stopPlayingAlarm(..)
    }
}
```

#### Setup Manifest

Set Manifest for permission, Service, Broadcast Receiver.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application .. >
        <service
            android:name=".service.AppAlarmService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.fadlurahmanfdev.whispr.ACTION_PLAY_ALARM" />
                <action android:name="com.fadlurahmanfdev.whispr.ACTION_STOP_ALARM" />
            </intent-filter>
        </service>

        <receiver
            android:name=".receiver.AppAlarmReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.fadlurahmanfdev.whispr.ACTION_SET_ALARM" />
                <action android:name="com.fadlurahmanfdev.whispr.ACTION_DISMISS_ALARM" />
                <action android:name="com.fadlurahmanfdev.whispr.ACTION_SNOOZE_ALARM" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".service.AppDeviceRebootReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

#### Real Time Clock Alarm

Using Real Time Clock, it means scheduling alarm using a specific date time, (e.g., schedule alarm
at 22:00)

```kotlin
// pick time from calendar
val calendar = Calendar.getInstance()

whispr.setExact(
    type = AlarmManager.RTC_WAKEUP,
    triggerAtMillis = calendar.timeInMillis,
    pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
        context = this@MainActivity,
        notificationId = 0,
        requestCode = 1000,
        bundle = null,
        title = "RTC Alarm",
        text = "RTC Alarm",
        clazz = AppAlarmReceiver::class.java,
    )
)
```

#### Elapsed Alarm

Using Elapsed time, it means scheduling alarm using a specific time from now, (e.g., schedule alarm
5 seconds from now, schedule alarm 30 minutes from now)

```kotlin
// pick time from calendar
val calendar = Calendar.getInstance()

whispr.setExact(
    type = AlarmManager.ELAPSED_REALTIME_WAKEUP,
    triggerAtMillis = SystemClock.elapsedRealtime() + 20000L,
    pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
        context = this@MainActivity,
        notificationId = 1,
        requestCode = 1001,
        bundle = null,
        title = "Elapsed Alarm",
        text = "Elapsed Alarm",
        clazz = AppAlarmReceiver::class.java,
    )
)
```

### Cancel Alarm

Cancel existing alarm, check if the pendingIntent is exist before canceling alarm

```kotlin
val pendingIntent = WhisprAlarmManager.getPendingIntentCancelAlarm(
    context = this@MainActivity,
    requestCode = AppConfig.ALARM_RTC_PENDING_INTENT_REQUEST_CODE,
    clazz = AppAlarmReceiver::class.java,
)
if (pendingIntent != null) {
    whispr.cancel(pendingIntent = pendingIntent)
}
```

### Set Alarm After Broadcast Receiver

```kotlin
class AppDeviceRebootReceiver : WhisprDeviceRebootReceiver() {
    private var whispr: Whispr? = null

    override fun onDeviceRebootCompleted(context: Context?, intent: Intent) {
        Log.d(this::class.java.simpleName, "App-Whispr-LOG %%% - device reboot completed triggered")
        if (context == null) return
        if (whispr == null) {
            whispr = Whispr(context)
        }

        // check if there is alarm schedule is active
        
        // restart the alarm
        whispr!!.setExact(
            type = AlarmManager.RTC_WAKEUP,
            triggerAtMillis = triggerAtMillis,
            pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
                context = context,
                notificationId = 0,
                requestCode = 1000,
                bundle = null,
                title = "RTC Alarm",
                text = "RTC Alarm",
                clazz = AppAlarmReceiver::class.java,
            )
        )
    }
}
```
