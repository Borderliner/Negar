package meshki.studio.negarname.ui.element

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.Note

@Composable
fun SingleNote(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerRadius: Dp = 30.dp,
    onDelete: () -> Unit,
    onPin: () -> Unit = {},
    isRtl: Boolean = false,
){
    val shouldScale = remember {
        mutableFloatStateOf(if (isRtl) 1f else -1f)
    }
    Box(
        modifier = modifier
    ){
        Canvas(modifier = Modifier.matchParentSize().scale(shouldScale.floatValue, 1f)){
            val clipPath = Path().apply {
                lineTo(size.width - cutCornerRadius.toPx(), 0f)
                lineTo(size.width, cutCornerRadius.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath){
                drawRoundRect(
                    color = Color(note.color),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
                drawRoundRect(
                    color = Color(
                        ColorUtils.blendARGB(note.color, 0x00000, 0.2f)
                    ),
                    topLeft = Offset(size.width - cutCornerRadius.toPx(), -100f),
                    size = Size(cutCornerRadius.toPx()+100f, cutCornerRadius.toPx()+100f),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 32.dp, top = 8.dp, bottom = 16.dp)
        ){
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onPin,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.vec_favorite),
                contentDescription = "",
                tint = if (note.pinned) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}