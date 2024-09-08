package com.zxc.idata.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxc.idata.R
import com.zxc.idata.enums.CountType

@Deprecated("暂时不用了")
@Composable
fun CountDialog(
    initCount: Long,
    countType: CountType,
    minValue: Long,
    maxValue: Long,
    step: Long,
    onCancel: () -> Unit,
    onConfirm: (Long) -> Unit,
    getResourceString: (Int) -> String,
) {
    var value by remember { mutableStateOf(initCount) }
    val context = LocalContext.current
    CommonDialog(
        title = "",
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = { onConfirm(value) },
        getResourceString = getResourceString
    ) {
        if (countType == CountType.NORMAL) {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    if (value - step >= minValue) {
                        value -= step
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
                Text(text = value.toString(), fontSize = 36.sp)
                IconButton(onClick = {
                    if (value + step <= maxValue) {
                        value += step
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
}