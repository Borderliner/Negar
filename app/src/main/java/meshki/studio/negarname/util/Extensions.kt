package meshki.studio.negarname.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable

fun <T : Parcelable> Intent.getParcelable(key: String, m_class: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelableExtra(key, m_class)
    else
        this.getParcelableExtra(key)
}
