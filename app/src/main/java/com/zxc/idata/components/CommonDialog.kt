package com.zxc.idata.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zxc.idata.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDialog(
    title: String,
    wrapContentHeight: Boolean = false,
    wrapContentWidth: Boolean = false,
    wrapContentSize: Boolean = false,
    height: Int = 0,
    hasConfirmButton: Boolean = true,
    hasCancelButton: Boolean = true,
    disableConfirmButton: Boolean = false,
    confirmButtonDangerMode: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    getResourceString: (Int) -> String,
    customButton: @Composable () -> Unit = {},
    innerContent: @Composable () -> Unit = {}
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .let { if (wrapContentHeight) it.wrapContentHeight() else it }
                .let { if (wrapContentWidth) it.wrapContentWidth() else it }
                .let { if (wrapContentSize) it.wrapContentSize() else it }
                .let { if (height > 0) it.height(height.dp) else it }
                .clip(RoundedCornerShape(16.dp)),
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                innerContent()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.weight(0.4f),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        customButton()
                    }
                    Row(modifier = Modifier.weight(0.6f), horizontalArrangement = Arrangement.End) {
                        if (hasCancelButton) {
                            TextButton(
                                onClick = onDismissRequest,
                            ) {
                                Text(getResourceString(R.string.cancel))
                            }
                        }
                        if (hasConfirmButton) {
                            TextButton(
                                onClick = onConfirm,
                                enabled = !disableConfirmButton,
                            ) {
                                Text(
                                    getResourceString(R.string.confirm),
                                    color = if (confirmButtonDangerMode) MaterialTheme.colorScheme.error else Color.Unspecified,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}