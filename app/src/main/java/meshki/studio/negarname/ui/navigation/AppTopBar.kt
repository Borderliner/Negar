package meshki.studio.negarname.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.app.AppViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    val scope = rememberCoroutineScope()
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
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
                    scope.launch {
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
                if (appState.value.theme.lowercase() == "light" || (appState.value.theme.lowercase() == "system" && !isSystemInDarkTheme())) {
                    Image(
                        modifier = Modifier
                            .size(logoSize, logoSize)
                            .offset(x = 0.dp, y = 5.dp),
                        alpha = 1f,
                        painter = painterResource(R.drawable.img_logo),
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.85f)),
                        contentDescription = stringResource(R.string.app_name)
                    )
                } else if (appState.value.theme.lowercase() == "dark" || (appState.value.theme.lowercase() == "system" && isSystemInDarkTheme())) {
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
                    scope.launch {
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
