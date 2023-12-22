package meshki.studio.negarname.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class NoteShadowShape(
    private val cutCornerRadius: Float,
    private val isRtl: Boolean
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val roundOffset = 30f
        val pathRtl = Path().apply {
            moveTo(roundOffset, 0f)
            lineTo(size.width - cutCornerRadius, 0f)
            lineTo(size.width, cutCornerRadius)
            lineTo(size.width, size.height - roundOffset)
            lineTo(size.width - roundOffset, size.height)
            lineTo(roundOffset, size.height)
            lineTo(0f, size.height - roundOffset)
            lineTo(0f, roundOffset)
            lineTo(roundOffset, 0f)
            close()
        }
        val path = Path().apply {
            moveTo(cutCornerRadius, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            lineTo(0f, cutCornerRadius)
            lineTo(cutCornerRadius, 0f)
            close()
        }
        return if (isRtl) {
            Outline.Generic(path = pathRtl)
        } else {
            Outline.Generic(path = path)
        }
    }
}

class TriangleShape: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // Moves to top center position
            moveTo(size.width / 2f, 0f)
            // Add line to bottom right corner
            lineTo(size.width, size.height)
            // Add line to bottom left corner
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path = path)
    }
}
val RoundedShapes = Shapes(
    small = RoundedCornerShape(2.dp),
    medium = RoundedCornerShape(5.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(16.dp)
)
