package meshki.studio.negarname.services.notification

import android.app.PendingIntent
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationAction(
    val iconResource: Int,
    @StringRes val textResource: Int,
    val intent: PendingIntent
) : Parcelable
