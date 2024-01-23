package meshki.studio.negarname.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@Composable
fun Toolbox(
    visibility: MutableState<Boolean>,
    animation: MutableState<Animatable<Float, AnimationVector1D>>,
    animateFromUp: Boolean = false,
    content: @Composable () -> Unit
) {
    if (visibility.value) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .zIndex(5f),
            contentAlignment = Alignment.BottomCenter) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.9f).fillMaxWidth()
                .background(Color.Transparent)
                .zIndex(5.5f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { visibility.value = false }
                    )
                }) {
            }
        }
    }
    AnimatedVisibility(
        modifier = Modifier.zIndex(6f),
        visible = visibility.value && animation.value.value > (animation.value.upperBound
            ?: Float.MAX_VALUE) * 0.3,
        enter = fadeIn() + slideInVertically(initialOffsetY = {
            it / 8 * (if (animateFromUp) -1 else 1)
        }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = {
            it / 8 * (if (animateFromUp) -1 else 1)
        }),
    ) {
        content()
    }
}


