package meshki.studio.negarname.ui.element

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.R

@Composable
fun NotesSearchSection(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit
){
    val searchQuery = remember { mutableStateOf("") }
    val visibleHint = remember { mutableStateOf(true) }
    Box {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                HintedTextField(
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp))
                        .padding(10.dp)
                        .padding(bottom = 4.dp),
                    text = searchQuery.value,
                    icon = R.drawable.vec_navigate_next,
                    hint = "جستجو",
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