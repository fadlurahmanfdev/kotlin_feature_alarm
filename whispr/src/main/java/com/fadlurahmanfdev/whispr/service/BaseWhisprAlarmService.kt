package com.fadlurahmanfdev.whispr.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.fadlurahmanfdev.whispr.constant.WhisprConfig
import com.fadlurahmanfdev.whispr.constant.WhisprKey

abstract class BaseWhisprAlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        onWhisprAlarmServiceCreated()
    }

    abstract fun onWhisprAlarmServiceCreated()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            WhisprConfig.ACTION_PLAY_ALARM -> {
                val notificationId = intent.getIntExtra(WhisprKey.PARAM_NOTIFICATION_ID, -1)
                val bundle = intent.extras
                val notificationTitle = bundle?.getString(WhisprKey.PARAM_NOTIFICATION_TITLE)
                val notificationText = bundle?.getString(WhisprKey.PARAM_NOTIFICATION_TEXT)
                if (notificationId < 0) {
                    Log.w(
                        this::class.java.simpleName,
                        "Whispr-LOG %%% - notification id: $notificationId, is not valid"
                    )
                } else {
                    onReceiveActionPlayAlarm(
                        notificationId,
                        notificationTitle,
                        notificationText,
                        bundle
                    )
                    onStartPlayingAlarmPlayer(bundle)
                }


            }

            WhisprConfig.ACTION_STOP_ALARM -> {
                val notificationId = intent.getIntExtra(WhisprKey.PARAM_NOTIFICATION_ID, -1)

                if (notificationId < 0) {
                    Log.w(
                        this::class.java.simpleName,
                        "Whispr-LOG %%% - notification id: $notificationId, is not valid"
                    )
                } else {
                    onReceiveActionStopAlarm(notificationId)
                }

                stopService()
            }
        }
        return START_STICKY
    }

    abstract fun onReceiveActionPlayAlarm(
        notificationId: Int,
        title: String?,
        text: String?,
        bundle: Bundle?
    )

    abstract fun onReceiveActionStopAlarm(notificationId: Int)

    private fun stopService() {
        stopRinging()
        stopSelf()
    }

    private fun onStartPlayingAlarmPlayer(bundle: Bundle?) {
        stopRinging()
        startRinging(getRingtone(bundle))
    }

    open fun getRingtone(bundle: Bundle?): Uri = defaultUriRingtone

    private fun stopRinging() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        cancelVibrator()
    }

    open var defaultUriRingtone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private fun startRinging(ringtone: Uri) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(applicationContext, ringtone)

        // set volume based on ringtone volume
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        )

        mediaPlayer?.prepare()
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        playVibrateEffect()
    }

    private fun playVibrateEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(2000L, 2000L, 2000L), 0))
        } else {
            vibrator.vibrate(longArrayOf(2000L, 2000L, 2000L), 0)
        }
    }

    private fun cancelVibrator() {
        vibrator.cancel()
    }
}