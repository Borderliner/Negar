package meshki.studio.negarname.ui.app

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.ui.theme.NegarTheme
import meshki.studio.negarname.ui.util.LeftToRightLayout
import meshki.studio.negarname.ui.util.RightToLeftLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AppActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appViewModel: AppViewModel = get()
        enableEdgeToEdge()

        setContent {
                val appState: AppState = rememberAppState()

                NegarTheme(
                    darkTheme = when (appViewModel.theme) {
                        "system" -> isSystemInDarkTheme()
                        "dark" -> true
                        "light" -> false
                        else -> isSystemInDarkTheme()
                    },
                    isRtl = appViewModel.isRtl
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
                            if (appViewModel.isRtl) {
                                RightToLeftLayout {
                                    AppScreen(appState)
                                }
                            } else {
                                LeftToRightLayout {
                                    AppScreen(appState)
                                }
                            }
                        }
                    }
                }

        }
    }
}

fun Context.getActivity(): ComponentActivity {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}
