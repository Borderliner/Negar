package meshki.studio.negarname.ui.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import timber.log.Timber

@Composable
fun rememberMultiplePermissionLauncher(snackbar: SnackbarHostState, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}, onRetry: () -> Unit = {}): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    return rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
        val areGranted =
            permissionMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            onSuccess()
        } else {
            scope.launch {
                val result = snackbar.showSnackbar(
                    message = ctx.resources.getString(R.string.permission_required_text),
                    actionLabel = ctx.resources.getString(R.string.allow),
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )

                when (result) {
                    SnackbarResult.Dismissed -> {
                        Toast.makeText(
                            ctx,
                            ctx.resources.getString(R.string.permission_required_text),
                            Toast.LENGTH_SHORT
                        ).show()
                        onFail()
                    }

                    SnackbarResult.ActionPerformed -> onRetry()
                }
            }
        }
    }
}

@Composable
fun rememberSinglePermissionLauncher(snackbar: SnackbarHostState, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}, onRetry: () -> Unit = {}): ManagedActivityResultLauncher<String, Boolean> {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    return rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            onSuccess()
        } else {
            scope.launch {
                val result = snackbar.showSnackbar(
                    message = ctx.resources.getString(R.string.permission_required_text),
                    actionLabel = ctx.resources.getString(R.string.allow),
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )

                when (result) {
                    SnackbarResult.Dismissed -> {
                        Toast.makeText(
                            ctx,
                            ctx.resources.getString(R.string.permission_required_text),
                            Toast.LENGTH_SHORT
                        ).show()
                        onFail()
                    }

                    SnackbarResult.ActionPerformed -> onRetry()
                }
            }
        }
    }
}

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
