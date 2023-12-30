package meshki.studio.negarname.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import meshki.studio.negarname.entity.Tool

@Composable
fun Toolbox(tool: Tool, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = tool.visibility.value && tool.animation.value.value > (tool.animation.value.upperBound
            ?: Float.MAX_VALUE) * 0.3,
        enter = fadeIn() + slideInVertically(initialOffsetY = {
            -it / 2 + 150
        }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = {
            -it / 2 + 150
        }),
    ) {
        content()
    }
}
