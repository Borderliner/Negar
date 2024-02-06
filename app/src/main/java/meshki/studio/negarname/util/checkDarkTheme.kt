package meshki.studio.negarname.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun isDarkTheme(theme: String): Boolean {
    return theme == "dark" || (theme == "system") && isSystemInDarkTheme()
}
