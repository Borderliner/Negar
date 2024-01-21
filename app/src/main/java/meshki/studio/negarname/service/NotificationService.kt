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
import meshki.studio.negarname.R
import meshki.studio.negarname.util.getCurrentLocale
import meshki.studio.negarname.util.getParcelable
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
    val id: Long,
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

            context.startService(notificationIntent)
        }

        fun stopNotification(context: Context, id: Int) {
            context.stopService(Intent(context, NotificationService::class.java)
                .apply {
                    putExtra("id", id)
                })
        }

        fun stopAllNotifications(context: Context) {
            context.stopService(Intent(context, NotificationService::class.java))
        }
    }

    private var manager: NotificationManagerCompat? = null
    private val notificationIdList = mutableListOf<Int>()

    override fun onCreate() {
        super.onCreate()
        manager = NotificationManagerCompat.from(this)
    }

    override fun onDestroy() {
        manager = null
        notificationIdList.clear()
        super.onDestroy()
    }

    override fun stopService(intent: Intent): Boolean {
        val id = intent.getIntExtra("id", -1)
        if (id > 0 ) {
            manager?.cancel(id)
            stopSelfResult(id)
            val list = notificationIdList.filter { it != id }
            notificationIdList.clear()
            notificationIdList.addAll(list)
        } else {
            manager?.cancelAll()
            notificationIdList.forEachIndexed { idx, i ->
                stopSelfResult(i)
                notificationIdList.removeAt(idx)
            }
        }
        return super.stopService(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.getParcelable("data", NotificationData::class.java)

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

            notificationBuilder
                .setSmallIcon(R.drawable.alarm)
                .setPriority(priority)
                .setCategory(data.category)
                .setVibrate(data.vibration.toLongArray())
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
                .setContentIntent(data.onClickAction?.intent)

            // Add actions
            data.actions.forEach {
                notificationBuilder.addAction(
                    it.iconResource,
                    this.resources.getString(it.textResource),
                    it.intent
                )
            }

            if (!data.critical)
                notificationBuilder.setSound(data.sound)

            val notification = notificationBuilder.build()
            if (data.critical) {
                notification.flags =
                    notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        startForeground(
                            data.id.toInt(),
                            notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        )
                    } else {
                        startForeground(data.id.toInt(), notification)
                    }
                } catch (exc: Exception) {
                    Toast.makeText(
                        this,
                        "Unhandled Exception: $exc",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                try {
                    manager?.notify(data.id.toInt(), notification)
                } catch (exc: SecurityException) {
                    Toast.makeText(
                        this,
                        "Cannot use notifications. Please give permission.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            notificationIdList.add(data.id.toInt())
            Timber.tag("Notification").i("ID List: $notificationIdList")
            //if (data.critical) ringtone?.play()
        } else {
            Timber.e("Notification Service received empty intent data.")
        }
        return if (data?.critical == true) START_STICKY else START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

