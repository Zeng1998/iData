package com.zxc.idata.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zxc.idata.data.model.TableOrder

@Entity(tableName = "idata_table_order")
data class LocalTableOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("table_id") val tableId: Long,
    @ColumnInfo("column_id") val columnId: Long,
    @ColumnInfo("order_type") val orderType: String,
    @ColumnInfo("checked") val checked: Int,
    @ColumnInfo("rank") val rank: Int,
    @ColumnInfo("create_ts") val createTs: Long,
    @ColumnInfo("update_ts") val updateTs: Long,
)

fun LocalTableOrder.toExternal() = TableOrder(
    id = id,
    tableId = tableId,
    columnId = columnId,
    orderType = orderType,
    checked = checked,
    rank = rank,
    createTs = createTs,
    updateTs = updateTs,
)

fun List<LocalTableOrder>.toExternal() = map { it.toExternal() }