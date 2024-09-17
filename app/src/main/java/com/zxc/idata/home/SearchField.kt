package com.zxc.idata.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxc.idata.R

@Composable
fun SearchField(
    showSearchField: Boolean,
    focusRequester: FocusRequester,
    searchText: String,
    onTextChange: (String) -> Unit,
    onCloseBtnClick: () -> Unit,
    onDisposable: () -> Unit,
) {
    AnimatedVisibility(visible = showSearchField,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }) {
        DisposableEffect(Unit) {
            // onCreate
            focusRequester.requestFocus()
            onDispose {
                // onDestroy
                onDisposable()
            }
        }
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 16.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            value = searchText,
            onValueChange = { onTextChange(it) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.width(48.dp)) {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1.0f)) {
                        innerTextField()
                    }
                    Column(modifier = Modifier.width(48.dp)) {
                        IconButton(onClick = onCloseBtnClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            },
        )
    }
}