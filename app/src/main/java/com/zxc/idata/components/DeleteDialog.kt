package com.zxc.idata.components

import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    getResourceString: (Int) -> String,
) {
    CommonDialog(
        title = title,
        wrapContentHeight = true,
        confirmButtonDangerMode = true,
        onDismissRequest = onCancel,
        onConfirm = onConfirm,
        getResourceString = getResourceString
    )
}