package com.zxc.idata.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDescriptionDao {

    @Upsert
    suspend fun upsert(fileDescription: LocalFileDescription): Long

    @Query("SELECT * FROM idata_file_description")
    fun observeAll(): Flow<List<LocalFileDescription>>

    @Query("DELETE FROM idata_file_description WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE idata_file_description SET update_ts = :updateTs WHERE id = :id")
    suspend fun updateUpdateTsById(id: Long, updateTs: Long)
}