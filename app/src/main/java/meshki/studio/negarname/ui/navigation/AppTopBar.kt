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
                        // TODO: add help
                    }
                }) {
                Icon(
                    painterResource(R.drawable.vec_question),
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