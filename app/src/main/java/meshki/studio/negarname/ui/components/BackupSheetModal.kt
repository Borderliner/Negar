package meshki.studio.negarname.ui.components

import android.Manifest
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.navigation.copyFile
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.util.checkPermission
import timber.log.Timber
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSheetModal(appViewModel: AppViewModel, sheetState: SheetState) {
    val scope = rememberCoroutineScope()
    if (sheetState.currentValue != SheetValue.Hidden) {
        ModalBottomSheet(onDismissRequest = {
            scope.launch { sheetState.hide() }
        }) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
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
                        onClick = {}
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

                    val ctx = LocalContext.current
                    val permissionLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                            if (!granted) {
                                scope.launch {
//                                    val result = appState.snackbar.showSnackbar(
//                                        message = ctx.resources.getString(R.string.storage_permission),
//                                        actionLabel = ctx.resources.getString(R.string.allow),
//                                        withDismissAction = false,
//                                        duration = SnackbarDuration.Long
//                                    )
//
//                                    when (result) {
//                                        SnackbarResult.Dismissed -> println()
//                                        SnackbarResult.ActionPerformed -> {
//                                            ActivityCompat.requestPermissions(
//                                                ctx as Activity, arrayOf(
//                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                                ), 0
//                                            )
//                                        }
//                                    }
                                }
//                                Toast.makeText(ctx, permissionText, Toast.LENGTH_LONG)
//                                    .show()
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
                                checkPermission(
                                    ctx,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    permissionLauncher
                                ) {
                                    scope.launch {
                                        appViewModel.database.close()
                                        appViewModel.appRepository.checkpoint()
                                        val path =
                                            appViewModel.appRepository.getDatabaseFilePath()
                                        if (path != null) {
                                            val sourceFile = File(path)
                                            Timber.tag("Import/Export").i("Export source file: $path")
                                            val documentsFolder = ctx.getExternalFilesDir(
                                                Environment.DIRECTORY_DOCUMENTS)
                                            if (documentsFolder != null) {
                                                val destinationPath = documentsFolder.absolutePath + File.separator + sourceFile.name
                                                Timber.tag("Import/Export").i("Export destination file: $destinationPath")
                                                val destinationFile = File(destinationPath)
                                                copyFile(sourceFile, destinationFile)
                                            }
                                        }
                                    }
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
