package meshki.studio.negarname.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import meshki.studio.negarname.ui.theme.RoundedShapes
import meshki.studio.negarname.ui.util.extensions.bounceClick
import meshki.studio.negarname.ui.util.extensions.dpToPx
import meshki.studio.negarname.ui.util.getNavigationBarHeight
import meshki.studio.negarname.ui.util.keyboardAsState

@Composable
fun ActionButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier,
    isBottomBarVisible: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val paddingSize = 2.dp

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(isPressed) {
        if (isPressed) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val ctx = LocalContext.current

    val isKeyboardOpen by keyboardAsState()
    val fabPadding = remember {
        mutableIntStateOf(-1)
    }

    val insets: WindowInsetsCompat? =
        ViewCompat.getRootWindowInsets((LocalContext.current as Activity).window.decorView)
    //val insets2 = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(isKeyboardOpen, isBottomBarVisible) {
        // 90 is hardcoded navigation bar height
        val keyboardHeight: Int = insets!!.getInsets(WindowInsetsCompat.Type.ime()).bottom
        val bottomBarHeight: Int = if (isBottomBarVisible) ctx.dpToPx(90) else ctx.dpToPx(1)
        val navigationBarHeight = getNavigationBarHeight()
        //Timber.tag("Action Button").i("Keyboard Height in Px: $keyboardHeight")
        //Timber.tag("Action Button").i("BottomBar Height in Px: $bottomBarHeight")
        //Timber.tag("Action Button").i("NavigationBar Height in Px: $navigationBarHeight")
        //Timber.tag("Action Button").i("Keyboard Height in Dp: ${ctx.pxToDp(keyboardHeight)}")
        //Timber.tag("Action Button").i("BottomBar Height in Dp: ${ctx.pxToDp(bottomBarHeight)}")
        //Timber.tag("Action Button").i("NavigationBar Height in Dp: ${ctx.pxToDp(navigationBarHeight)}")
        if (isKeyboardOpen) {
            fabPadding.intValue = (keyboardHeight - 20) * -1
        } else {
            fabPadding.intValue = bottomBarHeight * -1
        }
    }

    val opacity = 0.93f
    ExtendedFloatingActionButton(
        modifier = //with(LocalDensity.current) {
        modifier
            //.offset(y = fabPadding.intValue.toDp())
            .offset {
                IntOffset(x = 0, y = fabPadding.intValue)
            }
            .bounceClick()
            //.navigationBarsPadding()
            //.imePadding()
            //.padding(bottom = if (isBottomBarVisible.value) 0.dp else 0.dp)
            .padding(bottom = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedShapes.large),
        //},
        onClick = onClick,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedShapes.large,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
        //elevation = FloatingActionButtonDefaults.elevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(
                    start = paddingSize,
                    end = paddingSize
                ),
                //.background(Color.Transparent),
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
