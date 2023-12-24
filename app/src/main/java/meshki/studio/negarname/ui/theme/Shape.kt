package meshki.studio.negarname.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

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

class TriangleShape : Shape {
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
