package meshki.studio.negarname.ui.element

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.util.bounceClick
import meshki.studio.negarname.util.getNavigationBarHeight

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

    ExtendedFloatingActionButton(
        modifier = with(LocalDensity.current) {
            modifier
                .offset(y = (getNavigationBarHeight()).toDp())
                .bounceClick()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        },
        onClick = onClick,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp),
        elevation = FloatingActionButtonDefaults.elevation(2.dp)
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
