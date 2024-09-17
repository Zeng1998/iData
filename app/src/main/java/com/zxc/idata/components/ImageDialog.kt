package com.zxc.idata.components

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.scale.zoomable.zoomable.rememberZoomableState
import com.zxc.idata.R
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun ImageActionButton(
    iconId: Int,
    action: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surface,
) {
    IconButton(
        onClick = action) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageDialog(
    initImageList: List<String>,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
    onCamera: () -> Unit,
    getResourceString: (Int) -> String,
) {
    val context = LocalContext.current

    val imageItems = remember { initImageList.toMutableStateList() }
    var componentWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val colNum = 3
    val space = 8
    val imageSize = (componentWidth - (space * (colNum - 1)).dp) / colNum

    var isGalleryMode by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableIntStateOf(-1) }

    val multiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(9),
        onResult = { uriList ->
            uriList.map { uri ->
                Log.d("test", uri.toString())
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                if (!imageItems.contains(uri.toString())) {
                    imageItems.add(uri.toString())
                }
            }
        }
    )

    if (isGalleryMode) {
        val painter = rememberAsyncImagePainter(model = imageItems[currentImageIndex])
        val state = rememberZoomableState(contentSize = painter.intrinsicSize)
        var rotation by remember { mutableFloatStateOf(0f) }
        BasicAlertDialog(
            modifier = Modifier.fillMaxSize(),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { isGalleryMode = false }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ZoomableView(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    state = state,
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation),
                        painter = painter,
                        contentDescription = null
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ImageActionButton(
                            iconId = R.drawable.arrowleft,
                            action = {
                                currentImageIndex -= 1
                                if (currentImageIndex < 0) {
                                    currentImageIndex = imageItems.size - 1
                                }
                            },
                        )
                        ImageActionButton(
                            iconId = R.drawable.rotatecounterclockwise,
                            action = {
                                rotation -= 90f
                            }
                        )
                        ImageActionButton(
                            iconId = R.drawable.close,
                            action = {
                                isGalleryMode = false
                            }
                        )
                        ImageActionButton(
                            iconId = R.drawable.rotateclockwise,
                            action = {
                                rotation += 90f
                            }
                        )
                        ImageActionButton(
                            iconId = R.drawable.arrowright,
                            action = {
                                currentImageIndex += 1
                                if (currentImageIndex >= imageItems.size) {
                                    currentImageIndex = 0
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    CommonDialog(
        title = "",
        wrapContentSize = true,
        onDismissRequest = onCancel,
        onConfirm = {
            onConfirm(imageItems.joinToString(";"))
        },
        getResourceString = getResourceString
    ) {
        val lazyGridState = rememberLazyGridState()
        val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
            imageItems.add(to.index, imageItems.removeAt(from.index))
        }
        LazyVerticalGrid(
            modifier = Modifier
                .padding(16.dp)
                // https://medium.com/@vontonnie/how-to-get-the-height-or-width-of-an-element-in-jetpack-compose-8af04365d555
                .onGloballyPositioned {
                    componentWidth = with(density) {
                        it.size.width.toDp()
                    }
                },
            columns = GridCells.Fixed(colNum),
            horizontalArrangement = Arrangement.spacedBy(space.dp),
            verticalArrangement = Arrangement.spacedBy(space.dp),
            state = lazyGridState,
        ) {
            itemsIndexed(imageItems, key = { _, it -> it }) { index, it ->
                ReorderableItem(reorderableLazyGridState, key = it) { _ ->
                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .draggableHandle()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    onClick = {
                                        currentImageIndex = index
                                        isGalleryMode = true
                                    }
                                )
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .clickable(
                                    onClick = {
                                        imageItems.remove(it)
                                    }
                                ),
                        )
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(imageSize)
                        .background(Color.LightGray)
                        .combinedClickable(
                            onClick = {
                                Log.d("test", "单击")
                                multiLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            onLongClick = {
                                Log.d("test", "长按")
                                onCamera()
                            },
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = null
                    )
                }
            }
        }
    }
}