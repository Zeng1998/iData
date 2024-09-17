package com.zxc.idata.data.model

import com.zxc.idata.data.source.local.LocalColumnDescription

data class ColumnDescription(
    val id: Long = 0,
    val name: String,
    val tableId: Long,
    val type: String,
    val width: Int,
    val display: Int,
    val ext: String,
    val rank:Int,
    val createTs: Long,
    val updateTs: Long,
)

fun ColumnDescription.toLocal() = LocalColumnDescription(
    id = id,
    name = name,
    tableId = tableId,
    type = type,
    width = width,
    display = display,
    ext = ext,
    rank = rank,
    createTs = createTs,
    updateTs = updateTs,
)

fun List<ColumnDescription>.toLocal() = map { it.toLocal() }