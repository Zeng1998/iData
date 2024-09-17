package com.zxc.idata.data.model

import com.zxc.idata.data.source.local.LocalTableOrder

data class TableOrder(
    val id: Long = 0,
    val tableId: Long,
    val columnId: Long,
    val orderType: String,
    val checked: Int,
    val rank: Int,
    val createTs: Long,
    val updateTs: Long,
)

fun TableOrder.toLocal() = LocalTableOrder(
    id = id,
    tableId = tableId,
    columnId = columnId,
    orderType = orderType,
    checked = checked,
    rank = rank,
    createTs = createTs,
    updateTs = updateTs,
)

fun List<TableOrder>.toLocal() = map { it.toLocal() }