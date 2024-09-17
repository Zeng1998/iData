package com.zxc.idata.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zxc.idata.table.SelectOption
import kotlin.math.min

@Composable
fun OptionSelectDialog(
    options: List<SelectOption>,
    initialSelectedOptions: List<SelectOption>,
    onCancel: () -> Unit,
    onConfirm: (List<SelectOption>) -> Unit,
    single: Boolean,
    getResourceString: (Int) -> String,
) {
    val selectedIdList = remember { initialSelectedOptions.map { it.id }.toMutableStateList() }

    CommonDialog(
        title = "",
        wrapContentSize = true,
        hasConfirmButton = true,
        hasCancelButton = false,
        onDismissRequest = onCancel,
        onConfirm = { onConfirm(options.filter { it.id in selectedIdList }) },
        getResourceString = getResourceString,
    ) {
        val scrollState = rememberScrollState()
        val maxHeight = min(10, options.size) * 36

        Column(
            modifier = Modifier
                .height(maxHeight.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            for ((index, option) in options.withIndex()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .selectable(
                            selected = (option.id in selectedIdList),
                            onClick = {
                                if (single) {
                                    selectedIdList.clear()
                                    selectedIdList.add(option.id)
                                } else {
                                    if (option.id in selectedIdList) {
                                        selectedIdList.remove(option.id)
                                    } else {
                                        selectedIdList.add(option.id)
                                    }
                                }
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (single) {
                        RadioButton(
                            selected = (option.id in selectedIdList),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                    } else {
                        Checkbox(
                            modifier = Modifier.width(24.dp),
                            checked = (option.id in selectedIdList),
                            onCheckedChange = {
                                if (option.id in selectedIdList) {
                                    selectedIdList.remove(option.id)
                                } else {
                                    selectedIdList.add(option.id)
                                }
                            })
                    }
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(text = option.text, color = Color(option.color))
                    }
                }
            }
        }
    }
}