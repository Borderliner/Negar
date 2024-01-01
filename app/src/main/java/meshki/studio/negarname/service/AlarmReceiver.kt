package meshki.studio.negarname.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.os.PowerManager
import timber.log.Timber


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val time = intent.getLongExtra("time", System.currentTimeMillis())
            val title = intent.getStringExtra("title").orEmpty()
            val text = intent.getStringExtra("text").orEmpty()
            if (title.isNotEmpty() && text.isNotEmpty()) {
                NotificationService.showSchedule(context, title, text)
                val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
                if (!powerManager.isInteractive) { // if screen is not already on, turn it on (get wake_lock)
                    @SuppressLint("InvalidWakeLockTag") val wl = powerManager.newWakeLock(
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                        "id:wakeupscreen"
                    )
                    wl.acquire(10*60*1000L /*10 minutes*/)
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
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
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
