package meshki.studio.negarname.ui.app

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppState2(
    val coroutineScope: CoroutineScope,
    val navController: NavHostController,
    val snackbar: SnackbarHostState,
) {
    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        coroutineScope.launch {
            snackbar.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}

@Composable
fun rememberAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) = remember(coroutineScope, navController, snackbarHostState) {
    AppState2(
        coroutineScope = coroutineScope,
        navController = navController,
        snackbar = snackbarHostState
    )
}

data class AppState(
    val isReady: Boolean = false,
    val isRtl: Boolean = false,
    val locale: String = "en",
    val isBottomBarVisible: Boolean = true,
    val theme: String = "system"
)
