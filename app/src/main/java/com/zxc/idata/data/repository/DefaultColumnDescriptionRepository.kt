package com.zxc.idata.data.repository

import com.zxc.idata.data.model.ColumnDescription
import com.zxc.idata.data.model.toLocal
import com.zxc.idata.data.source.local.ColumnDescriptionDao
import com.zxc.idata.data.source.local.toExternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultColumnDescriptionRepository @Inject constructor(
    private val localDatasource: ColumnDescriptionDao
) : ColumnDescriptionRepository {

    override fun getColumnDescriptionStreamByTableId(tableId: Long): Flow<List<ColumnDescription>> {
        return localDatasource.observeAllByTableId(tableId).map { it.toExternal() }
    }

    override suspend fun upsertColumnDescription(columnDescription: ColumnDescription): Long {
        return localDatasource.upsert(columnDescription.toLocal())
    }

    override suspend fun batchUpsertColumnDescription(columnDescriptionList: List<ColumnDescription>): List<Long> {
        return localDatasource.batchUpsert(columnDescriptionList.toLocal())
    }

    override suspend fun deleteColumnDescriptionById(id: Long) {
        localDatasource.deleteById(id)
    }
}