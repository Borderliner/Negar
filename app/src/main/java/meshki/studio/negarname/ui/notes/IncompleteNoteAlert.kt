package meshki.studio.negarname.ui.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meshki.studio.negarname.R
import meshki.studio.negarname.ui.theme.PastelGreen
import meshki.studio.negarname.ui.theme.PastelRed

@Composable
fun IncompleteNoteAlert(
    onDismissRequest: () -> Unit = {},
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        icon = {
            Icon(
                Icons.Filled.Warning,
                contentDescription = stringResource(R.string.warning),
                modifier = Modifier.size(48.dp)
            )
        },

        title = {
            Text(
                text = stringResource(R.string.incomplete_note_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.incomplete_note_text),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen,
                    contentColor = Color.Black
                ),
                onClick = { onConfirm() }
            ) {
                Text(
                    text = stringResource(R.string.continue_job),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.padding(horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelRed,
                    contentColor = Color.Black
                ),
                onClick = {
                    onCancel()
                }
            ) {
                Text(
                    text = stringResource(R.string.return_job),
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        textContentColor = MaterialTheme.colorScheme.onBackground
    )
}