package com.zxc.idata.table

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zxc.idata.R
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.CountType
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun AddCell(
    width: Int,
    height: Int,
    onCellClick: () -> Unit,
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = false,
        onCellClick = onCellClick,
        ripple = true,
    ) {
        Text(text = "+", overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun NoCell(
    index: Int = -1,
    onClick: () -> Unit,
    onCheckBoxClick: () -> Unit,
    selected: Boolean = false
) {
    Row(
        modifier = Modifier
            .height(cellHeight.dp)
            .width(40.dp)
            .clickable(onClick = onClick, indication = null, interactionSource = remember {
                MutableInteractionSource()
            }),
        horizontalArrangement = Arrangement.Center,
    ) {
        if (selected) {
            Checkbox(
                modifier = Modifier
                    .width(40.dp),
                checked = true,
                onCheckedChange = { onCheckBoxClick() })
        } else {
            Text(
                if (index == -1) "" else index.toString(),
                modifier = Modifier
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InnerRowComponent(
    modifier: Modifier = Modifier,
    hasUnit: Boolean = false,
    innerContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = if (hasUnit) 4.dp else 8.dp)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        innerContent()
    }
}

@Composable
fun TextOrNumberCell(
    type: ColumnType,
    text: String,
    unit: String,
    width: Int,
    height: Int,
    isSelected: Boolean,
    isEditable: Boolean,
    onCellClick: () -> Unit,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .height(height.dp)
                    .weight(1f),
            ) {
                var textFieldValueState by remember { mutableStateOf(TextFieldValue(text)) }
                val textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    textAlign = if (type == ColumnType.TEXT) TextAlign.Start else TextAlign.End
                )
                if (isEditable) {
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    BasicTextField(
                        value = textFieldValueState,
                        onValueChange = {
                            textFieldValueState = it
                            onValueChange(it.text)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                // unfocused的时候光标回到最开头
                                textFieldValueState = if (it.isFocused) {
                                    TextFieldValue(
                                        textFieldValueState.text,
                                        selection = TextRange(text.length)
                                    )
                                } else {
                                    TextFieldValue(textFieldValueState.text)
                                }
                            },
                        textStyle = textStyle,
                        decorationBox = { innerTextField ->
                            InnerRowComponent(hasUnit = unit.isEmpty()) { innerTextField() }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = if (type == ColumnType.TEXT) KeyboardType.Text else KeyboardType.Number)
                    )
                } else {
                    InnerRowComponent(
                        modifier = Modifier.fillMaxWidth(),
                        hasUnit = unit.isEmpty()
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = textFieldValueState.text,
                            style = textStyle,
                        )
                    }
                }
            }

            if (text.isNotEmpty() && unit.isNotEmpty()) {
                Text(
                    text = unit,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

    }
}


@Composable
fun DateTimeCell(
    timestamp: String,
    width: Int,
    height: Int,
    format: String,
    isSelected: Boolean,
    onCellClick: () -> Unit,
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {
        if (timestamp.isNotEmpty()) {
            val text = SimpleDateFormat(format, Locale.getDefault()).format(timestamp.toLong())
            Text(text = text, overflow = TextOverflow.Ellipsis)
        } else {
            Text(text = "", overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DurationCell(
    seconds: String,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
    getResourceString: (Int) -> String,
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {
        var text = ""
        if (seconds.isNotEmpty()) {
            val secondsLong = seconds.toLong()
            val days = secondsLong / (60 * 60 * 24)
            val hours = (secondsLong % (60 * 60 * 24)) / (60 * 60)
            val minutes = (secondsLong % (60 * 60)) / 60
            val remainSeconds = secondsLong % 60

            if (days > 0) {
                text += days.toString() + getResourceString(R.string.day_abbr)
                text += " "
            }
            if (hours > 0) {
                text += hours.toString() + getResourceString(R.string.hour_abbr)
                text += " "
            }
            if (minutes > 0) {
                text += minutes.toString() + getResourceString(R.string.minute_abbr)
                text += " "
            }
            text += remainSeconds.toString() + getResourceString(R.string.second_abbr)
        }
        Text(text = text, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun CheckboxCell(
    isChecked: Boolean,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
    onCheckboxClick: () -> Unit
) {
    var checkedState by remember { mutableStateOf(isChecked) }
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                onCheckboxClick()
                checkedState = it
            }
        )
    }
}

@Composable
fun SingleOrMultipleSelectCell(
    selectedOptions: List<SelectOption>,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            selectedOptions.forEach {
                val color = Color(it.color)
                // https://stackoverflow.com/a/76889911/11191086
                // If you use padding before everything else, it behaves as padding.
                // If you use padding after everything else, it behaves as margin.
                Text(
                    text = it.text,
                    color = color,
                    modifier = Modifier
                        .border(1.dp, color, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CountCell(
    initValue: Long,
    countType: CountType,
    minValue: Long,
    maxValue: Long,
    step: Long,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
    getResourceString: (Int) -> String,
    onValueChange: (Long) -> Unit
) {
    var value by remember { mutableLongStateOf(initValue) }
    val context = LocalContext.current
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                if (value - step >= minValue) {
                    value -= step
                    onValueChange(value)
                } else {
                    Toast.makeText(
                        context,
                        getResourceString(R.string.out_of_range_limit_tips),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.subtract),
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = value.toString())
                if (countType == CountType.PROGRESS) {
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { value * 1f / maxValue },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            IconButton(onClick = {
                if (value + step <= maxValue) {
                    value += step
                    onValueChange(value)
                } else {
                    Toast.makeText(
                        context,
                        getResourceString(R.string.out_of_range_limit_tips),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
            }
        }
    }
}

@Composable
fun RatingCell(
    value: Int,
    maxValue: Int,
    halfStar: Boolean,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
    onValueChange: (Int) -> Unit
) {
    CommonSelectedCell(
        width = width,
        height = height,
        isSelected = isSelected,
        onCellClick = onCellClick
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 8.dp)
                // !! docs: The pointer input handling block will be cancelled and re-started when pointerInput is recomposed with a different key1.
                // https://stackoverflow.com/questions/72299963/value-of-mutablestate-inside-modifier-pointerinput-doesnt-change-after-remember
                // https://stackoverflow.com/questions/75004121/ontap-detecttapgestures-not-working-properly-as-clickables-with-same-code-an
                .pointerInput(halfStar) {
                    detectTapGestures { offset ->
                        val dp = offset.x.toDp()
                        val newValue = if (halfStar) {
                            (dp / 12).value.toInt() + 1
                        } else {
                            ((dp / 24).value.toInt() + 1) * 2
                        }
                        onValueChange(newValue)
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (i in 1..maxValue) {
                if (value >= i * 2) {
                    Icon(
                        painter = painterResource(
                            R.drawable.starfilled
                        ),
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFFffd43b),
                        contentDescription = null
                    )
                } else if (value >= i * 2 - 1 && halfStar) {
                    Box(modifier = Modifier) {
                        Icon(
                            painter = painterResource(
                                R.drawable.starhalf
                            ),
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFFffd43b),
                            contentDescription = null
                        )
                        Icon(
                            painter = painterResource(
                                R.drawable.star
                            ),
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFFffd43b),
                            contentDescription = null
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(
                            R.drawable.star
                        ),
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFFffd43b),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun ImageCell(
    imageURIList: List<String>,
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
) {
    CommonSelectedCell(width, height, isSelected, onCellClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            imageURIList.forEach {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}