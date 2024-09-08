package com.zxc.idata.data.repository

import com.zxc.idata.data.model.CellData
import com.zxc.idata.data.model.toLocal
import com.zxc.idata.data.source.local.CellDataDao
import com.zxc.idata.data.source.local.toExternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultCellDataRepository @Inject constructor(
    private val localDatasource: CellDataDao
) : CellDataRepository {

    override fun getCellDataStreamByColumnId(columnId: Long): Flow<List<CellData>> {
        return localDatasource.observeAllByColumnId(columnId).map { it.toExternal() }
    }

    override fun getCellDataStreamByTableId(tableId: Long): Flow<List<CellData>> {
        return localDatasource.observeAllByTableId(tableId).map { it.toExternal() }
    }

    override suspend fun upsertCellData(cellData: CellData) {
        localDatasource.upsert(cellData.toLocal())
    }

    override suspend fun batchUpsertCellData(cellDataList: List<CellData>) {
        localDatasource.upsertBatch(cellDataList.toLocal())
    }

    override suspend fun updateCellDataUpdateTsById(
        tableId: Long,
        columnId: Long,
        rowId: Long,
        updateTs: String
    ) {
        localDatasource.updateUpdateTsCellById(tableId, columnId, rowId, updateTs)
    }

    override suspend fun batchUpdateCellDataUpdateTsById(
        tableId: Long,
        columnId: Long,
        updateTs: String
    ) {
        localDatasource.updateUpdateTsCellsById(tableId, columnId, updateTs)
    }

    override suspend fun deleteCellDataById(id: Long) {
        localDatasource.deleteById(id)
    }

    override suspend fun deleteCellDataByColumnId(columnId: Long) {
        localDatasource.deleteByColumnId(columnId)
    }

    override suspend fun deleteCellDataByRowId(rowId: Long) {
        localDatasource.deleteByRowId(rowId)
    }

    override suspend fun deleteCellDataByRowIdList(rowIdList: List<Long>) {
        localDatasource.deleteByRowIdList(rowIdList)
    }

    override suspend fun deleteCellDataByTableId(tableId: Long) {
        localDatasource.deleteByTableId(tableId)
    }
}