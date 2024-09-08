package com.zxc.idata.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zxc.idata.data.model.FileDescription

@Entity(tableName = "idata_file_description")
data class LocalFileDescription(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("parent_id") val parentId: Long,
    @ColumnInfo("pinned") val pinned: Int,
    @ColumnInfo("create_ts") val createTs: Long,
    @ColumnInfo("update_ts") val updateTs: Long,
)

fun LocalFileDescription.toExternal() = FileDescription(
    id = id,
    name = name,
    type = type,
    parentId = parentId,
    pinned = pinned,
    createTs = createTs,
    updateTs = updateTs
)

fun List<LocalFileDescription>.toExternal() = map { it.toExternal() }