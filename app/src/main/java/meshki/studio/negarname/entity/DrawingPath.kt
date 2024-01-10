package meshki.studio.negarname.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import com.google.gson.Gson

class DrawingPath(val path: CustomPath, val properties: PathProperties) {
    override fun toString(): String {
        return "{path:$path,properties:$properties}"
    }

    companion object {
        fun fromString(serialized: String): DrawingPath {
            val drawingPath = Gson().fromJson(serialized, DrawingPath::class.java)
            return DrawingPath(
                path = drawingPath.path,
                properties = drawingPath.properties
            )
        }
    }
}

class PathProperties(
    var strokeWidth: Float = 10f,
    var color: Color = Color.Black,
    var alpha: Float = 1f,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var eraseMode: Boolean = false
) {
    fun copy(
        strokeWidth: Float = this.strokeWidth,
        color: Color = this.color,
        alpha: Float = this.alpha,
        strokeCap: StrokeCap = this.strokeCap,
        strokeJoin: StrokeJoin = this.strokeJoin,
        eraseMode: Boolean = this.eraseMode
    ) = PathProperties(
        strokeWidth, color, alpha, strokeCap, strokeJoin, eraseMode
    )

    fun copyFrom(properties: PathProperties) {
        this.strokeWidth = properties.strokeWidth
        this.color = properties.color
        this.strokeCap = properties.strokeCap
        this.strokeJoin = properties.strokeJoin
        this.eraseMode = properties.eraseMode
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromString(serializedObject: String): PathProperties {
            return Gson().fromJson(serializedObject, PathProperties::class.java)
        }
    }
}

data class PathAction(val action: String, val points: List<Float>)

class CustomPath(val actions: MutableList<PathAction> = mutableListOf()) {
    @Transient
    private val internalPath: Path = Path()

    fun getPath() = internalPath

    init {
        draw()
    }

    fun draw() {
        if (actions.isNotEmpty() && actions.size > 3) {
            actions.forEach {
                when (it.action) {
                    "LINE_TO" -> internalPath.lineTo(it.points[0], it.points[1])
                    "MOVE_TO" -> internalPath.moveTo(it.points[0], it.points[1])
                    "QUAD_TO" -> internalPath.quadraticBezierTo(it.points[0], it.points[1], it.points[2], it.points[3])
                    else -> Unit
                }
            }
        }
    }

    fun lineTo(x: Float, y: Float) {
        actions.add(PathAction("LINE_TO", listOf(x, y)))
        internalPath.lineTo(x, y)
    }

    fun moveTo(x: Float, y: Float) {
        actions.add(PathAction("MOVE_TO", listOf(x, y)))
        internalPath.moveTo(x, y)
    }

    fun quadraticBezierTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        actions.add(PathAction("QUAD_TO", listOf(x1, y1, x2, y2)))
        internalPath.quadraticBezierTo(x1, y1, x2, y2)
    }

    fun translate(offset: Offset) {
        internalPath.translate(offset)
    }

    override fun toString(): String {
        return actions.toString()
    }
}
