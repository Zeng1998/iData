package com.zxc.idata.table

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxc.idata.R
import com.zxc.idata.components.CommonDialog
import sh.calvin.reorderable.ReorderableColumn
import kotlin.math.min

@Composable
fun SelectOptionsInputDialog(
    title: String,
    options: List<SelectOption>,
    onConfirm: (List<SelectOption>) -> Unit,
    onCancel: () -> Unit,
    getResourceString: (Int) -> String,
    canDeleteOption: (SelectOption) -> Boolean,
) {
    // 这个场景下直接用 Column 就行，LazyColumn 有很多奇奇怪怪的问题
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    // better than mutableStateOf(list)+copy
    val selectOptions = remember { options.toMutableStateList() }
    var updatingIndex by remember { mutableIntStateOf(-1) }
    var newAddFlag by remember { mutableStateOf(false) }
    var sortingMode by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFFc12c1f),
        Color(0xFF4f6f46),
        Color(0xFF87c0ca),
        Color(0xFFb6a014),
        Color(0xFF003d74),
        Color(0xFF8a1874),
        Color(0xFF151d29),
        Color(0xFFb2b6b6),
    )
    CommonDialog(
        title = title,
        wrapContentSize = true,
        hasConfirmButton = true,
        hasCancelButton = false,
        onDismissRequest = onCancel,
        onConfirm = {
            // check same name options
            if (selectOptions.map { it.text }.toHashSet().size == selectOptions.size) {
                onConfirm(selectOptions)
            } else {
                Toast
                    .makeText(
                        context,
                        getResourceString(R.string.same_name_option_tips),
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        },
        getResourceString = getResourceString,
        customButton = {
            TextButton(
                onClick = {
                    sortingMode = !sortingMode
                },
            ) {
                Text(getResourceString(if (sortingMode) R.string.edit else R.string.sort))
            }
        }
    ) {
        val scrollState = rememberScrollState()
        val maxHeight = min(5, selectOptions.size) * 36
        if (sortingMode) {
            ReorderableColumn(
                list = selectOptions.toList(),
                onSettle = { from, to ->
                    run {
                        selectOptions.add(to, selectOptions.removeAt(from))
                    }
                },
                modifier = Modifier
                    .height(maxHeight.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) { index, option, isDragging ->
                key(option.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable(onClick = {})
                                .draggableHandle(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Row(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        updatingIndex = if (updatingIndex == index) -1 else index
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.circlesolid),
                                contentDescription = null,
                                tint = Color(option.color).copy(alpha = 0.75f),
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Text(text = option.text, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider()
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .height(maxHeight.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {
                for ((index, option) in selectOptions.withIndex()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        updatingIndex = if (updatingIndex == index) -1 else index
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.circlesolid),
                                contentDescription = null,
                                tint = Color(option.color).copy(alpha = 0.75f),
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        if (updatingIndex == index) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                for (color in colors) {
                                    Button(
                                        onClick = {
                                            selectOptions[index] =
                                                selectOptions[index].copy(color = color.value)
                                            updatingIndex = -1
                                        },
                                        modifier = Modifier.size(24.dp),
                                        shape = CircleShape,
                                        // use border, not Modifier.border
                                        border = BorderStroke(
                                            4.dp,
                                            Color(
                                                color.red,
                                                color.green,
                                                color.blue,
                                            ),
                                        ),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (option.color == color.value) Color(
                                                red = color.red,
                                                green = color.green,
                                                blue = color.blue,
                                                alpha = 0.1f
                                            ) else Color.White
                                        )
                                    ) {
                                        Text(text = "A", color = color)
                                    }
                                }
                            }
                        }
                        LaunchedEffect(selectOptions) {
                            if (newAddFlag && index == selectOptions.size - 1) {
                                focusRequester.requestFocus()
                            }
                        }
                        BasicTextField(
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            value = TextFieldValue(
                                option.text,
                                TextRange(option.text.length)
                            ),
                            onValueChange = {
                                selectOptions[index] = selectOptions[index].copy(text = it.text)
                                newAddFlag = true
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            decorationBox = { innerTextField ->
                                InnerRowComponent() {
                                    Column {
                                        innerTextField()
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider()
                                    }
                                }

                            },
                        )
                        Row(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        if (canDeleteOption(option)) {
                                            selectOptions.removeAt(index)
                                            newAddFlag = false
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    getResourceString(R.string.cannot_delete_option_tips),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
            TextButton(
                onClick = {
                    val newOption = SelectOption.new()
                    selectOptions.add(newOption)
                    newAddFlag = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = null
                )
                Text(text = getResourceString(R.string.add_an_option))
            }
        }
    }
}