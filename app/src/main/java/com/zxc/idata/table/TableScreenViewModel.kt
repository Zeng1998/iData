package com.zxc.idata.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxc.idata.data.model.CellData
import com.zxc.idata.data.repository.CellDataRepository
import com.zxc.idata.data.model.ColumnDescription
import com.zxc.idata.data.repository.DefaultColumnDescriptionRepository
import com.zxc.idata.data.repository.DefaultFileDescriptionRepository
import com.zxc.idata.data.model.TableOrder
import com.zxc.idata.data.repository.TableOrderRepository
import com.zxc.idata.data.repository.UserPreferencesRepository
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.CountType
import com.zxc.idata.enums.Language
import com.zxc.idata.enums.OrderType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CellDataUi(
    val data: CellData,
    val columnId: Long,
    val columnName: String,
    val columnWidth: Int,
    val columnType: String,
    val columnExt: String,
    val columnRank: Int,
)

data class ColumnState(
    val id: Long = 0,
    val name: String = "",
    val tableId: Long = 0,
    val type: ColumnType = ColumnType.TEXT,
    val width: Int = 120,
    val display: Int = 1,
    val ext: String = "",
    val rank: Int = 0,
    val createTs: Long = 0L,
    val updateTs: Long = 0L,

    // select column
    val selectOptions: List<SelectOption> = emptyList(),
    val updatingOptionIndex: Int = -1,
    val newOptionFlag: Boolean = false,

    // count column
    val countType: CountType = CountType.NORMAL,
    val minValue: Long = 0,
    val maxValue: Long = 100,
    val step: Long = 1L,

    // rating column
    val ratingMax: Int = 5,
    val halfStar: Boolean = false,
)

data class UiState(
    val tableName: String = "",
    val updatingRow: CellData? = null,

    val selectedRowIdList: List<Long> = emptyList(),

    val addingColumnState: ColumnState = ColumnState(),
    val updatingColumnState: ColumnState = ColumnState(),
    val usingColumnState: ColumnState = ColumnState(),

    // when back from the camera screen, keep the image dialog state
    val showImageDialog: Boolean = false,
    val newTakenImageUri: String = "",
)

fun String.convertToReversed(): String {
    return this.map { char -> (Int.MAX_VALUE - char.code).toChar() }.joinToString()
}

// https://medium.com/@cgaisl/how-to-pass-arguments-to-a-hiltviewmodel-from-compose-97c74a75f772
@HiltViewModel(assistedFactory = TableScreenViewModel.DetailViewModelFactory::class)
class TableScreenViewModel @AssistedInject constructor(
    private val fileDescriptionRepository: DefaultFileDescriptionRepository,
    private val columnDescriptionRepository: DefaultColumnDescriptionRepository,
    private val tableOrderRepository: TableOrderRepository,
    private val cellDataRepository: CellDataRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @Assisted val tableId: Long,
) : ViewModel() {

    @AssistedFactory
    interface DetailViewModelFactory {
        fun create(tableId: Long): TableScreenViewModel
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // https://stackoverflow.com/questions/78751064/room-flow-how-to-handle-updates-of-dynamic-based-param-request
    // 为什么要用 flatMapLatest？如果是map相当于uistate里的每个值都会映射成一个list
    private val _columnDescriptionItems =
        columnDescriptionRepository.getColumnDescriptionStreamByTableId(tableId = tableId)
    private val _userPreferencesFlow = userPreferencesRepository.getUserPreferencesFlow()
    private val _tableOrderItems = tableOrderRepository.getAllTableOrderByTableId(tableId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val columnDescriptionItems = _columnDescriptionItems
        .mapLatest {
            it.sortedBy { it.rank }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val languagePreference = _userPreferencesFlow.mapLatest { it.language }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = Language.EN_US,
        )

    val tableOrderItems = _tableOrderItems.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(),
        initialValue = emptyList()
    )

    private val _cellDataItems = cellDataRepository.getCellDataStreamByTableId(tableId)
    val cellDataUiItems =
        combine(
            _columnDescriptionItems,
            _cellDataItems,
            _tableOrderItems
        ) { columnDescriptionItems, cellDataItems, tableOrderItems ->
            // 注意一个坑点，这里的结果最好不要用 Map，改用 List，不然会出现这里的数据更新了，但是 Screen 里的 UI 没有刷新
            // order of rows
            val compareByParams = tableOrderItems
                .filter { it.checked == 1 }
                .sortedBy { it.rank }
                .map { order ->
                    val columnType =
                        ColumnType.valueOf(columnDescriptionItems.first { it.id == order.columnId }.type)
                    if (order.orderType == OrderType.ASC.name) {
                        { it: List<CellData> ->
                            it.first { i -> i.columnId == order.columnId }.value
                        }
                    } else {
                        { it: List<CellData> ->
                            if (columnType in arrayOf(
                                    ColumnType.NUMBER,
                                    ColumnType.DATETIME,
                                    ColumnType.DATE,
                                    ColumnType.TIME,
                                    ColumnType.DURATION,
                                    ColumnType.COUNT,
                                    ColumnType.RATING
                                )
                            ) {
                                it.first { i -> i.columnId == order.columnId }.value.toLong() * -1
                            } else {
                                it.first { i -> i.columnId == order.columnId }.value.convertToReversed()
                            }
                        }
                    }
                }
            val results = cellDataItems.groupBy { cell -> cell.rowId }
                .values
                .sortedWith(compareBy(*compareByParams.toTypedArray()))
                .map { cellDataList ->
                    cellDataList
                        .filter { cellData -> columnDescriptionItems.any { it.id == cellData.columnId } }
                        .map { cellData ->
                            val column = columnDescriptionItems.first { it.id == cellData.columnId }
                            CellDataUi(
                                data = cellData,
                                columnId = column.id,
                                columnName = column.name,
                                columnWidth = column.width,
                                columnType = column.type,
                                columnExt = column.ext,
                                columnRank = column.rank,
                            )
                        }
                        // order of columns
                        .sortedBy { it.columnRank }
                }
            results
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = emptyList()
        )

    // after add/update/delete the columns/rows
    private fun updateTableUpdateTs(updateTs: Long) {
        viewModelScope.launch {
            fileDescriptionRepository.updateFileDescriptionUpdateTsById(tableId, updateTs)
        }
    }

    fun createColumn(): Boolean {
        val currentTs = System.currentTimeMillis()
        val item = _uiState.value.addingColumnState
        if (columnDescriptionItems.value.any { it.name == item.name }) {
            return false
        }
        val currentColumnSize = columnDescriptionItems.value.size
        viewModelScope.launch {
            val columnId = columnDescriptionRepository.upsertColumnDescription(
                ColumnDescription(
                    name = item.name,
                    tableId = tableId,
                    type = item.type.name,
                    width = item.width,
                    display = item.display,
                    ext = item.ext,
                    rank = currentColumnSize + 1,
                    createTs = currentTs,
                    updateTs = currentTs
                )
            )
            createColumnCells(columnId, currentTs)
            createTableOrder(columnId, currentTs)
        }
        updateTableUpdateTs(currentTs)
        return true
    }

    private fun createTableOrder(columnId: Long, currentTs: Long) {
        viewModelScope.launch {
            tableOrderRepository.upsertTableOrder(
                TableOrder(
                    tableId = tableId,
                    columnId = columnId,
                    orderType = OrderType.ASC.name,
                    checked = 0,
                    rank = -1,
                    createTs = currentTs,
                    updateTs = currentTs,
                )
            )
        }
    }


    fun createRowCells() {
        val currentTs = System.currentTimeMillis()
        val currentRowIds = cellDataUiItems.value.map { it.first().data.rowId }
        val newRowId = if (currentRowIds.isEmpty()) 1 else currentRowIds.max() + 1
        viewModelScope.launch {
            cellDataRepository.batchUpsertCellData(
                // two special columns: create_ts and update_ts
                columnDescriptionItems.value.filter { it.name != "create_ts" && it.name != "update_ts" }
                    .map {
                        CellData(
                            tableId = tableId,
                            columnId = it.id,
                            rowId = newRowId,
                            value = "",
                            createTs = currentTs,
                            updateTs = currentTs,
                        )
                    } + listOf(
                    // no change
                    CellData(
                        tableId = tableId,
                        columnId = columnDescriptionItems.value.first { it.name == "create_ts" }.id,
                        rowId = newRowId,
                        value = currentTs.toString(),
                        createTs = currentTs,
                        updateTs = currentTs,
                    ),
                    // change when update cell, <del>add/delete new column(cells)</del>
                    CellData(
                        tableId = tableId,
                        columnId = columnDescriptionItems.value.first { it.name == "update_ts" }.id,
                        rowId = newRowId,
                        value = currentTs.toString(),
                        createTs = currentTs,
                        updateTs = currentTs,
                    )
                )

            )
        }
        updateTableUpdateTs(currentTs)
    }

    private fun createColumnCells(columnId: Long, currentTs: Long) {
        viewModelScope.launch {
            cellDataRepository.batchUpsertCellData(
                cellDataUiItems.value.map {
                    val rowId = it.first().data.rowId
                    CellData(
                        tableId = tableId,
                        columnId = columnId,
                        rowId = rowId,
                        value = "",
                        createTs = currentTs,
                        updateTs = currentTs,
                    )
                }
            )
        }
    }

    fun updateCellData(cellData: CellData, value: String) {
        val currentTs = System.currentTimeMillis()
        // no mixed insert and update for room
        viewModelScope.launch {
            cellDataRepository.upsertCellData(
                CellData(
                    id = cellData.id,
                    tableId = cellData.tableId,
                    columnId = cellData.columnId,
                    rowId = cellData.rowId,
                    value = value,
                    createTs = cellData.createTs,
                    updateTs = currentTs,
                )
            )
        }
        viewModelScope.launch {
            cellDataRepository.updateCellDataUpdateTsById(
                tableId = tableId,
                columnId = columnDescriptionItems.value.first { it.name == "update_ts" }.id,
                rowId = cellData.rowId,
                updateTs = currentTs.toString()
            )
        }
        updateTableUpdateTs(currentTs)
    }

    fun deleteSelectedRows() {
        val currentTs = System.currentTimeMillis()
        viewModelScope.launch {
            cellDataRepository.deleteCellDataByRowIdList(_uiState.value.selectedRowIdList)
        }
        updateTableUpdateTs(currentTs)
    }

    fun deleteColumn(columnId: Long) {
        val currentTs = System.currentTimeMillis()
        viewModelScope.launch {
            columnDescriptionRepository.deleteColumnDescriptionById(columnId)
        }
        viewModelScope.launch {
            cellDataRepository.deleteCellDataByColumnId(columnId)
        }
        updateTableUpdateTs(currentTs)
    }

    private fun ColumnDescription.toColumnState(): ColumnState {
        var state = ColumnState(
            id = this.id,
            name = this.name,
            tableId = this.tableId,
            type = ColumnType.valueOf(this.type),
            width = this.width,
            display = this.display,
            ext = this.ext,
            selectOptions = if (this.type in arrayOf(
                    ColumnType.SINGLE_SELECT.name,
                    ColumnType.MULTIPLE_SELECT.name,
                )
            ) {
                this.ext.toSelectOptionList()
            } else {
                emptyList()
            },
            updatingOptionIndex = -1,
            createTs = this.createTs,
            updateTs = this.updateTs,
        )
        if (this.type == ColumnType.COUNT.name) {
            val splitText = this.ext.split(",")
            val countType = CountType.valueOf(splitText[0])
            val minValue = splitText[1].toLong()
            val maxValue = splitText[2].toLong()
            val step = splitText[3].toLong()
            state = state.copy(
                countType = countType,
                minValue = minValue,
                maxValue = maxValue,
                step = step,
            )
        }
        if (this.type == ColumnType.RATING.name) {
            val splitText = this.ext.split(",")
            val ratingMax = splitText[0].toInt()
            val halfStar = splitText[1].toBoolean()
            state = state.copy(
                ratingMax = ratingMax,
                halfStar = halfStar,
            )
        }
        return state
    }

    fun setUpdatingColumn(columnDescription: ColumnDescription) {
        _uiState.value = _uiState.value.copy(
            updatingColumnState = columnDescription.toColumnState()
        )
    }

    fun setUsingColumn(columnDescription: ColumnDescription) {
        _uiState.value = _uiState.value.copy(
            usingColumnState = columnDescription.toColumnState()
        )
    }

    fun setAddingColumnState(columnState: ColumnState) {
        _uiState.value = _uiState.value.copy(addingColumnState = columnState)
    }

    fun setUpdatingColumnState(columnState: ColumnState) {
        _uiState.value = _uiState.value.copy(updatingColumnState = columnState)
    }

    fun setUpdatingRow(cellData: CellData) {
        _uiState.value = _uiState.value.copy(updatingRow = cellData)
    }

    fun setTableName(tableName: String) {
        _uiState.value = _uiState.value.copy(tableName = tableName)
    }

    fun addSelectingRowId(rowId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedRowIdList = _uiState.value.selectedRowIdList + rowId
        )
    }

    fun delSelectingRowId(rowId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedRowIdList = _uiState.value.selectedRowIdList - rowId
        )
    }

    fun clearSelectingRowId() {
        _uiState.value = _uiState.value.copy(
            selectedRowIdList = emptyList()
        )
    }

    fun addAllSelectingRowId() {
        _uiState.value = _uiState.value.copy(
            selectedRowIdList = cellDataUiItems.value.map { it.first().data.rowId }
        )
    }

    // write the updatingColumnState to db
    fun updateColumn() {
        val currentTs = System.currentTimeMillis()
        val item = _uiState.value.updatingColumnState
        viewModelScope.launch {
            columnDescriptionRepository.upsertColumnDescription(
                ColumnDescription(
                    id = item.id,
                    name = item.name,
                    tableId = item.tableId,
                    type = item.type.name,
                    width = item.width,
                    display = item.display,
                    ext = item.ext,
                    rank = item.rank,
                    createTs = item.createTs,
                    updateTs = currentTs
                )
            )
        }
        updateTableUpdateTs(currentTs)
    }

    fun updateColumns(columns: List<ColumnDescription>) {
        val currentTs = System.currentTimeMillis()
        viewModelScope.launch {
            columnDescriptionRepository.batchUpsertColumnDescription(columns)
        }
        updateTableUpdateTs(currentTs)
    }

    fun upsertTableOrders(tableOrders: List<TableOrder>) {
        val currentTs = System.currentTimeMillis()
        viewModelScope.launch {
            tableOrderRepository.batchUpsertTableOrder(tableOrders.map { it.copy(updateTs = currentTs) })
        }
    }

    // find if a select column's values contain a specific option
    fun findSelectOptionInColumn(columnId: Long, option: SelectOption): Boolean {
        return cellDataUiItems.value.flatten().any {
            it.columnId == columnId && it.data.value.contains(option.id)
        }
    }

    fun setShowImageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showImageDialog = show)
    }

    fun setNewTakenImageUri(uri: String) {
        _uiState.value = _uiState.value.copy(newTakenImageUri = uri)
    }

}