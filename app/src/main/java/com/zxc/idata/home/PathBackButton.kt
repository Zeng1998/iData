package com.zxc.idata.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zxc.idata.R

@Composable
fun PathBackButton(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Icon(
            painterResource(id = R.drawable.previousoutline),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onBackClick)
        )
    }
}