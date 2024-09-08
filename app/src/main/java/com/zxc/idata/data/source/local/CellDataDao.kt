package com.zxc.idata.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CellDataDao {

    @Upsert
    suspend fun upsert(cellData: LocalCellData)

    @Upsert
    suspend fun upsertBatch(cellDataList: List<LocalCellData>)

    @Query("UPDATE idata_cell_data SET value = :updateTs WHERE table_id = :tableId AND column_id = :columnId AND row_id = :rowId")
    suspend fun updateUpdateTsCellById(tableId: Long, columnId: Long, rowId: Long, updateTs: String)

    @Query("UPDATE idata_cell_data SET value = :updateTs WHERE table_id = :tableId AND column_id = :columnId")
    suspend fun updateUpdateTsCellsById(tableId: Long, columnId: Long, updateTs: String)

    @Query("SELECT * FROM idata_cell_data where column_id = :columnId")
    fun observeAllByColumnId(columnId: Long): Flow<List<LocalCellData>>

    @Query("SELECT * FROM idata_cell_data where table_id = :tableId")
    fun observeAllByTableId(tableId: Long): Flow<List<LocalCellData>>

    @Query("DELETE FROM idata_cell_data WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM idata_cell_data WHERE column_id = :columnId")
    suspend fun deleteByColumnId(columnId: Long)

    @Query("DELETE FROM idata_cell_data WHERE row_id = :rowId")
    suspend fun deleteByRowId(rowId: Long)

    @Query("DELETE FROM idata_cell_data WHERE row_id in (:rowIdList)")
    suspend fun deleteByRowIdList(rowIdList: List<Long>)

    @Query("DELETE FROM idata_cell_data WHERE table_id = :tableId")
    suspend fun deleteByTableId(tableId: Long)

}