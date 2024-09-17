package com.zxc.idata.table

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zxc.idata.R
import com.zxc.idata.components.DeleteDialog
import com.zxc.idata.components.RadioGroupDialog
import com.zxc.idata.components.TextFieldDialog
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.CountType
import com.zxc.idata.enums.DateFormat
import com.zxc.idata.enums.DateTimeFormat
import com.zxc.idata.enums.TimeFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.ln

@Composable
fun CommonMetaItemSurface(
    isEditable: Boolean,
    onClick: () -> Unit = {},
    innerContent: @Composable () -> Unit
) {
    var modifier = Modifier
        .height(48.dp)
        .fillMaxWidth()
    modifier = if (isEditable) {
        modifier.clickable(
            onClick = onClick,
        )
    } else {
        modifier.clickable(
            onClick = {},
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    }
    Surface(
        shape = RoundedCornerShape(4.dp), shadowElevation = 0.5.dp,
        modifier = modifier,
    ) {
        innerContent()
    }
}

@Composable
fun HeaderMetaItem(
    key: String,
    value: String,
    isEditable: Boolean = true,
    onClick: () -> Unit = {},
) {
    CommonMetaItemSurface(isEditable = isEditable, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = key)
            Text(text = value, color = if (isEditable) Color.Unspecified else Color.LightGray)
        }
    }
}

// f(0.0) = x
// f(0.5) = y
// f(1.0) = z
// A = (xz - y²) / (x - 2y + z)
// B = (y - x)² / (x - 2y + z)
// C = 2 * log((z-y) / (y-x))

const val A = 51.4
const val B = 8.6
const val C = 4.2

fun positionToWidth(position: Float): Int {
    return ceil(A + B * exp(C * position / 100)).toInt()
}

fun widthToPosition(width: Int): Float {
    return (ln((width.toDouble() - A) / B) / C).toFloat() * 100
}

@Composable
fun ColumnWidthSlider(key: String, width: Int, onWidthChange: (Int) -> Unit) {
    var sliderPosition by remember { mutableFloatStateOf(widthToPosition(width)) }
    CommonMetaItemSurface(isEditable = false, onClick = {}) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = key, modifier = Modifier.width(120.dp))
            Slider(
                modifier = Modifier.width(240.dp),
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    onWidthChange(positionToWidth(it))
                },
                valueRange = 0f..100f,
            )
        }
    }
}

@Composable
fun CheckBoxItem(key: String, initValue: Boolean, onValueChange: (Boolean) -> Unit) {
    CommonMetaItemSurface(isEditable = false, onClick = {}) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = key)
            val checkedState = remember { mutableStateOf(initValue) }
            Checkbox(
                // 不知道为什么固定size20dp就能取消内部padding
                modifier = Modifier
                    .size(20.dp),
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onValueChange(it)
                })
        }
    }
}

@Composable
fun DeleteMetaItem(text: String, isEditable: Boolean, onClick: () -> Unit = {}) {
    CommonMetaItemSurface(
        isEditable = isEditable,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
fun ConfirmButton(text: String, onClick: () -> Unit = {}, enabled: Boolean) {
    CommonMetaItemSurface(
        isEditable = enabled,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (enabled) {
                        it.background(MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        it.background(MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text, color = if (enabled) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnSheet(
    columnState: ColumnState,
    onUpdateColumnState: (ColumnState) -> Unit,
    isUpdating: Boolean,
    onDismissRequest: () -> Unit,
    onCreateColumn: () -> Boolean,
    onDeleteColumn: (Long) -> Unit,
    getResourceString: (Int) -> String,
    canDeleteOption: (SelectOption) -> Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    var showColumnNameDialog by remember { mutableStateOf(false) }
    var showColumnTypeDialog by remember { mutableStateOf(false) }
    var showNumberUnitDialog by remember { mutableStateOf(false) }
    var showDateTimeFormatDialog by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }
    var showTimeFormatDialog by remember { mutableStateOf(false) }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showCountTypeDialog by remember { mutableStateOf(false) }
    var showCountMinValueDialog by remember { mutableStateOf(false) }
    var showCountMaxValueDialog by remember { mutableStateOf(false) }
    var showCountStepDialog by remember { mutableStateOf(false) }
    var showMaxRatingDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeaderMetaItem(
                key = getResourceString(R.string.column_name),
                value = columnState.name,
                onClick = {
                    showColumnNameDialog = true
                })
            HeaderMetaItem(
                key = getResourceString(R.string.column_type),
                value = getResourceString(columnState.type.i18nId),
                isEditable = !isUpdating,
                onClick = {
                    showColumnTypeDialog = true
                })
            if (isUpdating) {
                ColumnWidthSlider(
                    key = getResourceString(R.string.width),
                    width = columnState.width,
                    onWidthChange = {
                        onUpdateColumnState(columnState.copy(width = it))
                    })
            }
            // no need to manually set width when adding column
            // ext
            if (columnState.type == ColumnType.NUMBER) {
                HeaderMetaItem(
                    key = getResourceString(R.string.unit),
                    value = columnState.ext,
                    onClick = { showNumberUnitDialog = true }
                )
            }
            if (columnState.type in arrayOf(
                    ColumnType.DATETIME,
                    ColumnType.DATE,
                    ColumnType.TIME
                )
            ) {
                if (columnState.ext.isEmpty()) {
                    onUpdateColumnState(
                        columnState.copy(
                            ext = when (columnState.type) {
                                ColumnType.DATETIME -> DateTimeFormat.yyyy_bar_MM_bar_dd_space_HH_colon_mm_colon_ss.displayName
                                ColumnType.DATE -> DateFormat.yyyy_bar_MM_bar_dd.displayName
                                ColumnType.TIME -> TimeFormat.HH_colon_mm_colon_ss.displayName
                                else -> ""
                            }
                        )
                    )
                }
                HeaderMetaItem(
                    key = getResourceString(R.string.format),
                    value = columnState.ext,
                    onClick = {
                        when (columnState.type) {
                            ColumnType.DATETIME -> {
                                showDateTimeFormatDialog = true
                            }

                            ColumnType.DATE -> {
                                showDateFormatDialog = true
                            }

                            else -> {
                                showTimeFormatDialog = true
                            }
                        }
                    }
                )
            }
            if (columnState.type in arrayOf(ColumnType.SINGLE_SELECT, ColumnType.MULTIPLE_SELECT)) {
                HeaderMetaItem(
                    key = getResourceString(R.string.options),
                    value = columnState.selectOptions.size.toString(),
                    onClick = {
                        showOptionsDialog = true
                    })
            }
            if (columnState.type == ColumnType.COUNT) {
                HeaderMetaItem(
                    key = getResourceString(R.string.type),
                    value = getResourceString(columnState.countType.i18nId),
                    onClick = { showCountTypeDialog = true })
                HeaderMetaItem(
                    key = getResourceString(R.string.min_value),
                    value = columnState.minValue.toString(),
                    onClick = { showCountMinValueDialog = true })
                HeaderMetaItem(
                    key = getResourceString(R.string.max_value),
                    value = columnState.maxValue.toString(),
                    onClick = { showCountMaxValueDialog = true })
                HeaderMetaItem(
                    key = getResourceString(R.string.step),
                    value = columnState.step.toString(),
                    onClick = { showCountStepDialog = true })
            }
            if (columnState.type == ColumnType.RATING) {
                onUpdateColumnState(
                    columnState.copy(
                        ext = "${columnState.ratingMax},${columnState.halfStar}"
                    )
                )
                HeaderMetaItem(
                    key = getResourceString(R.string.max_rating),
                    value = columnState.ratingMax.toString(),
                    onClick = { showMaxRatingDialog = true })
                CheckBoxItem(
                    key = getResourceString(R.string.half_star),
                    initValue = columnState.halfStar,
                    onValueChange = {
                        onUpdateColumnState(
                            columnState.copy(
                                halfStar = it,
                                ext = "${columnState.ratingMax},$it"
                            )
                        )
                    })
            }
            if (!isUpdating) {
                ConfirmButton(
                    text = getResourceString(R.string.confirm),
                    onClick = {
                        if (onCreateColumn()) {
                            onDismissRequest()
                            onUpdateColumnState(ColumnState())
                        } else {
                            Toast.makeText(
                                context,
                                getResourceString(R.string.same_name_column_tips),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = columnState.name.isNotEmpty()
                )
            }
            if (isUpdating) {
                DeleteMetaItem(
                    text = getResourceString(R.string.delete),
                    isEditable = true,
                    onClick = {
                        showDeleteConfirmDialog = true
                    }
                )
            }
        }
    }
    if (showColumnNameDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.column_name),
            value = columnState.name,
            focusRequester = focusRequester,
            onDismissRequest = { showColumnNameDialog = false },
            onConfirm = {
                onUpdateColumnState(columnState.copy(name = it))
                showColumnNameDialog = false
            },
            getResourceString = getResourceString
        )
    }
    if (showColumnTypeDialog) {
        RadioGroupDialog(
            title = getResourceString(R.string.column_type),
            options = ColumnType.entries.map { getResourceString(it.i18nId) },
            iconIds = ColumnType.entries.map { it.iconId },
            initSelectedIndex = ColumnType.entries.indexOf(columnState.type),
            onConfirm = {
                onUpdateColumnState(columnState.copy(type = ColumnType.entries[it]))
                showColumnTypeDialog = false
            },
            onCancel = {
                showColumnTypeDialog = false
            },
            getResourceString = getResourceString
        )
    }
    if (showNumberUnitDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.unit),
            value = columnState.ext,
            focusRequester = focusRequester,
            onDismissRequest = { showNumberUnitDialog = false },
            onConfirm = {
                onUpdateColumnState(columnState.copy(ext = it))
                showNumberUnitDialog = false
            },
            getResourceString = getResourceString
        )
    }
    if (showDateTimeFormatDialog || showDateFormatDialog || showTimeFormatDialog) {
        val options = if (showDateTimeFormatDialog) {
            DateTimeFormat.entries.map { it.displayName }
        } else if (showDateFormatDialog) {
            DateFormat.entries.map { it.displayName }
        } else {
            TimeFormat.entries.map { it.displayName }
        }
        val currentTimeMillis = System.currentTimeMillis()
        RadioGroupDialog(
            title = getResourceString(R.string.format),
            options = options.map {
                SimpleDateFormat(it, Locale.getDefault()).format(
                    currentTimeMillis
                )
            },
            initSelectedIndex = options.indexOf(columnState.ext),
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        ext = if (showDateTimeFormatDialog) DateTimeFormat.entries[it].displayName
                        else if (showDateFormatDialog) DateFormat.entries[it].displayName
                        else TimeFormat.entries[it].displayName
                    )
                )
                showDateTimeFormatDialog = false
                showDateFormatDialog = false
                showTimeFormatDialog = false
            },
            onCancel = {
                showDateTimeFormatDialog = false
                showDateFormatDialog = false
                showTimeFormatDialog = false
            },
            getResourceString = getResourceString,
        )
    }
    if (showOptionsDialog) {
        SelectOptionsInputDialog(
            title = getResourceString(R.string.options),
            options = columnState.selectOptions,
            onConfirm = { selectOptions ->
                onUpdateColumnState(
                    columnState.copy(
                        selectOptions = selectOptions,
                        ext = selectOptions.toText()
                    )
                )
                showOptionsDialog = false
            },
            onCancel = {
                showOptionsDialog = false
            },
            getResourceString = getResourceString,
            canDeleteOption = canDeleteOption,
        )
    }
    if (showCountTypeDialog) {
        RadioGroupDialog(
            title = getResourceString(R.string.type),
            options = CountType.entries.map { getResourceString(it.i18nId) },
            initSelectedIndex = CountType.entries.indexOf(columnState.countType),
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        countType = CountType.entries[it],
                        ext = "${CountType.entries[it]},${columnState.minValue},${columnState.maxValue},${columnState.step}"
                    )
                )
                showCountTypeDialog = false
            },
            onCancel = {
                showCountTypeDialog = false
            },
            getResourceString = getResourceString
        )
    }
    if (showCountMinValueDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.min_value),
            value = columnState.minValue.toString(),
            focusRequester = focusRequester,
            onDismissRequest = { showCountMinValueDialog = false },
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        minValue = it.toLong(),
                        ext = "${columnState.countType},$it,${columnState.maxValue},${columnState.step}"
                    )
                )
                showCountMinValueDialog = false
            },
            getResourceString = getResourceString,
            keyboardType = KeyboardType.Number,
            otherDisableConfirmButtonCondition = {
                it.toIntOrNull() == null || it.toInt() <= 0 || it.toInt() > columnState.maxValue
            }
        )
    }
    if (showCountMaxValueDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.max_value),
            value = columnState.maxValue.toString(),
            focusRequester = focusRequester,
            onDismissRequest = { showCountMaxValueDialog = false },
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        maxValue = it.toLong(),
                        ext = "${columnState.countType},${columnState.minValue},$it,${columnState.step}"
                    )
                )
                showCountMaxValueDialog = false
            },
            getResourceString = getResourceString,
            keyboardType = KeyboardType.Number,
            otherDisableConfirmButtonCondition = {
                it.toIntOrNull() == null || it.toInt() <= 0 || it.toInt() < columnState.minValue
            }
        )
    }
    if (showCountStepDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.step),
            value = columnState.step.toString(),
            focusRequester = focusRequester,
            onDismissRequest = { showCountStepDialog = false },
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        step = it.toLong(),
                        ext = "${columnState.countType},${columnState.minValue},${columnState.maxValue},${it}"
                    )
                )
                showCountStepDialog = false
            },
            getResourceString = getResourceString,
            keyboardType = KeyboardType.Number,
            otherDisableConfirmButtonCondition = {
                it.toIntOrNull() == null || it.toInt() <= 0
            }
        )
    }
    if (showMaxRatingDialog) {
        TextFieldDialog(
            title = getResourceString(R.string.max_rating),
            value = columnState.ratingMax.toString(),
            focusRequester = focusRequester,
            onDismissRequest = { showMaxRatingDialog = false },
            onConfirm = {
                onUpdateColumnState(
                    columnState.copy(
                        ratingMax = it.toInt(),
                        ext = "$it,${columnState.halfStar}"
                    )
                )
                showMaxRatingDialog = false
            },
            getResourceString = getResourceString,
            keyboardType = KeyboardType.Number,
            otherDisableConfirmButtonCondition = {
                it.toIntOrNull() == null || it.toInt() <= 0 || it.toInt() > 10
            }
        )
    }
    if (showDeleteConfirmDialog) {
        DeleteDialog(
            title = getResourceString(R.string.delete_column_tip),
            onCancel = { showDeleteConfirmDialog = false },
            onConfirm = {
                onDeleteColumn(columnState.id)
                onDismissRequest()
            },
            getResourceString = getResourceString
        )
    }
}