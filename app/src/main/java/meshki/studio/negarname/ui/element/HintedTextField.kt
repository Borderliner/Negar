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
    isHintVisible: Boolean,
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
                mod.padding(top = 4.dp, start = 30.dp)
            } else {
                mod.padding(top = 4.dp)
            }
            .onFocusChanged {
                onFocusChange(it)
            },
        )
        if(isHintVisible) {
            val mod2 = if (icon > 0) Modifier.padding(start = 32.dp) else Modifier
            Text(
                modifier = mod2.offset(0.dp, 4.dp),
                text = hint,
                style = textStyle,
                color = hintColor
            )
        }
        if (icon > 0)
            Icon(
                painterResource(icon),
                text,
                modifier = Modifier.padding(start = 5.dp)
            )
    }
}