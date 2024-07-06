package meshki.studio.negarname.ui.app

import androidx.compose.material3.SnackbarDuration

sealed class AppEvent {
    data class SetTheme(val theme: String) : AppEvent()
    data class SetLocale(val locale: String) : AppEvent()
    data class SetBottomBarVisible(val value: Boolean) : AppEvent()
    data class SetReady(val value: Boolean) : AppEvent()
    data class ShowSnackbar(val label: String, val message: String, val duration: SnackbarDuration = SnackbarDuration.Short, val withDismissAction: Boolean = false) : AppEvent()
}
