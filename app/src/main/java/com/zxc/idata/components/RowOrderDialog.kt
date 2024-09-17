package com.zxc.idata.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zxc.idata.R
import com.zxc.idata.data.model.ColumnDescription
import com.zxc.idata.data.model.TableOrder
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.OrderType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class OrderItem(
    // column info
    val column: ColumnDescription,
    // order info
    val order: TableOrder,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowOrderDialog(
    orderList: List<TableOrder>,
    columnList: List<ColumnDescription>,
    onConfirm: (List<TableOrder>) -> Unit,
    onCancel: () -> Unit,
    getResourceString: (Int) -> String,
) {
    val orderItems = remember {
        orderList
            .map { order ->
                OrderItem(
                    column = columnList.first { it.id == order.columnId },
                    order = order,
                )
            }
            // nb
            .sortedWith(
                compareBy<OrderItem> { -it.order.checked }
                    .thenBy { it.order.rank }
                    .thenBy { it.column.id }
            )
            .toMutableStateList()
    }
    CommonDialog(
        title = getResourceString(R.string.row_order),
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = {
            val newOrderList = orderItems
                .mapIndexed { idx, item ->
                    item.order.copy(
                        rank = idx + 1,
                        checked = item.order.checked,
                    )
                }
            onConfirm(newOrderList)
        },
        getResourceString = getResourceString
    ) {
        val lazyListState = rememberLazyListState()
        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            orderItems.add(to.index, orderItems.removeAt(from.index))
        }
        LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(orderItems, key = { _, item -> item.column.id }) { index, item ->
                ReorderableItem(reorderableLazyListState, key = item.column.id) { isDragging ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = if (isDragging) MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5f
                            )
                            else MaterialTheme.colorScheme.surfaceContainer,
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            Checkbox(
                                modifier = Modifier
                                    .width(40.dp),
                                checked = item.order.checked == 1,
                                onCheckedChange = {
                                    orderItems[index] =
                                        item.copy(order = item.order.copy(checked = if (it) 1 else 0))
                                },
                            )
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    painter = painterResource(id = ColumnType.valueOf(item.column.type).iconId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(24.dp),
                                )
                                Text(text = item.column.name)
                            }
                            Row {
                                IconButton(onClick = {
                                    orderItems[index] =
                                        item.copy(
                                            order = item.order.copy(
                                                orderType =
                                                if (item.order.orderType == OrderType.ASC.name) OrderType.DESC.name
                                                else OrderType.ASC.name
                                            )
                                        )
                                }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (item.order.orderType == OrderType.ASC.name) R.drawable.arrowdown
                                            else R.drawable.arrowup
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                IconButton(modifier = Modifier.draggableHandle(), onClick = {}) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.menu),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}