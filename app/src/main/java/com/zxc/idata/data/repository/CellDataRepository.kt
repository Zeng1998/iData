package com.zxc.idata.data.repository

import com.zxc.idata.data.model.CellData
import kotlinx.coroutines.flow.Flow


interface CellDataRepository {
    fun getCellDataStreamByColumnId(columnId: Long): Flow<List<CellData>>
    fun getCellDataStreamByTableId(tableId: Long): Flow<List<CellData>>
    suspend fun upsertCellData(cellData: CellData)
    suspend fun batchUpsertCellData(cellDataList: List<CellData>)
    suspend fun updateCellDataUpdateTsById(tableId: Long, columnId: Long, rowId: Long, updateTs: String)
    suspend fun batchUpdateCellDataUpdateTsById(tableId: Long, columnId: Long, updateTs: String)
    suspend fun deleteCellDataById(id: Long)
    suspend fun deleteCellDataByColumnId(columnId: Long)
    suspend fun deleteCellDataByRowId(rowId: Long)
    suspend fun deleteCellDataByRowIdList(rowIdList: List<Long>)
    suspend fun deleteCellDataByTableId(tableId: Long)
}