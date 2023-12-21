package meshki.studio.negarname.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.vm.MainViewModel
import meshki.studio.negarname.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(mainViewModel: MainViewModel) {
    val appMenuState = remember { mutableStateOf(false) }
    val aboutDialogState = remember { mutableStateOf(false) }
    val logoSize = 125.dp

    AboutDialog(aboutDialogState)

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(
                enabled = !appMenuState.value,
                modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                onClick = {
                    if (!appMenuState.value) {
                        appMenuState.value = !appMenuState.value
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
                if (isSystemInDarkTheme()) {
                    Image(
                        modifier = Modifier
                            .size(logoSize, logoSize)
                            .offset(x = 0.dp, y = 5.dp),
                        alpha = 1f,
                        painter = painterResource(R.drawable.img_logo_white),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.9f)),
                        contentDescription = stringResource(R.string.app_name)
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .size(logoSize, logoSize)
                            .offset(x = 0.dp, y = 5.dp),
                        alpha = 1f,
                        painter = painterResource(R.drawable.img_logo),
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.85f)),
                        contentDescription = stringResource(R.string.app_name)
                    )
                }
            }
        },
        actions = {
            val ctx = LocalContext.current
            //ToggleThemeButton()
            IconButton(
                enabled = !aboutDialogState.value,
                modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                onClick = {
//                    if (!aboutDialogState.value) {
//                        aboutDialogState.value = !aboutDialogState.value
//                    }
                    if (mainViewModel.locale == "fa") {
                        mainViewModel.setLocale(ctx, Locale("en").toLanguageTag())
                    } else {
                        mainViewModel.setLocale(ctx, Locale("fa").toLanguageTag())
                    }
                }) {
                Icon(
                    Icons.Outlined.Info,
                    "درباره‌ی نگارنامه",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .85f)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.Black.copy(0.1f)),
    )
}

@Composable
fun AboutDialog(state: MutableState<Boolean>) {
    val _moreInfo = remember { mutableStateOf(false) }
    val moreInfo: State<Boolean> = _moreInfo

    if (state.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                state.value = false
            },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true)
                ) {
                    // create custom title
                    Text(
                        stringResource(id = R.string.app_name),
                        modifier = Modifier.padding(top = 35.dp, bottom = 10.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onBackground)
                    )

                    if (moreInfo.value) {
                        Spacer(modifier = Modifier.height(25.dp))
                    } else {
                        Text(
                            text = "نگارنامه ابزاریست برای شما تا بتوانید کارهای خود را نظم دهید و با برنامه پیش بروید. اگر نگارنامه برای شما مفید است، می‌توانید با حمایت مالی به بهتر شدن آن کمک کنید!",
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "برنامه نویس: محمدرضا حاجیانپور",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier
                        .padding(start = 30.dp, bottom = 10.dp)
                        .width(100.dp),
                    onClick = {
                        state.value = false
                    }) {
                    Text(
                        "باشه",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }

            },
            dismissButton = {
                Button(
                    modifier = Modifier
                        .padding(start = 30.dp, bottom = 10.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                    onClick = {
                        _moreInfo.value = !moreInfo.value
                    }) {
                    Text(
                        "بیشتر",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        )
    }

}