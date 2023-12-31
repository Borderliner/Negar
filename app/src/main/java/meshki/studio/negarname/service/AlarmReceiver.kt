package meshki.studio.negarname.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Timber.i(intent.toString())
            NotificationService.showSchedule(context, "Negar Alarm", "Alarm set at +${intent.getLongExtra("time", 0)}")
        } catch (err: Error) {
            Timber.e(err)
        } catch (exc: Exception) {
            Timber.w(exc)
        }
    }
}

fun setAlarm(context: Context) {
    val timeSec = System.currentTimeMillis() + 5000
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("time", timeSec)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeSec, pendingIntent)
}
