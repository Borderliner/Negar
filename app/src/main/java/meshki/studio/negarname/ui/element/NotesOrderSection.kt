package meshki.studio.negarname.ui.element

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import meshki.studio.negarname.entity.OrderBy
import meshki.studio.negarname.entity.OrderType
import meshki.studio.negarname.R

@Composable
fun NotesOrderSection(
    modifier: Modifier = Modifier,
    topPadding: Dp,
    caretOffset: Float,
    color: Color,
    orderBy: OrderBy = OrderBy.Date(OrderType.Descending),
    onOrderChange: (OrderBy) -> Unit
) {
    PopupSection(
        modifier = modifier,
        topPadding = topPadding,
        offsetPercent = caretOffset,
        color = color
    ) {
        Column {
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