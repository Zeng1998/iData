package com.zxc.idata.data.repository

import com.zxc.idata.enums.Language
import com.zxc.idata.enums.OrderType
import com.zxc.idata.enums.SortType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferencesFlow(): Flow<UserPreferences>
    suspend fun updateSortOrder(sortOrder: OrderType)
    suspend fun updateSortType(sortType: SortType)
    suspend fun updateLanguage(language: Language)
}