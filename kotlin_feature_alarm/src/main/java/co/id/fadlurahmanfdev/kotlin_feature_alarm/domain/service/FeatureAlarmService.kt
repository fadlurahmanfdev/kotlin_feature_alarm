package co.id.fadlurahmanfdev.kotlin_feature_alarm.domain.service

import android.app.Notification
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
import androidx.core.content.ContextCompat

abstract class FeatureAlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator

    companion object {
        const val ACTION_PLAY_ALARM = "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_PLAY_ALARM"
        const val ACTION_STOP_ALARM = "co.id.fadlurahmanfdev.kotlin_feature_alarm.ACTION_STOP_ALARM"
        const val PARAM_NOTIFICATION_ID = "PARAM_NOTIFICATION_ID"

        fun <T : FeatureAlarmService> startPlayingAlarm(
            context: Context,
            notificationId: Int,
            bundle: Bundle? = null,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz).apply {
                action = ACTION_PLAY_ALARM
                putExtra(PARAM_NOTIFICATION_ID, notificationId)
                if (bundle != null) {
                    putExtras(bundle)
                }
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun <T : FeatureAlarmService> stopPlayingAlarm(
            context: Context,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz).apply {
                action = ACTION_STOP_ALARM
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

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
        onFeatureAlarmServiceCreate()
    }

    abstract fun onFeatureAlarmServiceCreate()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_ALARM -> {
                val notificationId = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1)
                val bundle = intent.extras
                if (notificationId != -1) {
                    onReceiveActionPlayAlarm(notificationId, bundle)
                    onStartForeground(notificationId, bundle)
                }
            }

            ACTION_STOP_ALARM -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    open fun onReceiveActionPlayAlarm(notificationId: Int, bundle: Bundle?) {}

    private fun onStartForeground(notificationId: Int, bundle: Bundle?) {
        startForeground(notificationId, onAlarmNotification(applicationContext, bundle))
        onStartPlayingAlarmPlayer(bundle)
    }

    private fun stopForegroundService() {
        stopRinging()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    abstract fun onAlarmNotification(context: Context, bundle: Bundle?): Notification

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