package meshki.studio.negarname.ui.element

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun HintedTextField(
    modifier: Modifier = Modifier,
    text: String,
    hint: String,
    icon: Int = 0,
    hintColor: Color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
    isHintVisible: Boolean = true,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    expanded: Boolean = false,
    onValueChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit = {},
) {
    Box(
        modifier = modifier
    ) {
        val mod: Modifier = when (expanded) {
            true -> { Modifier.fillMaxSize() }
            false -> { Modifier.fillMaxWidth() }
        }

        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle.copy(MaterialTheme.colorScheme.onBackground),
            modifier = if (icon > 0) {
                mod.padding(top = 5.dp, start = 30.dp)
            } else {
                mod.padding(top = 5.dp)
            }
            .onFocusChanged {
                onFocusChange(it)
            },
        )
        if(isHintVisible) {
            Text(
                modifier = if (icon > 0) Modifier.padding(start = 32.dp, top = 3.dp) else Modifier,
                text = hint,
                style = textStyle,
                color = hintColor
            )
        }
        if (icon > 0)
            Icon(
                painterResource(icon),
                text,
                modifier = Modifier.padding(top = 5.dp, start = 5.dp)
            )
    }
}