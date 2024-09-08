package com.zxc.idata.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxc.idata.data.model.ColumnDescription
import com.zxc.idata.data.model.FileDescription
import com.zxc.idata.data.model.TableOrder
import com.zxc.idata.data.repository.ColumnDescriptionRepository
import com.zxc.idata.data.repository.FileDescriptionRepository
import com.zxc.idata.data.repository.TableOrderRepository
import com.zxc.idata.data.repository.UserPreferencesRepository
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.DateTimeFormat
import com.zxc.idata.enums.FileDescriptionType
import com.zxc.idata.enums.Language
import com.zxc.idata.enums.OrderType
import com.zxc.idata.enums.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val path: List<FileDescription> = arrayListOf(
        FileDescription(-1, "Home", FileDescriptionType.FOLDER.name, -2, 0, -1, -1)
    ),
    val openAddDialog: Boolean = false,
    val openRenameDialog: Boolean = false,
    val openDeleteDialog: Boolean = false,
    val openFabMenus: Boolean = false,
    val isCompleted: Boolean = false,
    val id: Long = -1,
    val name: String = "",
    val type: FileDescriptionType = FileDescriptionType.FOLDER,
    val updatingItem: FileDescription? = null,
    val searchText: String = "",
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val fileDescriptionRepository: FileDescriptionRepository,
    private val columnDescriptionRepository: ColumnDescriptionRepository,
    private val tableOrderRepository: TableOrderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _fileDescriptionItemsFlow = fileDescriptionRepository.getFileDescriptionStream()
    private val _userPreferencesFlow = userPreferencesRepository.getUserPreferencesFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortTypePreference = _userPreferencesFlow.mapLatest { it.sortType }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = SortType.UPDATE_TS,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortOrderPreference = _userPreferencesFlow.mapLatest { it.sortOrder }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = OrderType.DESC,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val languagePreference = _userPreferencesFlow.mapLatest { it.language }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = Language.EN_US,
        )


    // 当这两个流/state有任意一个发生改变，就会触发这个state的更新
    val filteredFileDescriptionItems: StateFlow<List<FileDescription>> = combine(
        _fileDescriptionItemsFlow,
        _uiState,
        _userPreferencesFlow
    )
    { fileDescriptionItems, uiState, userPreferences ->
        setCompleted(false)
        var showItems = fileDescriptionItems
        showItems = showItems.filter { it.parentId == uiState.path.last().id }
        if (uiState.searchText.isNotEmpty()) {
            showItems = showItems.filter { it.name.contains(uiState.searchText) }
        }
        val itemSortFn = { items: List<FileDescription> ->
            when (userPreferences.sortType) {
                SortType.NAME -> {
                    if (userPreferences.sortOrder == OrderType.ASC) {
                        items.sortedBy { it.name }
                    } else {
                        items.sortedByDescending { it.name }
                    }
                }

                SortType.CREATE_TS -> {
                    if (userPreferences.sortOrder == OrderType.ASC) {
                        items.sortedBy { it.createTs }
                    } else {
                        items.sortedByDescending { it.createTs }
                    }
                }

                SortType.UPDATE_TS -> {
                    if (userPreferences.sortOrder == OrderType.ASC) {
                        items.sortedBy { it.updateTs }
                    } else {
                        items.sortedByDescending { it.updateTs }
                    }
                }
            }
        }
        showItems = itemSortFn(showItems.filter { it.pinned == 1 }) +
                itemSortFn(showItems.filter { it.pinned == 0 })
        setCompleted(true)
        showItems
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(),
        initialValue = emptyList()
    )

    fun checkFileDescriptionExistInCurrentFolder(name: String): Boolean {
        return filteredFileDescriptionItems.value.any { it.parentId == uiState.value.path.last().id && it.name == name }
    }


    fun createFileDescription(name: String) = viewModelScope.launch {
        val currentTs = System.currentTimeMillis()
        val tableId = fileDescriptionRepository.upsertFileDescription(
            FileDescription(
                name = name,
                type = _uiState.value.type.name,
                parentId = _uiState.value.path.last().id,
                pinned = 0,
                createTs = currentTs,
                updateTs = currentTs
            )
        )
        // if it's datatable type, then create two default columns
        if (_uiState.value.type == FileDescriptionType.TABLE) {
            val ids = columnDescriptionRepository.batchUpsertColumnDescription(
                listOf(
                    ColumnDescription(
                        name = "create_ts",
                        tableId = tableId,
                        type = ColumnType.DATETIME.name,
                        width = 120,
                        display = 0,
                        ext = DateTimeFormat.yyyy_bar_MM_bar_dd_space_HH_colon_mm_colon_ss.displayName,
                        rank = -1,
                        createTs = currentTs,
                        updateTs = currentTs,
                    ),
                    ColumnDescription(
                        name = "update_ts",
                        tableId = tableId,
                        type = ColumnType.DATETIME.name,
                        width = 120,
                        display = 0,
                        ext = DateTimeFormat.yyyy_bar_MM_bar_dd_space_HH_colon_mm_colon_ss.displayName,
                        rank = -2,
                        createTs = currentTs,
                        updateTs = currentTs,
                    )
                )
            )
            tableOrderRepository.batchUpsertTableOrder(
                listOf(
                    TableOrder(
                        tableId = tableId,
                        columnId = ids[0],
                        orderType = OrderType.ASC.name,
                        checked = 1,
                        rank = Int.MAX_VALUE,
                        createTs = currentTs,
                        updateTs = currentTs
                    ),
                    TableOrder(
                        tableId = tableId,
                        columnId = ids[1],
                        orderType = OrderType.ASC.name,
                        checked = 0,
                        rank = Int.MAX_VALUE,
                        createTs = currentTs,
                        updateTs = currentTs
                    )
                )
            )
        }
    }

    fun renameFileDescription(name: String) = viewModelScope.launch {
        val currentTs = System.currentTimeMillis()
        val item = _uiState.value.updatingItem!!
        fileDescriptionRepository.upsertFileDescription(
            FileDescription(
                id = item.id,
                name = name,
                type = item.type,
                parentId = item.parentId,
                pinned = item.pinned,
                createTs = item.createTs,
                updateTs = currentTs
            )
        )
    }

    fun pinFileDescription(item: FileDescription) = viewModelScope.launch {
        val pinned = if (item.pinned == 1) 0 else 1
        fileDescriptionRepository.upsertFileDescription(
            FileDescription(
                id = item.id,
                name = item.name,
                type = item.type,
                parentId = item.parentId,
                pinned = pinned,
                createTs = item.createTs,
                updateTs = item.updateTs
            )
        )
    }

    fun deleteFileDescription() = viewModelScope.launch {
        val item = _uiState.value.updatingItem!!
        fileDescriptionRepository.deleteFileDescriptionById(item.id)
    }

    fun updateSortType(sortType: SortType) = viewModelScope.launch {
        userPreferencesRepository.updateSortType(sortType)
    }

    fun updateSortOrder(sortOrder: OrderType) = viewModelScope.launch {
        userPreferencesRepository.updateSortOrder(sortOrder)
    }


    fun intoFolder(fileDescription: FileDescription) {
        val index = _uiState.value.path.map { it.id }.indexOf(fileDescription.id)
        if (index != -1) {
            // drop util the id
            _uiState.update {
                it.copy(
                    path = it.path.dropLast(it.path.size - index - 1),
                )
            }
        } else {
            _uiState.update {
                it.copy(path = it.path + fileDescription)
            }
        }
    }

    fun outFolder() {
        _uiState.update {
            it.copy(
                path = it.path.dropLast(1),
            )
        }
    }

    fun homeFolder() {
        _uiState.update {
            it.copy(
                path = arrayListOf(
                    FileDescription(-1, "Home", FileDescriptionType.FOLDER.name, -2, 0, -1, -1)
                ),
            )
        }
    }

    fun isHomeFolder(): Boolean {
        return _uiState.value.path.size == 1
    }

    fun setName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun setUpdatingItem(fileDescription: FileDescription) {
        _uiState.update {
            it.copy(updatingItem = fileDescription)
        }
    }


    fun setSearchText(searchText: String) {
        _uiState.update {
            it.copy(searchText = searchText)
        }
    }

    fun setCompleted(isCompleted: Boolean) {
        _uiState.update {
            it.copy(isCompleted = isCompleted)
        }
    }

    fun openAddDialog(type: FileDescriptionType) {
        _uiState.update {
            it.copy(openAddDialog = true, type = type)
        }
    }

    fun openRenameDialog() {
        _uiState.update {
            it.copy(openRenameDialog = true)
        }
    }

    fun openDeleteDialog() {
        _uiState.update {
            it.copy(openDeleteDialog = true)
        }
    }

    fun closeDialog() {
        _uiState.update {
            it.copy(
                openAddDialog = false,
                openRenameDialog = false,
                openDeleteDialog = false,
                id = -1,
                name = ""
            )
        }
    }

    fun openFabMenus() {
        _uiState.update {
            it.copy(openFabMenus = true)
        }
    }

    fun closeFabMenus() {
        _uiState.update {
            it.copy(openFabMenus = false)
        }
    }
}