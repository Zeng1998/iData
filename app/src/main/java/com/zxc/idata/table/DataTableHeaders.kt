package com.zxc.idata.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zxc.idata.R

@Composable
fun NoHeader(isAllSelected: Boolean, onClick: () -> Unit) {
    Checkbox(
        modifier = Modifier.width(40.dp),
        checked = isAllSelected,
        onCheckedChange = { onClick() })
}

@Composable
fun CommonHeader(
    name: String,
    width: Int,
    iconPainter: Painter,
    onClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .width(width.dp)
            .clickable {
                onClick()
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(text = name, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun AddHeader(width: Int, onClick: () -> Unit) {
    CommonHeader(
        name = "",
        width = width,
        iconPainter = painterResource(id = R.drawable.add),
        onClick = onClick
    )
}
