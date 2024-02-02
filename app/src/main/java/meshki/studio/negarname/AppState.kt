package meshki.studio.negarname

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppState(
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
    AppState(
        coroutineScope = coroutineScope,
        navController = navController,
        snackbar = snackbarHostState
    )
}
