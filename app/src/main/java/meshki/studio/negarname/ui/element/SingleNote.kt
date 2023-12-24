package meshki.studio.negarname.ui.element

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import meshki.studio.negarname.R
import meshki.studio.negarname.entity.Note
import meshki.studio.negarname.ui.theme.NoteShape
import meshki.studio.negarname.ui.theme.notePath

@Composable
fun SingleNote(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    cutCornerRadius: Dp = 30.dp,
    isRtl: Boolean = false,
    onTap: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit = {},
) {
    val cRad = with(LocalDensity.current) {
        cutCornerRadius.toPx()
    }

    val noteShape = remember { mutableStateOf(NoteShape(cRad, isRtl)) }

    LaunchedEffect(isRtl) {
        noteShape.value = NoteShape(cRad, isRtl)
    }

    Box(
        modifier = modifier
    ) {
        val textColor = Color.Black.copy(0.90f)
        Box(
            Modifier
                .matchParentSize()
                .shadow(elevation = 6.dp, shape = noteShape.value)
        ) {}
        Canvas(modifier = Modifier
            .matchParentSize()
            .clip(noteShape.value)
            .clickable {
                onTap()
            }) {
            val clipPath = notePath(size, cornerRadius.toPx(), cutCornerRadius.toPx(), isRtl)
            val foldOffset = if (isRtl) {
                Offset(size.width - cutCornerRadius.toPx(), -100f)
            } else {
                Offset(cutCornerRadius.toPx() * -1.20f, -100f)
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = Color(note.color),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
                drawRoundRect(
                    color = Color(
                        ColorUtils.blendARGB(note.color, 0x00000, 0.2f)
                    ),
                    topLeft = foldOffset,
                    size = Size(cutCornerRadius.toPx() + 100f, cutCornerRadius.toPx() + 100f),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 32.dp, top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onPin,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(if (note.pinned) R.drawable.vec_favorite_fill else R.drawable.vec_favorite),
                contentDescription = "",
                tint = if (note.pinned) Color.Red.copy(alpha = 0.8f) else textColor
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = textColor
            )
        }
    }
}