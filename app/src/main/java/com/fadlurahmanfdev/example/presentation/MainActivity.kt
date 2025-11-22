package com.fadlurahmanfdev.example.presentation

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.constant.AppConfig
import com.fadlurahmanfdev.example.data.dto.FeatureModel
import com.fadlurahmanfdev.example.notification.AppNotification
import com.fadlurahmanfdev.example.presentation.adapter.ListExampleAdapter
import com.fadlurahmanfdev.example.receiver.AppAlarmReceiver
import com.fadlurahmanfdev.whispr.Whispr
import com.fadlurahmanfdev.whispr.WhisprAlarmManager
import java.util.Calendar

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {

    private lateinit var alarmManager: AlarmManager
    private lateinit var whispr: Whispr
    private lateinit var appNotification: AppNotification
    private lateinit var dateTimePickerBottomsheet: DateTimePickerBottomsheet
    private lateinit var dateTimePickerEnum: String

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Schedule Alarm RTC",
            desc = "Schedule Alarm using Real Time Clock",
            enum = "SCHEDULE_ALARM_RTC"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Cancel Alarm RTC",
            desc = "Cancel Alarm RTC",
            enum = "CANCEL_ALARM_RTC"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Schedule Alarm Elapsed",
            desc = "Schedule Alarm using Elapsed (x seconds from now / x hours from now)",
            enum = "SCHEDULE_ALARM_ELAPSED"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Cancel Alarm Elapsed",
            desc = "Cancel Alarm Elapsed",
            enum = "CANCEL_ALARM_ELAPSED"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Full Screen Notification",
            desc = "Example Full Screen Notification",
            enum = "SHOW_FULL_SCREEN_NOTIFICATION"
        ),
    )

    private lateinit var rv: RecyclerView

    private lateinit var adapter: ListExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rv = findViewById<RecyclerView>(R.id.rv)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter

        whispr = Whispr(this)
        appNotification = AppNotification(this)

        dateTimePickerBottomsheet = DateTimePickerBottomsheet(this)
        dateTimePickerBottomsheet.initCallback(object : DateTimePickerBottomsheet.Callback {
            override fun onConfirm(
                dayOfMonth: Int,
                month: Int,
                year: Int,
                hour: Int,
                minute: Int
            ) {
                Log.d(
                    this@MainActivity::class.java.simpleName,
                    "App-Whispr-LOG %%% - successfully pick a date time: $year-$month-$dayOfMonth, $hour:$minute"
                )
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                when (dateTimePickerEnum) {
                    "SCHEDULE_ALARM_RTC" -> {
                        whispr.setExact(
                            type = AlarmManager.RTC_WAKEUP,
                            triggerAtMillis = calendar.timeInMillis,
                            pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
                                context = this@MainActivity,
                                notificationId = AppConfig.ALARM_NOTIFICATION_ID_FOR_RTC,
                                requestCode = AppConfig.ALARM_RTC_PENDING_INTENT_REQUEST_CODE,
                                bundle = null,
                                title = "Example Alarm at $hour:$minute",
                                text = "Example Alarm at $hour:$minute",
                                clazz = AppAlarmReceiver::class.java,
                            )
                        )
                    }
                }
            }

            override fun onCancel() {

            }

        })
    }

    override fun onClicked(item: FeatureModel) {
        dateTimePickerEnum = item.enum
        when (item.enum) {
            "SCHEDULE_ALARM_RTC" -> {
                dateTimePickerBottomsheet.show()
            }

            "CANCEL_ALARM_RTC" -> {
                val pi = WhisprAlarmManager.getPendingIntentCancelAlarm(
                    context = this@MainActivity,
                    requestCode = AppConfig.ALARM_RTC_PENDING_INTENT_REQUEST_CODE,
                    clazz = AppAlarmReceiver::class.java,
                )
                Log.d(
                    this::class.java.simpleName,
                    "App-Whispr-LOG %%% - pending intent exist: ${pi != null}"
                )
                if (pi != null) {
                    whispr.cancel(
                        pendingIntent = pi
                    )
                }
            }

            "SCHEDULE_ALARM_ELAPSED" -> {
                whispr.setExact(
                    type = AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis = SystemClock.elapsedRealtime() + 20000L,
                    pendingIntent = WhisprAlarmManager.getPendingIntentSetAlarm(
                        context = this@MainActivity,
                        notificationId = AppConfig.ALARM_NOTIFICATION_ID_FOR_ELAPSED,
                        requestCode = AppConfig.ALARM_ELAPSED_PENDING_INTENT_REQUEST_CODE,
                        bundle = null,
                        title = "Title Elapsed Alarm",
                        text = "Text Elapsed Alarm",
                        clazz = AppAlarmReceiver::class.java,
                    )
                )
            }

            "CANCEL_ALARM_ELAPSED" -> {
                val pi = WhisprAlarmManager.getPendingIntentCancelAlarm(
                    context = this@MainActivity,
                    requestCode = AppConfig.ALARM_ELAPSED_PENDING_INTENT_REQUEST_CODE,
                    clazz = AppAlarmReceiver::class.java,
                )
                Log.d(
                    this::class.java.simpleName,
                    "App-Whispr-LOG %%% - pending intent exist: ${pi != null}"
                )
                if (pi != null) {
                    whispr.cancel(
                        pendingIntent = pi
                    )
                }
            }

            "SHOW_FULL_SCREEN_NOTIFICATION" -> {
                appNotification.createNotificationChannel(
                    id = "ALARM_NOTIFICATION",
                    name = "Alarm Notification",
                    description = "Alarm for notification",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                    sound = Uri.parse(
                        "android.resource://${packageName}/raw/${
                            resources.getResourceEntryName(
                                R.raw.adhan
                            )
                        }"
                    )
                )

                val notificationBuilder = appNotification.getFullScreenNotificationBuilder(
                    channelId = "ALARM_NOTIFICATION",
                    icon = R.drawable.baseline_alarm_24,
                    title = "Alarm Title",
                    text = "Alarm Text",
                    fullScreenIntent = WhisprAlarmManager.getPendingIntentFullScreen(
                        this,
                        1001,
                        Intent(this, ExampleFullScreenIntentActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )

                appNotification.notificationManager.notify(1, notificationBuilder.build())
            }
        }
    }
}