package com.zxc.idata.data.repository

import com.zxc.idata.data.model.TableOrder
import com.zxc.idata.data.model.toLocal
import com.zxc.idata.data.source.local.TableOrderDao
import com.zxc.idata.data.source.local.toExternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultTableOrderRepository @Inject constructor(
    private val localDatasource: TableOrderDao
) : TableOrderRepository {
    override suspend fun deleteTableOrderById(id: Long) {
        localDatasource.deleteById(id)
    }

    override suspend fun deleteTableOrderByTableId(tableId: Long) {
        localDatasource.deleteByTableId(tableId)
    }

    override fun getAllTableOrderByTableId(tableId: Long): Flow<List<TableOrder>> {
        return localDatasource.getAllByTableId(tableId).map { it.toExternal() }
    }

    override suspend fun upsertTableOrder(tableOrder: TableOrder): Long {
        return localDatasource.upsert(tableOrder.toLocal())
    }

    override suspend fun batchUpsertTableOrder(tableOrderList: List<TableOrder>): List<Long> {
        return localDatasource.batchUpsert(tableOrderList.toLocal())
    }
}