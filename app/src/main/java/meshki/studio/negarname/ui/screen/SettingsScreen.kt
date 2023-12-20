package meshki.studio.negarname.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(navigateTo: (route: String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Settings Screen")
    }
}