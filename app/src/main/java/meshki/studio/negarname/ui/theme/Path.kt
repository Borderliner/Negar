package meshki.studio.negarname.ui.theme

import androidx.compose.ui.graphics.Path

fun roundedRect(
    left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float,
    tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
): Path {
    var rx = rx
    var ry = ry
    val path = Path()
    if (rx < 0) rx = 0f
    if (ry < 0) ry = 0f
    val width = right - left
    val height = bottom - top
    if (rx > width / 2) rx = width / 2
    if (ry > height / 2) ry = height / 2
    val widthMinusCorners = width - 2 * rx
    val heightMinusCorners = height - 2 * ry
    path.moveTo(right, top + ry)
    if (tr) path.relativeQuadraticBezierTo(0f, -ry, -rx, -ry) //top-right corner
    else {
        path.relativeLineTo(0f, -ry)
        path.relativeLineTo(-rx, 0f)
    }
    path.relativeLineTo(-widthMinusCorners, 0f)
    if (tl) path.relativeQuadraticBezierTo(-rx, 0f, -rx, ry) //top-left corner
    else {
        path.relativeLineTo(-rx, 0f)
        path.relativeLineTo(0f, ry)
    }
    path.relativeLineTo(0f, heightMinusCorners)
    if (bl) path.relativeQuadraticBezierTo(0f, ry, rx, ry) //bottom-left corner
    else {
        path.relativeLineTo(0f, ry)
        path.relativeLineTo(rx, 0f)
    }
    path.relativeLineTo(widthMinusCorners, 0f)
    if (br) path.relativeQuadraticBezierTo(rx, 0f, rx, -ry) //bottom-right corner
    else {
        path.relativeLineTo(rx, 0f)
        path.relativeLineTo(0f, -ry)
    }
    path.relativeLineTo(0f, -heightMinusCorners)
    path.close() //Given close, last lineto can be removed.
    return path
}
