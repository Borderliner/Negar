package meshki.studio.negarname.ui.navigation

import android.Manifest
import android.app.Activity
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import meshki.studio.negarname.ui.app.AppState
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.util.checkPermission
import meshki.studio.negarname.ui.app.AppViewModel
import org.koin.compose.koinInject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(appState: AppState) {
    val appViewModel = koinInject<AppViewModel>()
    val logoSize = 125.dp

    val modalSheetState = rememberModalBottomSheetState()

    if (modalSheetState.currentValue != SheetValue.Hidden) {
        ModalBottomSheet(onDismissRequest = {
            appState.coroutineScope.launch { modalSheetState.hide() }
        }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.import_export), fontSize = 20.sp)
                Text(text = stringResource(R.string.import_export_desc))
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
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
                                appState.coroutineScope.launch {
                                    val result = appState.snackbar.showSnackbar(
                                        message = ctx.resources.getString(R.string.storage_permission),
                                        actionLabel = ctx.resources.getString(R.string.allow),
                                        withDismissAction = false,
                                        duration = SnackbarDuration.Long
                                    )

                                    when (result) {
                                        SnackbarResult.Dismissed -> println()
                                        SnackbarResult.ActionPerformed -> {
                                            ActivityCompat.requestPermissions(
                                                ctx as Activity, arrayOf(
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                ), 0
                                            )
                                        }
                                    }
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
                            appState.coroutineScope.launch {
                                checkPermission(
                                    ctx,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    permissionLauncher
                                ) {
                                    appViewModel.viewModelScope.launch {
                                        appViewModel.database.close()
                                        appViewModel.appRepository.checkpoint()
                                        val path =
                                            appViewModel.appRepository.getDatabaseFilePath()
                                        if (path != null) {
                                            val sourceFile = File(path)
                                            Timber.tag("Import/Export").i("Export source file: $path")
                                            val documentsFolder = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
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

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Gray.copy(alpha = 0.15f)),
        navigationIcon = {
            IconButton(
                //enabled = !appMenuState.value,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                onClick = {
                    appState.coroutineScope.launch {
                        if (appViewModel.drawerState.isOpen) {
                            appViewModel.drawerState.close()
                        } else {
                            appViewModel.drawerState.open()
                        }
                    }
                }) {
                Icon(
                    Icons.Filled.MoreVert,
                    "Menu",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .85f)
                )
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (appViewModel.theme.lowercase() == "light" || (appViewModel.theme.lowercase() == "system" && !isSystemInDarkTheme())) {
                    Image(
                        modifier = Modifier
                            .size(logoSize, logoSize)
                            .offset(x = 0.dp, y = 5.dp),
                        alpha = 1f,
                        painter = painterResource(R.drawable.img_logo),
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.85f)),
                        contentDescription = stringResource(R.string.app_name)
                    )
                } else if (appViewModel.theme.lowercase() == "dark" || (appViewModel.theme.lowercase() == "system" && isSystemInDarkTheme())) {
                    Image(
                        modifier = Modifier
                            .size(logoSize, logoSize)
                            .offset(x = 0.dp, y = 5.dp),
                        alpha = 1f,
                        painter = painterResource(R.drawable.img_logo_white),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.9f)),
                        contentDescription = stringResource(R.string.app_name)
                    )
                }
            }
        },
        actions = {
            //ToggleThemeButton()
            IconButton(
                modifier = Modifier.padding(top = 8.dp, end = 8.dp),
                onClick = {
                    appState.coroutineScope.launch {
                        modalSheetState.show()
                    }
                }) {
                Icon(
                    painterResource(R.drawable.vec_export_notes),
                    "",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .85f)
                )
            }
        },
    )
}

@Throws(IOException::class)
fun copyFile(sourceFile: File?, destFile: File) {
    if (!destFile.parentFile?.exists()!!) destFile.parentFile?.mkdirs()
    if (!destFile.exists()) {
        destFile.createNewFile()
    }
    var source: FileChannel? = null
    var destination: FileChannel? = null
    try {
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
        destination.transferFrom(source, 0, source.size())
    } finally {
        source?.close()
        destination?.close()
    }
}