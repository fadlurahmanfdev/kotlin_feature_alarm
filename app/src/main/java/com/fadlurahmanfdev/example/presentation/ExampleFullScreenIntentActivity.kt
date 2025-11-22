package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.receiver.AppAlarmReceiver
import com.fadlurahmanfdev.example.service.AppAlarmService
import com.fadlurahmanfdev.whispr.WhisprAlarmManager
import com.fadlurahmanfdev.whispr.receiver.BaseWhisprAlarmReceiver
import com.fadlurahmanfdev.whispr.presentation.BaseWhisprFullScreenIntentActivity
import com.ncorti.slidetoact.SlideToActView

class ExampleFullScreenIntentActivity : BaseWhisprFullScreenIntentActivity() {
    lateinit var slider: SlideToActView
    lateinit var tvTime: TextView
    lateinit var tvText: TextView

    var notificationId:Int?=null

    override fun onCreateBaseFullScreenIntentActivity(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setContentView(R.layout.activity_example_full_screen_intent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        slider = findViewById(R.id.alarm_slider)
        tvTime = findViewById(R.id.tv_alarm_time)
        tvText = findViewById(R.id.tv_alarm_text)

        val time = intent.extras?.getString("PARAM_TITLE") ?: "-"
        val text = intent.extras?.getString("PARAM_TEXT") ?: "-"
        notificationId = intent.extras?.getInt("PARAM_NOTIFICATION_ID")

        tvTime.text = time
        tvText.text = text

        slider.onSlideCompleteListener  = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                WhisprAlarmManager.stopPlayingAlarm(
                    this@ExampleFullScreenIntentActivity,
                    notificationId = notificationId ?: -1,
                    AppAlarmService::class.java
                )
                finish()
            }
        }
    }

    override fun onDestroyBaseFullScreenIntentActivity() {

    }
}