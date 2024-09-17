package com.zxc.idata.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zxc.idata.data.model.CellData

@Entity(tableName = "idata_cell_data")
data class LocalCellData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("table_id") val tableId: Long,
    @ColumnInfo("column_id") val columnId: Long,
    @ColumnInfo("row_id") val rowId: Long,
    @ColumnInfo("value") val value: String,
    @ColumnInfo("create_ts") val createTs: Long,
    @ColumnInfo("update_ts") val updateTs: Long,
)

fun LocalCellData.toExternal() = CellData(
    id = id,
    tableId = tableId,
    columnId = columnId,
    rowId = rowId,
    value = value,
    createTs = createTs,
    updateTs = updateTs,
)

fun List<LocalCellData>.toExternal() = map { it.toExternal() }