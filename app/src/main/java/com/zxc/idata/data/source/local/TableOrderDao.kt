package com.zxc.idata.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TableOrderDao {
    @Upsert
    suspend fun upsert(tableOrder: LocalTableOrder): Long

    @Upsert
    suspend fun batchUpsert(tableOrderList: List<LocalTableOrder>): List<Long>

    @Query("SELECT * FROM idata_table_order WHERE table_id = :tableId")
    fun getAllByTableId(tableId: Long): Flow<List<LocalTableOrder>>

    @Query("DELETE FROM idata_table_order WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM idata_table_order WHERE table_id = :tableId")
    suspend fun deleteByTableId(tableId: Long)
}