package meshki.studio.negarname.ui.util.extensions

import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.LocaleListCompat

fun Context.getActivity(): ComponentActivity {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

fun Context.pxToDp(px: Int): Int {
    return (px / this.resources.displayMetrics.density).toInt()
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * this.resources.displayMetrics.density).toInt()
}

fun Context.getCurrentLocale(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        resources.configuration.locales.get(0).language
    } else{
        //noinspection deprecation
        resources.configuration.locale.language
    }
}

fun Context.setLocale(localeTag: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(localeTag)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(localeTag)
        )
    }
}

data class AppVersion(
    val versionName: String,
    val versionNumber: Long,
)
fun Context.getAppVersion(): AppVersion {
    return try {
        val packageManager = packageManager
        val packageName = packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        AppVersion(
            versionName = packageInfo.versionName,
            versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo),
        )
    } catch (e: Exception) {
        AppVersion("", 0)
    }
}

fun Context.restartApp() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    // Required for API 34 and later
    // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
    mainIntent.setPackage(packageName)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}
