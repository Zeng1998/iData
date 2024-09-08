package com.zxc.idata.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextFieldDialog(
    title: String,
    value: String,
    focusRequester: FocusRequester,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    getResourceString: (Int) -> String,
    keyboardType: KeyboardType = KeyboardType.Text,
    otherDisableConfirmButtonCondition: (String) -> Boolean = { false }
) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var state by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    CommonDialog(
        title = title,
        wrapContentSize = true,
        hasCancelButton = false,
        disableConfirmButton = state.text.isEmpty() || otherDisableConfirmButtonCondition(state.text),
        onDismissRequest = onDismissRequest,
        onConfirm = { onConfirm(state.text) },
        getResourceString = getResourceString
    ) {
        TextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = state,
            onValueChange = { state = it },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}