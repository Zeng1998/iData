package com.zxc.idata.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zxc.idata.R

@Composable
fun DurationDialog(
    initSeconds: Long,
    onCancel: () -> Unit,
    onConfirm: (Long) -> Unit,
    getResourceString: (Int) -> String,
) {
    val days = remember { (0..9999).map { it.toString() } }
    val dayPickerState = rememberPickerState((initSeconds / 24 / 60 / 60).toString())
    val hours = remember { (0..23).map { it.toString() } }
    val hourPickerState = rememberPickerState((initSeconds / 60 / 60 % 24).toString())
    val minutes = remember { (0..59).map { it.toString() } }
    val minutePickerState = rememberPickerState((initSeconds / 60 % 60).toString())
    val seconds = remember { (0..59).map { it.toString() } }
    val secondPickerState = rememberPickerState((initSeconds % 60).toString())
    CommonDialog(
        title = "",
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = {
            val day = dayPickerState.selectedItem.toLong()
            val hour = hourPickerState.selectedItem.toLong()
            val minute = minutePickerState.selectedItem.toLong()
            val second = secondPickerState.selectedItem.toLong()
            onConfirm(
                day * 24 * 60 * 60 + hour * 60 * 60 + minute * 60 + second
            )
        },
        getResourceString = getResourceString
    ) {
        Column {
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = getResourceString(R.string.day_abbr),
                    modifier = Modifier.weight(0.25f),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = getResourceString(R.string.hour_abbr),
                    modifier = Modifier.weight(0.25f),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = getResourceString(R.string.minute_abbr),
                    modifier = Modifier.weight(0.25f),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = getResourceString(R.string.second_abbr),
                    modifier = Modifier.weight(0.25f),
                    textAlign = TextAlign.Center,
                )
            }
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Picker(
                    state = dayPickerState,
                    items = days,
                    visibleItemsCount = 3,
                    startIndex = dayPickerState.selectedItem.toInt(),
                    modifier = Modifier.weight(0.25f),
                    textModifier = Modifier.padding(8.dp),
                )
                Picker(
                    state = hourPickerState,
                    items = hours,
                    visibleItemsCount = 3,
                    startIndex = hourPickerState.selectedItem.toInt(),
                    modifier = Modifier.weight(0.25f),
                    textModifier = Modifier.padding(8.dp),
                )
                Picker(
                    state = minutePickerState,
                    items = minutes,
                    visibleItemsCount = 3,
                    startIndex = minutePickerState.selectedItem.toInt(),
                    modifier = Modifier.weight(0.25f),
                    textModifier = Modifier.padding(8.dp),
                )
                Picker(
                    state = secondPickerState,
                    items = seconds,
                    visibleItemsCount = 3,
                    startIndex = secondPickerState.selectedItem.toInt(),
                    modifier = Modifier.weight(0.25f),
                    textModifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}