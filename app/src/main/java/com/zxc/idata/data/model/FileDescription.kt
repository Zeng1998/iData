package com.zxc.idata.data.model

import com.zxc.idata.data.source.local.LocalFileDescription

data class FileDescription(
    val id: Long = 0,
    val name: String,
    val type: String,
    val parentId: Long,
    val pinned: Int,
    val createTs: Long,
    val updateTs: Long,
)

fun FileDescription.toLocal() = LocalFileDescription(
    id = id,
    name = name,
    type = type,
    parentId = parentId,
    pinned = pinned,
    createTs = createTs,
    updateTs = updateTs
)