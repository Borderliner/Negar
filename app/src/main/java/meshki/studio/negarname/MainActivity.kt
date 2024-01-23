package meshki.studio.negarname

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.embrace.android.embracesdk.Embrace
import kotlinx.coroutines.launch
import meshki.studio.negarname.ui.element.BottomBar
import meshki.studio.negarname.ui.element.TopBar
import meshki.studio.negarname.ui.theme.NegarTheme
import meshki.studio.negarname.ui.theme.PastelRed
import meshki.studio.negarname.util.LeftToRightLayout
import meshki.studio.negarname.util.RightToLeftLayout
import meshki.studio.negarname.util.getAppVersion
import meshki.studio.negarname.vm.MainViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import timber.log.Timber
import java.text.DecimalFormat
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinContext {
                val mainViewModel = koinInject<MainViewModel>()
                val navController = rememberNavController()

                NegarTheme {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Card(
                            colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(
                                    top = 16.dp,
                                    bottom = 0.dp,
                                    start = 0.dp,
                                    end = 0.dp
                                ),
                            elevation = CardDefaults.elevatedCardElevation(20.dp),
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            ),

                            ) {
                            if (mainViewModel.isRtl) {
                                RightToLeftLayout {
                                    MainScreenScaffold(navController)
                                }
                            } else {
                                LeftToRightLayout {
                                    MainScreenScaffold(navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenScaffold(navController: NavHostController) {
    val mainViewModel = koinInject<MainViewModel>()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            ),
        drawerState = mainViewModel.drawerState,
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
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp)) {
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.language) + ": ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(mainViewModel.getLocaleName())
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
                                mainViewModel.drawerState.close()
                                if (mainViewModel.locale == "fa") {
                                    mainViewModel.setLocale(Locale("en").toLanguageTag())
                                } else {
                                    mainViewModel.setLocale(Locale("fa").toLanguageTag())
                                }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Text(
                                    text = stringResource(R.string.dark_mode) + ": ",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (isSystemInDarkTheme()) stringResource(R.string.on) else stringResource(
                                        R.string.off
                                    )
                                )
                            }
                        },
                        badge = {
                            Icon(
                                painter = painterResource(R.drawable.vec_nights_stay),
                                contentDescription = stringResource(R.string.dark_mode)
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
                            //
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
                            scope.launch {
                                val activity = (ctx as? Activity)
                                activity?.finish()
                            }
                        }
                    )
                    Text(
                        stringResource(R.string.version) + ": " + DecimalFormat.getInstance()
                            .format(getAppVersion(ctx).versionName.toDouble()),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        gesturesEnabled = true
    ) {
        var drawerWidth by remember {
            mutableStateOf(mainViewModel.drawerState.offset.value)
        }

        val contentOffset = remember {
            derivedStateOf {
                mainViewModel.drawerState.offset.value
            }
        }

        SideEffect {
            if (drawerWidth == 0f) {
                drawerWidth = mainViewModel.drawerState.offset.value
            }
        }
        var xPos by remember { mutableFloatStateOf(abs(drawerWidth) - abs(contentOffset.value)) }

        LaunchedEffect(drawerWidth, contentOffset.value) {
            xPos = abs(drawerWidth) - abs(contentOffset.value)
        }

        Scaffold(
            modifier = Modifier
                .offset(x = with(LocalDensity.current) {
                    max(0.dp, xPos.toDp() - 90.dp)
                })
                .blur(radius = (xPos / 500).dp),
            topBar = { TopBar() },
            bottomBar = {
                AnimatedVisibility(
                    visible = mainViewModel.isBottomBarVisible.value,
                    enter = fadeIn() + slideInVertically(initialOffsetY = {
                        it / 12
                    }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = {
                        it / 12
                    }),
                ) {
                    BottomBar(navController)
                }
            },
            containerColor = Color.Transparent,
            //modifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                Modifier
                    .padding(top = it.calculateTopPadding())
            ) {
                Divider(color = Color.Gray.copy(0.4f), thickness = 1.dp)
                if (mainViewModel.isReady) {
                    Navigation(navController)
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
}
