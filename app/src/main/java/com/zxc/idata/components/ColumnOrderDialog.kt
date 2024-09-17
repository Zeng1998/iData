package com.zxc.idata.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.zxc.idata.enums.ColumnType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnOrderDialog(
    columns: List<ColumnDescription>,
    onConfirm: (List<ColumnDescription>) -> Unit,
    onCancel: () -> Unit,
    getResourceString: (Int) -> String,
) {
    val list = remember { columns.toMutableStateList() }
    CommonDialog(
        title = getResourceString(R.string.column_order),
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = {
            onConfirm(list.mapIndexed { idx, item -> item.copy(rank = idx + 1) })
        },
        getResourceString = getResourceString
    ) {
        val lazyListState = rememberLazyListState()
        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            list.add(to.index, list.removeAt(from.index))
        }
        LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(list, key = { _, item -> item.id }) { _, item ->
                ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->
                    Card(
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = ColumnType.valueOf(item.type).iconId),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(24.dp),
                            )
                            Text(text = item.name, modifier = Modifier.weight(1f))
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