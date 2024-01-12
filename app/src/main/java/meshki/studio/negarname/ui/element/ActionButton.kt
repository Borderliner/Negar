package meshki.studio.negarname.ui.element

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import meshki.studio.negarname.ui.theme.RoundedShapes
import meshki.studio.negarname.util.bounceClick
import meshki.studio.negarname.util.getNavigationBarHeight
import meshki.studio.negarname.util.keyboardAsState

@Composable
fun ActionButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val paddingSize = 2.dp

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val isKeyboardOpen by keyboardAsState()
    val fabPadding = remember {
        mutableFloatStateOf(getNavigationBarHeight().toFloat() - 50)
    }

    val insets: WindowInsetsCompat? = ViewCompat.getRootWindowInsets((LocalContext.current as Activity).window.decorView)
    val keyboardHeight = insets!!.getInsets(WindowInsetsCompat.Type.ime()).bottom
    println("Kbd height: $keyboardHeight")

    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen) {
            fabPadding.floatValue = (keyboardHeight.toFloat() * -1f) + getNavigationBarHeight() + 25
        } else {
            fabPadding.floatValue = getNavigationBarHeight().toFloat() - 50
        }
    }

    ExtendedFloatingActionButton(
        modifier = with(LocalDensity.current) {
            modifier
                .offset(y = fabPadding.floatValue.toDp())
                .bounceClick()
                .border(1.dp, Color.Gray, RoundedShapes.large)
        },
        onClick = onClick,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedShapes.large,
        elevation = FloatingActionButtonDefaults.elevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                start = paddingSize,
                end = paddingSize
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (text.isNotBlank()) {
                Text(text = text)
                Spacer(Modifier.width(8.dp))
            }
            icon()
        }
    }
}
