package meshki.studio.negarname.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import meshki.studio.negarname.R

class NotificationService {
    companion object {
        private fun showNotification(context: Context, title: String, text: String, channelId: String, channelName: String) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
            manager.notify(1, builder.build())
        }
        fun showSchedule(context: Context, title: String, text: String) {
            showNotification(context, title, text, "schedule_channel", "Scheduling Notifications")
        }

        fun showWarning(context: Context, title: String, text: String) {
            showNotification(context, title, text, "warning_channel", "Warnings")
        }
    }
}