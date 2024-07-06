package meshki.studio.negarname.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meshki.studio.negarname.R
import meshki.studio.negarname.data.local.Database
import meshki.studio.negarname.data.repository.DatabaseRepository
import meshki.studio.negarname.ui.app.AppEvent
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.util.checkPermissions
import meshki.studio.negarname.ui.util.extensions.copyTo
import meshki.studio.negarname.ui.util.extensions.getActivity
import meshki.studio.negarname.ui.util.extensions.restartApp
import meshki.studio.negarname.ui.util.rememberMultiplePermissionLauncher
import net.lingala.zip4j.ZipFile
import org.koin.compose.koinInject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSheetModal(snackbar: SnackbarHostState, sheetState: SheetState) {
    val scope = rememberCoroutineScope()
    val appViewModel = koinInject<AppViewModel>()
    val ctx = LocalContext.current
    val database = koinInject<Database>()
    val databaseRepository = koinInject<DatabaseRepository>()
    val requiredPermissions = mutableListOf<String>()
    // No WRITE_EXTERNAL_STORAGE for Android 11+
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    val permissionLauncher = rememberMultiplePermissionLauncher(snackbar)
    val backupLauncher = rememberLauncherForActivityResult(BackupContract()) {
        try {
            if (it == null) throw Error(ctx.getString(R.string.error_backup_no_access))
            scope.launch {
                withContext(Dispatchers.IO) {
                    databaseRepository.checkpoint()
                    val databasePath = databaseRepository.getDatabaseFilePath()
                    if (databasePath.isNullOrEmpty()) throw Error(ctx.getString(R.string.error_backup_database_file_notfound))
                    else {
                        val outputStream = ctx.contentResolver.openOutputStream(it)!!
                        val inputStream = FileInputStream(File(databasePath))
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()

                        scope.launch {
                            sheetState.hide()
                            Toast.makeText(ctx, R.string.backup_success, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } catch (err: Error) {
            scope.launch {
                sheetState.hide()
                Toast.makeText(ctx, err.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(RestoreContract()) {
        try {
            if (it == null) throw Error(ctx.getString(R.string.error_backup_no_access))
            scope.launch {
                withContext(Dispatchers.IO) {
                    val databasePath = databaseRepository.getDatabaseFilePath()
                    if (databasePath.isNullOrEmpty()) throw Error(ctx.getString(R.string.error_backup_database_file_notfound))
                    else {
                        database.close()
                        val inputStream = ctx.contentResolver.openInputStream(it)!!
                        val outputStream = FileOutputStream(File(databasePath))

                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()

                        scope.launch {
                            sheetState.hide()
                            val result = appViewModel.showSnackbar(
                                message = ctx.getString(R.string.restore_success),
                                actionLabel = ctx.getString(R.string.restartApp)
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    ctx.restartApp()
                                }

                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                }
            }
        } catch (err: Error) {
            scope.launch {
                sheetState.hide()
                Toast.makeText(ctx, err.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    if (sheetState.currentValue != SheetValue.Hidden) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch { sheetState.hide() }
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.import_export), fontSize = 24.sp)
                Text(text = stringResource(R.string.import_export_desc))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top,
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = PastelGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            scope.launch {
                                checkPermissions(
                                    ctx, requiredPermissions.toTypedArray(), permissionLauncher
                                ) {
                                    restoreLauncher.launch(BackupContract.REQUEST_CODE)
                                }
                            }
                        }
                    ) {
                        Column(
                            Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.vec_download),
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = Color.Black.copy(alpha = 0.9f)
                            )
                            Text(text = stringResource(R.string.import_data), fontSize = 16.sp)
                        }
                    }

                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = PastelRed,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            scope.launch {
                                checkPermissions(
                                    ctx, requiredPermissions.toTypedArray(), permissionLauncher
                                ) {
                                    backupLauncher.launch(BackupContract.REQUEST_CODE)
                                }
                            }
                        }
                    ) {
                        Column(
                            Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.vec_upload),
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = Color.Black.copy(alpha = 0.9f)
                            )
                            Text(text = stringResource(R.string.export_data), fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
