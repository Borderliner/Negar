package meshki.studio.negarname.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    action: () -> Unit
) {
    if (
        permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        action()
        // Use location because permissions are already granted
    } else {
        // Request permissions
        launcher.launch(permissions)
    }
}