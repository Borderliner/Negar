package meshki.studio.negarname.ui.element

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.theme.RoundedShapes

@Composable
fun NotesSearchSection(
    modifier: Modifier = Modifier,
    topPadding: Dp,
    offsetPercent: Float,
    color: Color,
    onTextChange: (String) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }
    val visibleHint = remember { mutableStateOf(true) }
    SectionPopup(modifier = modifier, topPadding = topPadding, offsetPercent = offsetPercent, color = color) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                HintedTextField(
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedShapes.extraLarge)
                        .padding(10.dp),
                    text = searchQuery.value,
                    icon = R.drawable.vec_navigate_next,
                    hint = stringResource(R.string.search),
                    onValueChange = {
                        searchQuery.value = it
                        onTextChange(it)
                    },
                    onFocusChange = {
                        visibleHint.value = !it.isFocused
                    },
                    isHintVisible = visibleHint.value,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}