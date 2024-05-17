package meshki.studio.negarname.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelOrange


@Composable
fun AboutDialog(state: MutableState<Boolean>) {
    var moreInfo by remember { mutableStateOf(false) }


    if (state.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                state.value = false
            },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true)
                ) {
                    // create custom title
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier.padding(bottom = 12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                            )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (moreInfo) {
                        Text(
                            text = stringResource(R.string.developer_name),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.developer_email),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.about_text),
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.about_text_2),
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
            confirmButton = {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(0.47f),
                    colors = ButtonDefaults.elevatedButtonColors(
                        contentColor = if (!moreInfo) MaterialTheme.colorScheme.onTertiaryContainer else Color.Black.copy(alpha = 0.9f),
                        containerColor = if (!moreInfo) MaterialTheme.colorScheme.tertiaryContainer else PastelOrange
                    ),
                    onClick = {
                        moreInfo = !moreInfo
                    }) {
                    Text(
                        text = if (!moreInfo) stringResource(R.string.more) else stringResource(R.string.back),
                    )
                }
            },
            dismissButton = {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(0.47f),
                    colors = ButtonDefaults.elevatedButtonColors(
                        contentColor = Color.Black.copy(alpha = 0.9f),
                        containerColor = PastelGreen
                    ),
                    onClick = {
                        moreInfo = false
                        state.value = false
                    }) {
                    Text(
                        stringResource(R.string.ok),
                    )
                }
            }
        )
    }
}