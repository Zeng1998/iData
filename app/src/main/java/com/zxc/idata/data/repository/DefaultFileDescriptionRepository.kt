package com.zxc.idata.data.repository

import com.zxc.idata.data.model.FileDescription
import com.zxc.idata.data.model.toLocal
import com.zxc.idata.data.source.local.FileDescriptionDao
import com.zxc.idata.data.source.local.toExternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFileDescriptionRepository @Inject constructor(
    private val localDatasource: FileDescriptionDao
) : FileDescriptionRepository {

    // 返回值定义为Flow之后，Room能够去监听数据库的变化（增删改）
    override fun getFileDescriptionStream(): Flow<List<FileDescription>> {
        return localDatasource.observeAll().map { it.toExternal() }
    }

    override suspend fun upsertFileDescription(fileDescription: FileDescription):Long {
        return localDatasource.upsert(fileDescription.toLocal())
    }

    override suspend fun deleteFileDescriptionById(id: Long) {
        localDatasource.deleteById(id)
    }

    override suspend fun updateFileDescriptionUpdateTsById(id: Long, updateTs: Long) {
        localDatasource.updateUpdateTsById(id, updateTs)
    }

}