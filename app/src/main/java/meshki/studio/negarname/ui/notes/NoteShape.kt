package meshki.studio.negarname.ui.notes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import meshki.studio.negarname.ui.theme.roundedRect

class NoteShape(
    private val cutCornerRadius: Float,
    private val isRtl: Boolean
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = notePath(size, 30f, cutCornerRadius, isRtl)
        return Outline.Generic(path = path)
    }
}

fun notePath(size: Size, cornerRadius: Float, cutCornerRadius: Float, isRtl: Boolean): Path {
    val roundedPath = roundedRect(
        0f, 0f, size.width, size.height, cornerRadius, cornerRadius,
        tl = true,
        tr = true,
        br = true,
        bl = true
    )

    val path: Path = if (isRtl) {
        Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width - cutCornerRadius, 0f)
            lineTo(size.width, cutCornerRadius)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            lineTo(0f, 0f)
            close()
        }
    } else {
        Path().apply {
            moveTo(cutCornerRadius, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            lineTo(0f, cutCornerRadius)
            lineTo(cutCornerRadius, 0f)
            close()
        }
    }

    val finalPath = Path().apply {
        op(roundedPath, path, PathOperation.Intersect)
    }

    return finalPath
}