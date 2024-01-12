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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import meshki.studio.negarname.MainActivity
import timber.log.Timber
import meshki.studio.negarname.R
import java.util.Calendar

const val ACTION_ALARM_SNOOZE = "meshki.studio.negarname.ACTION_ALARM_SNOOZE"
const val ACTION_ALARM_SHOW = "meshki.studio.negarname.ACTION_ALARM_SHOW"
const val ACTION_ALARM_CLICK = "meshki.studio.negarname.ACTION_ALARM_CLICK"

data class AlarmData(
    val id: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val text: String,
    val critical: Boolean = false
)

fun setAlarm(context: Context, alarm: AlarmData) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", alarm.id)
            putExtra("time", alarm.time)
            putExtra("title", alarm.title)
            putExtra("text", alarm.text)
            putExtra("critical", alarm.critical)
            action = ACTION_ALARM_SHOW
        }

        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (alarm.critical) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarm.time, null), pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
        }
    } catch (err: Error) {
        Toast.makeText(context, err.toString(), Toast.LENGTH_LONG).show()
        Timber.wtf(err)
    } catch (exc: SecurityException) {
        Toast.makeText(context, exc.toString(), Toast.LENGTH_LONG).show()
        Timber.e(exc)
    }
}


class AlarmReceiver : BroadcastReceiver() {
    companion object {
        var alert: Uri? = null
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
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

            when (intent.action) {
                ACTION_ALARM_SHOW -> {
                    Timber.tag("Alarms").i("SHOULD DO SOMETHING NOW")
                    val id = intent.getIntExtra("id", 0)
                    val time = intent.getLongExtra("time", System.currentTimeMillis())
                    val title = intent.getStringExtra("title").orEmpty()
                    val text = intent.getStringExtra("text").orEmpty()
                    val critical = intent.getBooleanExtra("critical", false)
                    if (title.isNotEmpty() && text.isNotEmpty()) {
                        val activityPendingIntent = PendingIntent.getActivity(
                            context, 0, Intent(
                                context,
                                MainActivity::class.java
                            ).apply {
                                    putExtra("id", id)
                                    action = ACTION_ALARM_CLICK
                            },
                            PendingIntent.FLAG_MUTABLE
                        )

                        val snoozePendingIntent = PendingIntent.getBroadcast(
                            context, 0, Intent(
                                context,
                                AlarmReceiver::class.java
                            ).apply {
                                putExtra("id", id)
                                action = ACTION_ALARM_SNOOZE
                                data = Uri.parse("negarname://notifications/$id")
                            },
                            PendingIntent.FLAG_MUTABLE
                        )

                        val cal = Calendar.getInstance().apply {
                            timeInMillis = time
                        }

                        val notificationData = NotificationData(
                            id = id,
                            title = "$title (${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)})",
                            text = text,
                            channel = NotificationChannelData("ALARMS_CHANNEL", "Alarms"),
                            critical = critical,
                            sticky = true,
                            icon = R.drawable.alarm,
                            color = null,
                            category = NotificationCompat.CATEGORY_ALARM,
                            onClickAction = NotificationAction(
                                iconResource = R.drawable.alarm,
                                textResource = 0,
                                activityPendingIntent
                            ),
                            actions = listOf(
                                NotificationAction(
                                    iconResource = R.drawable.timer_off,
                                    textResource = R.string.snooze,
                                    snoozePendingIntent
                                )
                            ),
                            sound = alert
                        )
                        NotificationService.showNotification(context, notificationData)

                        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
                        if (!powerManager.isInteractive) { // if screen is not already on, turn it on (get wake_lock)
                            @SuppressLint("InvalidWakeLockTag") val wl = powerManager.newWakeLock(
                                PowerManager.ACQUIRE_CAUSES_WAKEUP
                                        or PowerManager.ON_AFTER_RELEASE
                                        or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                                "id:wakeupscreen"
                            )
                            wl.acquire(1 * 30 * 1000L /* 30 seconds */)
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
                                            1000,
                                        ), intArrayOf(
                                            VibrationEffect.DEFAULT_AMPLITUDE,
                                            VibrationEffect.DEFAULT_AMPLITUDE,
                                        ), -1
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
                    val id = intent.getIntExtra("id", 0)
                    NotificationService.stopNotification(context, id)

                    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager =
                            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        vibratorManager.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                    }
                    if (vibrator.hasVibrator()) {
                        vibrator.cancel()
                    }

                    if (ringtone!!.isPlaying) {
                        ringtone?.stop()
                    }
                }
                else -> Unit
            }
        } catch (err: Error) {
            Toast.makeText(context, err.toString(), Toast.LENGTH_LONG).show()
            Timber.wtf(err)
        } catch (exc: Exception) {
            Toast.makeText(context, exc.toString(), Toast.LENGTH_LONG).show()
            Timber.e(exc)
        }
    }
}
