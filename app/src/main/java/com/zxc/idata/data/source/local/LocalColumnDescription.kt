package com.zxc.idata.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zxc.idata.data.model.ColumnDescription

@Entity(tableName = "idata_column_description")
data class LocalColumnDescription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("table_id") val tableId: Long,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("width") val width: Int,
    @ColumnInfo("display") val display: Int,
    @ColumnInfo("ext") val ext: String,
    @ColumnInfo("rank") val rank: Int,
    @ColumnInfo("create_ts") val createTs: Long,
    @ColumnInfo("update_ts") val updateTs: Long,
)

fun LocalColumnDescription.toExternal() = ColumnDescription(
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

fun List<LocalColumnDescription>.toExternal() = map { it.toExternal() }