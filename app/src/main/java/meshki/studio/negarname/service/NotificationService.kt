package meshki.studio.negarname.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.theme.PastelLime
import meshki.studio.negarname.ui.theme.PastelPink

class NotificationService {
    companion object {
        var manager: NotificationManager? = null

        private fun showNotification(context: Context, title: String, text: String, critical: Boolean, channelId: String, channelName: String) {
            if (manager == null) {
                manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = if (critical) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(channelId, channelName, importance)
                    manager?.createNotificationChannel(channel)
                }
            }
            val snoozeIntent = Intent(
                context,
                AlarmReceiver::class.java
            )
            snoozeIntent.setAction(ACTION_ALARM_SNOOZE)
            val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent,
                PendingIntent.FLAG_IMMUTABLE)

            val priority = if (critical) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
            val builder = NotificationCompat.Builder(context, channelId)
                .setPriority(priority)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(title)
                .setSmallIcon(R.drawable.alarm)
                .addAction(R.drawable.timer_off, context.resources.getString(R.string.snooze), snoozePendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setColor(PastelPink.toArgb())
                .setContentIntent(snoozePendingIntent!!)
                .setWhen(System.currentTimeMillis())
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setLights(PastelLime.toArgb(), 700, 500)
            val notification = builder.build()
            notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
            manager?.notify(1, notification)
        }
        fun showAlarm(context: Context, alarmData: AlarmData) {
            showNotification(context, alarmData.title, alarmData.text, alarmData.critical, "alarms_channel", "Alarms Notifications")
        }

        fun showWarning(context: Context, title: String, text: String, critical: Boolean) {
            showNotification(context, title, text, critical, "warnings_channel", "Warnings")
        }
    }
}