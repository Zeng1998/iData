package com.zxc.idata.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxc.idata.data.repository.UserPreferences
import com.zxc.idata.data.repository.UserPreferencesRepository
import com.zxc.idata.enums.Language
import com.zxc.idata.enums.OrderType
import com.zxc.idata.enums.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val openBottomSheet: Boolean = false,
    val selectingPreferenceKey: String = "",
    val optionIds: List<Int> = emptyList(),
    val optionNames: List<String> = emptyList(),
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userPreferences = userPreferencesRepository.getUserPreferencesFlow()
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(),
        initialValue = UserPreferences()
    )

    fun updatePreference(value: String) {
        viewModelScope.launch {
            when (_uiState.value.selectingPreferenceKey) {
                "language" -> userPreferencesRepository.updateLanguage(Language.valueOf(value))
                "sortOrder" -> userPreferencesRepository.updateSortOrder(OrderType.valueOf(value))
                "sortType" -> userPreferencesRepository.updateSortType(SortType.valueOf(value))
            }
        }
    }

    fun setSelectingPreferenceKey(key: String) {
        _uiState.update { it.copy(selectingPreferenceKey = key) }
    }

    fun setOptionIds(optionIds: List<Int>) {
        _uiState.update { it.copy(optionIds = optionIds) }
    }

    fun setOptionNames(optionNames: List<String>) {
        _uiState.update { it.copy(optionNames = optionNames) }
    }

    fun openBottomSheet() {
        _uiState.update { it.copy(openBottomSheet = true) }
    }

    fun closeBottomSheet() {
        _uiState.update { it.copy(openBottomSheet = false) }
    }
}