package meshki.studio.negarname.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class BackupContract: ActivityResultContract<Int, Uri?>() {
    companion object {
        const val REQUEST_CODE = 1001
    }

    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "negarname_backup_" + System.currentTimeMillis() + ".negar")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val data = intent?.data
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else null
    }
}

class RestoreContract: ActivityResultContract<Int, Uri?>() {
    companion object {
        const val REQUEST_CODE = 1002
    }

    override fun createIntent(context: Context, input: Int): Intent {
        val intentType = "application/octet-stream"
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = intentType
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(intentType))
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val data = intent?.data
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else null
    }
}
