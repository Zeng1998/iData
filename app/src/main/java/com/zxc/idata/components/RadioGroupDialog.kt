package com.zxc.idata.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioGroupDialog(
    title: String,
    options: List<String>,
    iconIds: List<Int>? = null,
    initSelectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit,
    getResourceString: (Int) -> String,
) {
    CommonDialog(
        title = title,
        wrapContentSize = true,
        hasConfirmButton = false,
        hasCancelButton = false,
        onDismissRequest = onCancel,
        onConfirm = { },
        getResourceString = getResourceString
    ) {
        Column(Modifier.selectableGroup()) {
            options.forEachIndexed { index, text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .selectable(
                            selected = (index == initSelectedIndex),
                            onClick = { onConfirm(index) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == initSelectedIndex),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    if(iconIds != null) {
                        Icon(
                            painter = painterResource(iconIds[index]),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}