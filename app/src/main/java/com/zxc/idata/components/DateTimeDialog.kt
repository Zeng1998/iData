package com.zxc.idata.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zxc.idata.R
import java.util.Calendar

@Composable
fun DateTimeDialog(
    initTs: Long,
    hasDatePicker: Boolean = true,
    hasTimePicker: Boolean = true,
    onCancel: () -> Unit,
    onConfirm: (Int, Int, Int, Int, Int, Int, Long) -> Unit,
    getResourceString: (Int) -> String,
) {
    var selectedTabId by remember { mutableIntStateOf(if (hasDatePicker) 0 else 1) }
    // equals setTimeInMillis()
    val now = if (initTs == -1L) Calendar.getInstance()
    else Calendar.getInstance().apply { timeInMillis = initTs }
    val initYear = now.get(Calendar.YEAR)
    val initMonth = now.get(Calendar.MONTH) + 1
    val initDay = now.get(Calendar.DAY_OF_MONTH)
    val initHour = now.get(Calendar.HOUR_OF_DAY)
    val initMinute = now.get(Calendar.MINUTE)
    val initSecond = now.get(Calendar.SECOND)
    val years = remember { (1001..2100).map { it.toString() } }
    val yearPickerState = rememberPickerState(initYear.toString())
    val months = remember { (1..12).map { it.toString() } }
    val monthPickerState = rememberPickerState(initMonth.toString())
    val days =
        remember { (1..now.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it.toString() } }
    val dayPickerState = rememberPickerState(initDay.toString())
    val hours = remember { (0..23).map { it.toString() } }
    val hourPickerState = rememberPickerState(now.get(Calendar.HOUR_OF_DAY).toString())
    val minutes = remember { (0..59).map { it.toString() } }
    val minutePickerState = rememberPickerState(now.get(Calendar.MINUTE).toString())
    val seconds = remember { (0..59).map { it.toString() } }
    val secondPickerState = rememberPickerState(now.get(Calendar.SECOND).toString())
    CommonDialog(
        title = "",
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = {
            val calendar = Calendar.getInstance()
            val year = yearPickerState.selectedItem.toInt()
            val month = monthPickerState.selectedItem.toInt() - 1
            val day = dayPickerState.selectedItem.toInt()
            val hour = hourPickerState.selectedItem.toInt()
            val minute = minutePickerState.selectedItem.toInt()
            val second = secondPickerState.selectedItem.toInt()
            calendar.set(year, month, day, hour, minute, second)
            onConfirm(
                year, month, day, hour, minute, second, calendar.timeInMillis
            )
        },
        getResourceString = getResourceString
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val tabs =
                    listOf(getResourceString(R.string.date), getResourceString(R.string.time))
                tabs.forEachIndexed { index, tab ->
                    if (index == 0 && !hasDatePicker) return@forEachIndexed
                    if (index == 1 && !hasTimePicker) return@forEachIndexed
                    Row(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .let {
                                if (selectedTabId == index) {
                                    it.background(MaterialTheme.colorScheme.surface)
                                } else {
                                    it
                                }
                            }
                            .clickable(
                                onClick = {
                                    selectedTabId = index
                                },
                            ),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = tab, color = if (selectedTabId == index) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            if (selectedTabId == 0 && hasDatePicker) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                ) {
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = getResourceString(R.string.year),
                            modifier = Modifier.weight(0.4f),
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = getResourceString(R.string.month),
                            modifier = Modifier.weight(0.3f),
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = getResourceString(R.string.day),
                            modifier = Modifier.weight(0.3f),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        Picker(
                            state = yearPickerState,
                            items = years,
                            visibleItemsCount = 3,
                            startIndex = initYear - 1001,
                            modifier = Modifier.weight(0.4f),
                            textModifier = Modifier.padding(8.dp),
                        )
                        Picker(
                            state = monthPickerState,
                            items = months,
                            visibleItemsCount = 3,
                            startIndex = initMonth - 1,
                            modifier = Modifier.weight(0.3f),
                            textModifier = Modifier.padding(8.dp),
                        )
                        Picker(
                            state = dayPickerState,
                            items = days,
                            visibleItemsCount = 3,
                            startIndex = initDay - 1,
                            modifier = Modifier.weight(0.3f),
                            textModifier = Modifier.padding(8.dp),
                        )
                    }
                }
            } else if (selectedTabId == 1 && hasTimePicker) {
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = getResourceString(R.string.hour),
                        modifier = Modifier.weight(0.4f),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = getResourceString(R.string.minute),
                        modifier = Modifier.weight(0.3f),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = getResourceString(R.string.second),
                        modifier = Modifier.weight(0.3f),
                        textAlign = TextAlign.Center,
                    )
                }
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Picker(
                        state = hourPickerState,
                        items = hours,
                        visibleItemsCount = 3,
                        startIndex = initHour,
                        modifier = Modifier.weight(0.4f),
                        textModifier = Modifier.padding(8.dp),
                    )
                    Picker(
                        state = minutePickerState,
                        items = minutes,
                        visibleItemsCount = 3,
                        startIndex = initMinute,
                        modifier = Modifier.weight(0.3f),
                        textModifier = Modifier.padding(8.dp),
                    )
                    Picker(
                        state = secondPickerState,
                        items = seconds,
                        visibleItemsCount = 3,
                        startIndex = initSecond,
                        modifier = Modifier.weight(0.3f),
                        textModifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}