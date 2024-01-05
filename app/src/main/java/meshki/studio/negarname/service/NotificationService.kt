package meshki.studio.negarname.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import kotlinx.parcelize.Parcelize
import meshki.studio.negarname.MainActivity
import meshki.studio.negarname.R
import meshki.studio.negarname.util.getCurrentLocale
import meshki.studio.negarname.util.setLocale
import timber.log.Timber

enum class NotificationPriority {
    HIGH,
    NORMAL,
    LOW
}

@Parcelize
data class NotificationAction(
    val iconResource: Int,
    @StringRes val textResource: Int,
    val intent: PendingIntent
) : Parcelable

@Parcelize
data class NotificationChannelData(
    val id: String,
    val name: String
) : Parcelable

@Parcelize
data class NotificationData(
    val id: Int,
    val title: String,
    val text: String,
    val channel: NotificationChannelData,
    val critical: Boolean = false,
    val sticky: Boolean = false,
    val sound: Uri?,
    val color: String?,
    val icon: Int,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val vibration: List<Long> = listOf(0, 1000),
    val category: String = NotificationCompat.CATEGORY_MESSAGE,
    val onClickAction: NotificationAction?,
    val actions: List<NotificationAction> = listOf()
) : Parcelable

class NotificationService : Service() {
    companion object {
        fun showNotification(
            context: Context,
            data: NotificationData
        ) {
            val notificationIntent = Intent(context, NotificationService::class.java)
            notificationIntent.apply {
                putExtra("data", data)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, notificationIntent)
            } else {
                context.startService(notificationIntent)
            }
        }

        fun stopNotification(context: Context) {
            context.stopService(Intent(context, NotificationService::class.java))
        }
    }

    private var manager: NotificationManagerCompat? = null
    private val notificationIdList = mutableListOf<Int>()

    override fun onCreate() {
        super.onCreate()
        manager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data: NotificationData? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("data", NotificationData::class.java)
        } else {
            intent?.getParcelableExtra("data")
        }

        if (data != null) {
            val notificationBuilder = NotificationCompat.Builder(this, data.channel.id)

            this.grantUriPermission(
                "com.android.systemui",
                data.sound,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // Create notification channel for API +26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance =
                    if (data.critical) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
                val att = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val ctx = this
                val channel =
                    NotificationChannel(data.channel.id, data.channel.name, importance).apply {
                        name = data.channel.name
                        description = data.channel.name
                        enableLights(true)
                        enableVibration(true)
                        vibrationPattern = data.vibration.toLongArray()
                        lightColor =
                            if (!data.color.isNullOrBlank()) data.color.toColorInt() else getColor(R.color.primary)
                        if (!data.critical) setSound(data.sound, att)
                        setBypassDnd(data.critical)
                        lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                        setShowBadge(true)
                        setImportance(importance)
                        setLocale(ctx, getCurrentLocale(ctx))
                        // ringtone = ringtone!!
                    }

                manager?.createNotificationChannel(channel)
            }

            val priority =
                if (data.critical) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_DEFAULT

            val action1 = data.actions[0]
            notificationBuilder
                .setSmallIcon(R.drawable.alarm)
                .setPriority(priority)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(longArrayOf(0L, 1000L))
                .setColor(
                    if (!data.color.isNullOrBlank()) data.color.toColorInt() else ContextCompat.getColor(
                        this,
                        R.color.primary
                    )
                )
                .setContentTitle(data.title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(data.text))
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .addAction(
                    action1.iconResource,
                    this.resources.getString(action1.textResource),
                    action1.intent
                )
                .setContentIntent(data.onClickAction?.intent)

            if (!data.critical)
                notificationBuilder.setSound(data.sound)

            val notification = notificationBuilder.build()
            if (data.critical) {
                notification.flags =
                    notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        startId,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(startId, notification)
                }
            } else {
                try {
                    manager?.notify(startId, notification)
                } catch (exc: SecurityException) {
                    Toast.makeText(
                        this,
                        "Cannot use notifications. Please give permission.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            notificationIdList.add(startId)
            Timber.tag("Notification").i("ID List: $notificationIdList")
            //if (data.critical) ringtone?.play()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopService(null)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun stopService(intent: Intent?): Boolean {
        manager?.cancelAll()
        notificationIdList.forEachIndexed { idx, i ->
            stopSelfResult(i)
            notificationIdList.removeAt(idx)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        return super.stopService(intent)
    }
}

