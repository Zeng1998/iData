package com.zxc.idata.data.repository

import com.zxc.idata.data.model.ColumnDescription
import kotlinx.coroutines.flow.Flow


interface ColumnDescriptionRepository {
    fun getColumnDescriptionStreamByTableId(tableId: Long): Flow<List<ColumnDescription>>
    suspend fun upsertColumnDescription(columnDescription: ColumnDescription): Long
    suspend fun batchUpsertColumnDescription(columnDescriptionList: List<ColumnDescription>): List<Long>
    suspend fun deleteColumnDescriptionById(id: Long)
}