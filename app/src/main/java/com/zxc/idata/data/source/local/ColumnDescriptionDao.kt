package com.zxc.idata.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnDescriptionDao {
    @Upsert
    suspend fun upsert(columnDescription: LocalColumnDescription): Long

    @Upsert
    suspend fun batchUpsert(columnDescriptionList: List<LocalColumnDescription>): List<Long>

    @Query("SELECT * FROM idata_column_description where table_id = :tableId")
    fun observeAllByTableId(tableId: Long): Flow<List<LocalColumnDescription>>

    @Query("DELETE FROM idata_column_description WHERE id = :id")
    suspend fun deleteById(id: Long)
}