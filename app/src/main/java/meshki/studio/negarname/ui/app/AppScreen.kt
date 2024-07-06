package meshki.studio.negarname.ui.app

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import io.embrace.android.embracesdk.Embrace
import kotlinx.coroutines.launch
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.components.AboutDialog
import meshki.studio.negarname.ui.components.BackupSheetModal
import meshki.studio.negarname.ui.navigation.AppBottomBar
import meshki.studio.negarname.ui.navigation.AppNavigation
import meshki.studio.negarname.ui.navigation.AppTopBar
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.ui.util.LeftToRightLayout
import meshki.studio.negarname.ui.util.RightToLeftLayout
import meshki.studio.negarname.ui.util.extensions.getAppVersion
import meshki.studio.negarname.ui.util.isDarkTheme
import org.koin.compose.koinInject
import java.text.DecimalFormat
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val appViewModel = koinInject<AppViewModel>()
    val appState = appViewModel.appState.collectAsState()
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val aboutDialog = remember { mutableStateOf(false) }
    AboutDialog(state = aboutDialog)

    val backupSheetModalState = rememberModalBottomSheetState()
    BackupSheetModal(snackbar = appViewModel.snackbarHost, sheetState = backupSheetModalState)

    ModalNavigationDrawer(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isDarkTheme(theme = appState.value.theme)) 1f else 0.755f
                        ),
                        Color.Transparent
                    )
                )
            )
            .blur(0.dp),
        drawerState = appViewModel.drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(270.dp),
                drawerShape = RoundedCornerShape(30.dp),
            ) {
                val ctx = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.1f))
                ) {
                    Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))
                }
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp)
                ) {
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.language) + ": ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(appViewModel.getLocaleName())
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.language),
                                contentDescription = stringResource(R.string.language)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                appViewModel.drawerState.close()
                                if (appState.value.locale == "fa") {
                                    appViewModel.onEvent(AppEvent.SetLocale(Locale("en").toLanguageTag()))
                                } else {
                                    appViewModel.onEvent(AppEvent.SetLocale(Locale("fa").toLanguageTag()))
                                }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.theme) + ": ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    stringResource(
                                        when (appState.value.theme.lowercase()) {
                                            "light" -> R.string.light
                                            "dark" -> R.string.dark
                                            "system" -> R.string.system
                                            else -> R.string.off
                                        }
                                    )
                                )
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(
                                    when (appState.value.theme.lowercase()) {
                                        "light" -> R.drawable.vec_light_mode
                                        "dark" -> R.drawable.dark_mode
                                        "system" -> R.drawable.vec_routine
                                        else -> R.drawable.vec_routine
                                    }
                                ),
                                contentDescription = stringResource(R.string.theme)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                when (appState.value.theme) {
                                    "system" -> appViewModel.onEvent(AppEvent.SetTheme("dark"))
                                    "dark" -> appViewModel.onEvent(AppEvent.SetTheme("light"))
                                    "light" -> appViewModel.onEvent(AppEvent.SetTheme("system"))
                                }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.buy) + " ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = stringResource(R.string.pro_version))
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_verified),
                                contentDescription = stringResource(R.string.buy)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                //
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.create_restore) + " ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = stringResource(R.string.backup))
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_create_backup),
                                contentDescription = stringResource(R.string.create_restore)
                                        + " " + stringResource(R.string.backup)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                appViewModel.drawerState.close()
                                backupSheetModalState.show()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.report_bugs),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_bug_report),
                                contentDescription = stringResource(R.string.report_bugs)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                Embrace.getInstance().showBugReportForm()
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(R.string.about),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_info),
                                contentDescription = stringResource(R.string.about)
                            )
                        },
                        selected = false,
                        onClick = {
                            aboutDialog.value = true
                        }
                    )
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = PastelRed,
                            unselectedTextColor = Color.Black
                        ),
                        label = {
                            Text(
                                text = stringResource(R.string.exit),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_power),
                                contentDescription = stringResource(R.string.exit)
                            )
                        },
                        selected = false,
                        onClick = {
                            val activity = (ctx as? Activity)
                            activity?.finishAndRemoveTask()
                        }
                    )
                    Text(
                        stringResource(R.string.version) + ": " + DecimalFormat.getInstance()
                            .format(ctx.getAppVersion().versionName.toDouble()),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        gesturesEnabled = true
    ) {
        var drawerWidth by remember {
            mutableStateOf(appViewModel.drawerState.offset.value)
        }

        val xPos = remember {
            derivedStateOf {
                abs(drawerWidth) - abs(appViewModel.drawerState.offset.value)
            }
        }

        SideEffect {
            if (drawerWidth == 0f) {
                drawerWidth = appViewModel.drawerState.offset.value
            }
        }

        Scaffold(
            snackbarHost = {
                if (appState.value.isRtl) {
                    LeftToRightLayout {
                        SnackbarHost(appViewModel.snackbarHost)
                    }
                } else {
                    RightToLeftLayout {
                        SnackbarHost(appViewModel.snackbarHost)
                    }
                }
            },
            modifier = Modifier
                .offset(x = with(LocalDensity.current) {
                    max(0.dp, xPos.value.toDp() - 90.dp)
                })
                .blur(radius = with(LocalDensity.current) {
                    (xPos.value / 500).toDp()
                })
                .clip(RoundedCornerShape(30.dp)),
            topBar = { AppTopBar() },
            bottomBar = {
                AnimatedVisibility(
                    visible = appState.value.isBottomBarVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = {
                        it / 12
                    }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = {
                        it / 12
                    }),
                ) {
                    AppBottomBar(navController)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            //modifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                Modifier
                    .padding(top = it.calculateTopPadding())
            ) {
                Divider(color = Color.Gray.copy(0.4f), thickness = 1.dp)
                if (appState.value.isReady) {
                    AppNavigation(navController)
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Loading...")
                    }
                }
            }
        }
    }

    appViewModel.onEvent(AppEvent.SetReady(true))
}
