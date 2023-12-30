package meshki.studio.negarname.entity

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableStateOf

data class Tool(
    val id: String,
    private val initialVisibility: Boolean = false,
    val initialAnimationBounds: Pair<Float, Float> = Pair(0f, 220f)
) {
    val animation = mutableStateOf(Animatable(0f))
    val visibility = mutableStateOf(initialVisibility)

    init {
        animation.value.updateBounds(initialAnimationBounds.first, initialAnimationBounds.second)
    }
}