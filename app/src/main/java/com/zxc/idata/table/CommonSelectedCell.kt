package com.zxc.idata.table

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommonSelectedCell(
    width: Int,
    height: Int,
    isSelected: Boolean,
    onCellClick: () -> Unit,
    ripple: Boolean = false,
    innerContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(height.dp)
            .width(width.dp)
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .let {
                if (ripple) {
                    it.clickable(
                        onClick = onCellClick,
                    )
                } else {
                    it.clickable(
                        onClick = onCellClick,
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        }
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        innerContent()
    }
}