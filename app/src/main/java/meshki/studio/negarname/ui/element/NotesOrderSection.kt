package meshki.studio.negarname.ui.element

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.R

@Composable
fun NotesOrderSection(
    modifier: Modifier = Modifier,
    topPadding: Dp,
    offsetPercent: Float,
    color: Color,
    orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
    onOrderChange: (OrderBy) -> Unit
) {
    var boxSize by remember {
        mutableStateOf(Size.Zero)
    }

    Box(
        Modifier
            .background(Color.Transparent)
            .onGloballyPositioned {
                //here u can access the parent layout coordinate size
                boxSize = it.parentLayoutCoordinates?.size?.toSize() ?: Size.Zero
            }) {
        val offsetX = with(LocalDensity.current) {
            (offsetPercent * boxSize.width).toDp()
        }
        val size = 15.dp
        Canvas(
            modifier = Modifier
                .width(size)
                .height(size*3.5f)
                .padding(top = topPadding - 14.dp)
                .offset(x = offsetX)
                .zIndex(3f)
        ) {
            val sizePx = size.toPx()
            val trianglePath = Path().apply {
                // Moves to top center position
                moveTo(sizePx / 2f, 0f)
                // Add line to bottom right corner
                lineTo(sizePx, sizePx)
                // Add line to bottom left corner
                lineTo(0f, sizePx)
            }
            clipPath(trianglePath) {
                drawPath(
                    trianglePath,
                    color = color,
                    colorFilter = ColorFilter.tint(Color.Gray.copy(0.2f), BlendMode.Darken)
                )
            }
        }
        ShadyCard(
            Modifier
                .padding(horizontal = 8.dp)
                .padding(top = topPadding),
            color = color,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = modifier
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        text = stringResource(R.string.title),
                        checked = orderBy is OrderBy.Title,
                        onCheck = { onOrderChange(OrderBy.Title(orderBy.orderType)) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        text = stringResource(R.string.date),
                        checked = orderBy is OrderBy.Date,
                        onCheck = { onOrderChange(OrderBy.Date(orderBy.orderType)) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        text = stringResource(R.string.color),
                        checked = orderBy is OrderBy.Color,
                        onCheck = { onOrderChange(OrderBy.Color(orderBy.orderType)) }
                    )
                }
                Spacer(modifier = Modifier.height(0.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        text = stringResource(R.string.ascending),
                        checked = orderBy.orderType is OrderType.Ascending,
                        onCheck = { onOrderChange(orderBy.getType(OrderType.Ascending)) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        text = stringResource(R.string.descending),
                        checked = orderBy.orderType is OrderType.Descending,
                        onCheck = { onOrderChange(orderBy.getType(OrderType.Descending)) }
                    )
                }
            }
        }
    }
}