package com.zxc.idata.data.model

import com.zxc.idata.data.source.local.LocalCellData
import com.zxc.idata.enums.OrderType


data class CellData(
    val id: Long = 0,
    val tableId: Long,
    val columnId: Long,
    val rowId: Long,
    val value: String,
    val createTs: Long,
    val updateTs: Long,
)

fun CellData.toLocal() = LocalCellData(
    id = id,
    tableId = tableId,
    columnId = columnId,
    rowId = rowId,
    value = value,
    createTs = createTs,
    updateTs = updateTs,
)

fun List<CellData>.toLocal() = map { it.toLocal() }

fun List<CellData>.sortAndGroupToRow(
    columnId: Long,
    orderType: OrderType
): Map<Long, List<CellData>> {
    return this
        .groupBy { it.rowId }
        .toMap()
        .toList()
        .let {
            if (orderType == OrderType.ASC) {
                it.sortedBy { (_, value) ->
                    value.first { cell -> cell.columnId == columnId }.value
                }
            } else {
                it.sortedByDescending { (_, value) ->
                    value.first { it.columnId == columnId }.value
                }
            }
        }.toMap()
}