package com.zxc.idata.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zxc.idata.R
import com.zxc.idata.enums.FileDescriptionType

@Composable
fun FabMenus(type: FileDescriptionType, onClick: (FileDescriptionType) -> Unit, getResourceString: (Int) -> String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("${getResourceString(R.string.new_)} ${getResourceString(type.i18nId)}")
        SmallFloatingActionButton(onClick = { onClick(type) }) {
            Icon(
                painter = painterResource(
                    id = if (type == FileDescriptionType.FOLDER) R.drawable.folderadd
                    else R.drawable.tablealias
                ),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun FabButton(
    showMenus: Boolean,
    openAddDialog: (FileDescriptionType)->Unit,
    openFabMenus: ()->Unit,
    closeFabMenus: ()->Unit,
    getResourceString: (Int) -> String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
            visible = showMenus,
            enter = slideInVertically { it },
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End,
            ) {
                val clickFn = { type: FileDescriptionType ->
                    openAddDialog(type)
                    closeFabMenus()
                }
                FabMenus(FileDescriptionType.FOLDER, clickFn,getResourceString)
                FabMenus(FileDescriptionType.TABLE, clickFn,getResourceString)
            }
        }
        if (showMenus) {
            FloatingActionButton(onClick = closeFabMenus) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = ""
                )
            }
        } else {
            FloatingActionButton(onClick = openFabMenus) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = ""
                )
            }
        }
    }
}