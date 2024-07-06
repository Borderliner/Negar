package meshki.studio.negarname.ui.app

import android.animation.ObjectAnimator
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.components.BackupContract
import meshki.studio.negarname.ui.theme.NegarTheme
import meshki.studio.negarname.ui.util.LeftToRightLayout
import meshki.studio.negarname.ui.util.RightToLeftLayout
import org.koin.compose.KoinContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class AppActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen().apply {
            setOnExitAnimationListener { viewProvider ->
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    View.ALPHA,
                    1f,
                    0f
                ).apply {
                    duration = 500L
                    doOnEnd {
                        viewProvider.remove()
                    }
                    start()
                }
            }
        }
        enableEdgeToEdge()
        val appViewModel: AppViewModel = get()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !appViewModel.appState.value.isReady
        }

        setContent {
            KoinContext {
                val appState = appViewModel.appState.collectAsState()
                NegarTheme(
                    darkTheme = when (appState.value.theme) {
                        "system" -> isSystemInDarkTheme()
                        "dark" -> true
                        "light" -> false
                        else -> isSystemInDarkTheme()
                    },
                    isRtl = appState.value.isRtl
                ) {
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
                            if (appState.value.isRtl) {
                                RightToLeftLayout {
                                    AppScreen()
                                }
                            } else {
                                LeftToRightLayout {
                                    AppScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
