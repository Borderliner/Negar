package meshki.studio.negarname.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import timber.log.Timber

const val ACTION_ALARM_SNOOZE = "meshki.studio.negarname.ACTION_ALARM_SNOOZE"
const val ACTION_SHOW_ALARM = "meshki.studio.negarname.ACTION_SHOW_ALARM"

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        var alert: Uri? = null
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            when (intent.action) {
                ACTION_SHOW_ALARM -> {
                    val time = intent.getLongExtra("time", System.currentTimeMillis())
                    val title = intent.getStringExtra("title").orEmpty()
                    val text = intent.getStringExtra("text").orEmpty()
                    val critical = intent.getBooleanExtra("critical", false)
                    if (title.isNotEmpty() && text.isNotEmpty()) {
                        NotificationService.showAlarm(
                            context, AlarmData(
                                time,
                                title,
                                text,
                                critical
                            )
                        )
                        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
                        if (!powerManager.isInteractive) { // if screen is not already on, turn it on (get wake_lock)
                            @SuppressLint("InvalidWakeLockTag") val wl = powerManager.newWakeLock(
                                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                                "id:wakeupscreen"
                            )
                            wl.acquire(10 * 60 * 1000L /*10 minutes*/)
                        }
                        if (ringtone == null || alert == null) {
                            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                            ringtone = RingtoneManager.getRingtone(context, alert)

                            if (ringtone == null) {
                                alert =
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                ringtone = RingtoneManager.getRingtone(context, alert)
                                if (ringtone == null) {
                                    alert =
                                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                                    ringtone = RingtoneManager.getRingtone(context, alert)
                                }
                            }
                        }
                        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager =
                                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                        }
                        if (vibrator.hasVibrator() && critical) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createWaveform(
                                        longArrayOf(
                                            0,
                                            50,
                                            100,
                                            50
                                        ), intArrayOf(
                                            VibrationEffect.DEFAULT_AMPLITUDE,
                                            VibrationEffect.DEFAULT_AMPLITUDE,
                                            VibrationEffect.DEFAULT_AMPLITUDE,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        ), 0
                                    )
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(400)
                            }
                        }

                        ringtone?.play()
                    }
                }

                ACTION_ALARM_SNOOZE -> {
                    NotificationService.manager?.cancelAll()
                    if (ringtone!!.isPlaying) {
                        ringtone?.stop()
                    }
                }
            }
        } catch (err: Error) {
            Timber.wtf(err)
        } catch (exc: Exception) {
            Timber.e(exc)
        }
    }
}

data class AlarmData(
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val text: String,
    val critical: Boolean = false
)

fun setAlarm(context: Context, alarm: AlarmData) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("time", alarm.time)
        intent.putExtra("title", alarm.title)
        intent.putExtra("text", alarm.text)
        intent.putExtra("critical", alarm.critical)
        intent.setAction(ACTION_SHOW_ALARM)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (alarm.critical) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarm.time, null), pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
        }
    } catch (err: Error) {
        Timber.wtf(err)
    } catch (exc: SecurityException) {
        Timber.e(exc)
    }
}
