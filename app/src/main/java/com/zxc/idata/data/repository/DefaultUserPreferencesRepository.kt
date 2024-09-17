package com.zxc.idata.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zxc.idata.enums.Language
import com.zxc.idata.enums.OrderType
import com.zxc.idata.enums.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferences(
    val sortOrder: OrderType = OrderType.DESC,
    val sortType: SortType = SortType.UPDATE_TS,
    val language: Language = Language.EN_US,
)

private object PreferencesKeys {
    val SORT_ORDER = stringPreferencesKey("sort_order")
    val SORT_TYPE = stringPreferencesKey("sort_type")
    val LANGUAGE = stringPreferencesKey("language")
}

class DefaultUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override fun getUserPreferencesFlow(): Flow<UserPreferences> {
        val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
            .map { preferences ->
                val sortOrder = preferences[PreferencesKeys.SORT_ORDER] ?: "DESC"
                val sortType = preferences[PreferencesKeys.SORT_TYPE] ?: "UPDATE_TS"
                val language = preferences[PreferencesKeys.LANGUAGE] ?: "EN_US"
                UserPreferences(
                    sortOrder = OrderType.valueOf(sortOrder),
                    sortType = SortType.valueOf(sortType),
                    language = Language.valueOf(language),
                )
            }
        return userPreferencesFlow
    }

    override suspend fun updateSortOrder(sortOrder: OrderType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    override suspend fun updateSortType(sortType: SortType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_TYPE] = sortType.name
        }
    }

    override suspend fun updateLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }
}