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
import meshki.studio.negarname.entity.Week
import java.util.Calendar

const val ACTION_ALARM_SNOOZE = "meshki.studio.negarname.ACTION_ALARM_SNOOZE"
const val ACTION_ALARM_SHOW = "meshki.studio.negarname.ACTION_ALARM_SHOW"
const val ACTION_ALARM_CLICK = "meshki.studio.negarname.ACTION_ALARM_CLICK"
const val ACTION_ALARM_REMOVE = "meshki.studio.negarname.ACTION_ALARM_REMOVE"

data class AlarmData(
    val id: Long = 0,
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val text: String,
    val repeating: Boolean = false,
    val week: Week = Week(),
    val critical: Boolean = false
)

fun setAlarm(context: Context, alarm: AlarmData): Boolean {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", alarm.id)
            putExtra("time", alarm.time)
            putExtra("title", alarm.title)
            putExtra("text", alarm.text)
            putExtra("critical", alarm.critical)
            putExtra("repeating", alarm.repeating)
            action = ACTION_ALARM_SHOW
        }

        val pendingIntent =
            PendingIntent.getBroadcast(context, alarm.id.toInt(), intent, PendingIntent.FLAG_MUTABLE)
        if (alarm.critical) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarm.time, null), pendingIntent)
        } else {
            if (alarm.repeating) {
                val weekInMillis = 7 * 24 * 60 * 60 * 1000L /* 7 Days */
                alarm.week.list.forEachIndexed { idx, day ->
                    if (day.value) {
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = alarm.time
                        }
                        cal.set(Calendar.DAY_OF_WEEK, idx + 1)
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, weekInMillis , pendingIntent)
                    }
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
            }
        }
        return true
    } catch (err: Error) {
        Toast.makeText(context, err.toString(), Toast.LENGTH_LONG).show()
        Timber.wtf(err)
        return false
    } catch (exc: SecurityException) {
        Toast.makeText(context, exc.toString(), Toast.LENGTH_LONG).show()
        Timber.e(exc)
        return false
    }
}

fun deleteAlarm(context: Context, id: Int) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, id, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_MUTABLE)
        alarmManager.cancel(pendingIntent)
        AlarmReceiver.stopAlarm(context, id)
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

        fun stopAlarm(context: Context, id: Int) {
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

            if (ringtone?.isPlaying == true) {
                ringtone?.stop()
            }
        }
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
                    val id = intent.getLongExtra("id", 0)
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
                    stopAlarm(context, id)
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
