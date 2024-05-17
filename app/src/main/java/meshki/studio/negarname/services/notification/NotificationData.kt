package meshki.studio.negarname.services.notification

import android.net.Uri
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    val id: Long,
    val title: String,
    val text: String,
    val channel: NotificationChannel,
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
