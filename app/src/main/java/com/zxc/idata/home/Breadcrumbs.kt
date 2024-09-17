package com.zxc.idata.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zxc.idata.R
import com.zxc.idata.data.model.FileDescription

@Composable
fun Breadcrumbs(path: List<FileDescription>, onHomeClick: () -> Unit, onPathClick: (FileDescription) -> Unit) {
    Row(
        modifier = Modifier.wrapContentHeight(),
        verticalAlignment = Alignment.Bottom
    ) {
        // alignByBaseline: align English and Chinese characters.
        Icon(
            painterResource(id = R.drawable.home),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .alignByBaseline()
                .clickable(onClick = onHomeClick)
        )
        for (item in path) {
            Text(" / ", modifier = Modifier.alignByBaseline())
            Text(
                item.name,
                modifier = Modifier
                    .clickable(onClick = { onPathClick(item) })
                    .alignByBaseline()
            )
        }
    }
}