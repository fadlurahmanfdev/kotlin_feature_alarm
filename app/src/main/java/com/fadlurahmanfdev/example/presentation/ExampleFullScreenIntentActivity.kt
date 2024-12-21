package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.core.service.AlarmReceiver
import com.fadlurahmanfdev.feature_alarm.core.service.FeatureAlarmReceiver
import com.fadlurahmanfdev.feature_alarm.presentation.BaseFullScreenIntentActivity
import com.ncorti.slidetoact.SlideToActView

class ExampleFullScreenIntentActivity : BaseFullScreenIntentActivity() {
    lateinit var slider: SlideToActView
    lateinit var tvTime: TextView
    lateinit var tvText: TextView

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


        val time = intent.extras?.getString("PARAM_TIME") ?: "-"
        val text = intent.extras?.getString("PARAM_TEXT") ?: "-"

        tvTime.text = time
        tvText.text = text

        slider.onSlideCompleteListener  = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                FeatureAlarmReceiver.sendBroadcastSendAlarm(
                    this@ExampleFullScreenIntentActivity,
                    AlarmReceiver::class.java
                )
                finish()
            }
        }
    }

    override fun onDestroyBaseFullScreenIntentActivity() {

    }
}