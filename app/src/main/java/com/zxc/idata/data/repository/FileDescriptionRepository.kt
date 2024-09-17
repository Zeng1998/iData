package com.zxc.idata.data.repository

import com.zxc.idata.data.model.FileDescription
import kotlinx.coroutines.flow.Flow

interface FileDescriptionRepository {
    fun getFileDescriptionStream(): Flow<List<FileDescription>>
    suspend fun upsertFileDescription(fileDescription: FileDescription):Long
    suspend fun deleteFileDescriptionById(id: Long)
    suspend fun updateFileDescriptionUpdateTsById(id: Long, updateTs: Long)
}