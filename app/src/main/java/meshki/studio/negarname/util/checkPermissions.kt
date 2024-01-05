package meshki.studio.negarname.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import timber.log.Timber

fun checkPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    action: () -> Unit
) {
    val isGranted = ContextCompat.checkSelfPermission(context, permission)
    if (isGranted == PackageManager.PERMISSION_GRANTED) {
        action()
    } else {
        launcher.launch(permission)
    }
}

fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    action: () -> Unit
) {
    if (
        permissions.all {
            val result = ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
            Timber.tag("Permissions").i("$it: $result")
            result
        }
    ) {
        action()
    } else {
        // Request permissions
        launcher.launch(permissions)
    }
}

fun checkAlarmsPermission(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Please give \"Alarms & reminders\" permission to this app and try again.", Toast.LENGTH_LONG).show()
            Intent().also { intent ->
                intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                context.startActivity(intent)
            }
        }
    }
}
