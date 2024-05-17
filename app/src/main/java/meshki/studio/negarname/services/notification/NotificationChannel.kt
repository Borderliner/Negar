package meshki.studio.negarname.services.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationChannel(
    val id: String,
    val name: String
) : Parcelable
