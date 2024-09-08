package com.zxc.idata.data.repository

import com.zxc.idata.data.model.TableOrder
import kotlinx.coroutines.flow.Flow

interface TableOrderRepository {
    suspend fun upsertTableOrder(tableOrder: TableOrder): Long
    suspend fun batchUpsertTableOrder(tableOrderList: List<TableOrder>): List<Long>
    fun getAllTableOrderByTableId(tableId: Long): Flow<List<TableOrder>>
    suspend fun deleteTableOrderById(id: Long)
    suspend fun deleteTableOrderByTableId(tableId: Long)
}