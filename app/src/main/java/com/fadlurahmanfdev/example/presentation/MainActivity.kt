package com.fadlurahmanfdev.example.presentation

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.core.service.AlarmReceiver
import com.fadlurahmanfdev.example.data.dto.FeatureModel
import com.fadlurahmanfdev.example.presentation.adapter.ListExampleAdapter
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmReceiver
import java.util.Calendar

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {

    private lateinit var alarmManager:AlarmManager

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Info Next Alarm",
            desc = "Get Info Next Alarm",
            enum = "INFO_NEXT_ALARM"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Full Screen Alarm",
            desc = "Full Screen Alarm",
            enum = "FULL_SCREEN_ALARM"
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
    }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "INFO_NEXT_ALARM" -> {
//                val time = Calendar.getInstance().apply {
//                    timeInMillis =  alarmManager.nextAlarmClock.triggerTime
//                }
                Log.d(this::class.java.simpleName, "next alarm will be trigger at ${alarmManager.nextAlarmClock.triggerTime}")
            }

            "FULL_SCREEN_ALARM" -> {
                val triggerTime = Calendar.getInstance().apply {
                    add(Calendar.SECOND, 10)
                }
                val setAlarmPendingIntent = FeatureAlarmReceiver.getPendingIntentSetAlarm(
                    context = this,
                    requestCode = 0,
                    bundle = Bundle(),
                    clazz = AlarmReceiver::class.java
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime.timeInMillis,
                        setAlarmPendingIntent
                    )
                }
            }
        }
    }
}