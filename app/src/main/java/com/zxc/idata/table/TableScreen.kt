package com.zxc.idata.table

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.scale.zoomable.zoomable.rememberZoomableState
import com.zxc.idata.R
import com.zxc.idata.components.ColumnOrderDialog
import com.zxc.idata.components.DateTimeDialog
import com.zxc.idata.components.DeleteDialog
import com.zxc.idata.components.DurationDialog
import com.zxc.idata.components.ImageDialog
import com.zxc.idata.components.OptionSelectDialog
import com.zxc.idata.components.RowOrderDialog
import com.zxc.idata.enums.ColumnType
import com.zxc.idata.enums.CountType
import com.zxc.idata.ui.theme.IDataTheme
import com.zxc.idata.utils.StringResourceUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    navHostController: NavHostController,
    tableId: Long,
    tableName: String,
    viewModel: TableScreenViewModel = hiltViewModel<TableScreenViewModel, TableScreenViewModel.DetailViewModelFactory> { factory ->
        factory.create(tableId)
    },
) {

    LocalConfiguration.current
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.setTableName(tableName)
    val columnDescriptionItems by viewModel.columnDescriptionItems.collectAsStateWithLifecycle()
    val tableOrderItems by viewModel.tableOrderItems.collectAsStateWithLifecycle()
    val languagePreference by viewModel.languagePreference.collectAsStateWithLifecycle()
    val stringResourceUtils = StringResourceUtils(context, languagePreference)
    val getResourceString = stringResourceUtils::getString
    var showDeleteRowsDialog by remember { mutableStateOf(false) }
    var showRowOrderDialog by remember { mutableStateOf(false) }
    var showColumnOrderDialog by remember { mutableStateOf(false) }
    var showCameraView by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    IDataTheme {
        if (showCameraView) {
            CameraScreen(
                onSuccess = {
                    showCameraView = false
                    viewModel.setNewTakenImageUri(it.toString())
                },
                onCancel = {
                    showCameraView = false
                }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        title = {
                            Text(uiState.tableName)
                        },
                        navigationIcon = {
                            IconButton(onClick = { navHostController.popBackStack() }) {
                                Icon(
                                    painterResource(id = R.drawable.arrowleft),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showRowOrderDialog = true }) {
                                Icon(
                                    painterResource(id = R.drawable.sortascending),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            IconButton(onClick = { showColumnOrderDialog = true }) {
                                Icon(
                                    painterResource(id = R.drawable.column),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                floatingActionButton = {
                    if (uiState.selectedRowIdList.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(getResourceString(R.string.rows_selected_tip).format(uiState.selectedRowIdList.size))
                            FloatingActionButton(
                                onClick = {
                                    showDeleteRowsDialog = true
                                },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "",
                                )
                            }

                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                ) {
                    TableView(
                        viewModel,
                        onCamera = { showCameraView = true },
                        getResourceString
                    )
                }
                if (showDeleteRowsDialog) {
                    DeleteDialog(
                        title = getResourceString(R.string.delete_rows_tips).format(uiState.selectedRowIdList.size),
                        onCancel = {
                            showDeleteRowsDialog = false
                            viewModel.clearSelectingRowId()
                        },
                        onConfirm = {
                            showDeleteRowsDialog = false
                            viewModel.deleteSelectedRows()
                        },
                        getResourceString = getResourceString
                    )
                }
                if (showRowOrderDialog) {
                    RowOrderDialog(
                        orderList = tableOrderItems,
                        columnList = columnDescriptionItems,
                        onConfirm = {
                            showRowOrderDialog = false
                            viewModel.upsertTableOrders(it)
                        },
                        onCancel = {
                            showRowOrderDialog = false
                        },
                        getResourceString = getResourceString
                    )
                }
                if (showColumnOrderDialog) {
                    ColumnOrderDialog(
                        columns = columnDescriptionItems.filter { it.name != "create_ts" && it.name != "update_ts" },
                        onConfirm = {
                            showColumnOrderDialog = false
                            val currentTs = System.currentTimeMillis()
                            viewModel.updateColumns(it.map { columnDescription ->
                                columnDescription.copy(updateTs = currentTs)
                            })
                        },
                        onCancel = {
                            showColumnOrderDialog = false
                        },
                        getResourceString = getResourceString
                    )
                }
            }
        }
    }
}


@Composable
fun ImageActionContainerButton(
    iconId: Int,
    action: () -> Unit,
) {
    IconButton(
        onClick = action,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(28.dp)
        )
    }
}

fun deleteImage(uri: Uri, context: Context): Boolean {
    val contentResolver = context.contentResolver
    return try {
        contentResolver.delete(uri, null, null) > 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun captureImage(imageCapture: ImageCapture, context: Context, onSuccess: (Uri?) -> Unit) {
    val contentValues = ContentValues().apply {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val datetime = simpleDateFormat.format(Date())
        put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_IDATA_${datetime}.jpeg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/iData")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(outputFileResults.savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.i("test", "Failed $exception")
            }
        })
}


@Composable
fun CameraPreview(previewImageUri: Uri, onSave: (Uri) -> Unit, onExit: (Uri) -> Unit) {
    val painter = rememberAsyncImagePainter(model = previewImageUri)
    val state = rememberZoomableState(contentSize = painter.intrinsicSize)

    BackHandler {
        onExit(previewImageUri)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ZoomableView(
            modifier = Modifier.fillMaxSize(),
            state = state,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
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
                    .padding(horizontal = 32.dp, vertical = 96.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ImageActionContainerButton(
                    iconId = R.drawable.close,
                    action = {
                        onExit(previewImageUri)
                    },
                )
                ImageActionContainerButton(
                    iconId = R.drawable.checkmark,
                    action = {
                        onSave(previewImageUri)
                    },
                )
            }
        }
    }
}

@Composable
fun CameraScreen(
    onSuccess: (Uri) -> Unit,
    onCancel: () -> Unit,
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
        ResolutionStrategy(
            Size(1920, 1080),
            ResolutionStrategy.FALLBACK_RULE_NONE
        )
    ).build()
    val preview = Preview.Builder().setResolutionSelector(resolutionSelector).build()
    val previewView = remember { PreviewView(context) }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().setResolutionSelector(resolutionSelector).build()
    }
    var isPreviewMode by remember { mutableStateOf(false) }
    var previewImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraProvider: ProcessCameraProvider? = null
    val cameraPermissionRequest =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}

    LaunchedEffect(lensFacing) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) -> {
                // Camera permission already granted
                // Implement camera related code
            }

            else -> {
                cameraPermissionRequest.launch(android.Manifest.permission.CAMERA)
            }
        }
        cameraProvider = context.getCameraProvider()
        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }
    val previewCancelAction = {
        isPreviewMode = false
        deleteImage(previewImageUri!!, context)
    }
    BackHandler {
        if (isPreviewMode) {
            previewCancelAction()
        } else {
            onCancel()
        }
    }
    if (isPreviewMode) {
        CameraPreview(previewImageUri!!, onSave = { uri ->
            isPreviewMode = false
            onSuccess(uri)
        }, onExit = { uri ->
            previewCancelAction()
        })
    } else {
        Box(
            contentAlignment = Alignment.BottomCenter, modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black
                )
        ) {
            AndroidView(
                { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(9 / 16f)
            )
            IconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 96.dp),
                onClick = {
                    captureImage(imageCapture = imageCapture, context = context, onSuccess = {
                        isPreviewMode = true
                        previewImageUri = it
                    })
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.circlefilled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

const val cellHeight = 40


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun TableView(
    viewModel: TableScreenViewModel,
    onCamera: () -> Unit,
    getResourceString: (Int) -> String,
) {
    LocalContext.current
    val configuration = LocalConfiguration.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val columnDescriptionItems by viewModel.columnDescriptionItems.collectAsStateWithLifecycle()
    val cellDataUiItems by viewModel.cellDataUiItems.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val horizontalScrollState = rememberScrollState()
    var selectedCellIndex by remember { mutableIntStateOf(-1) }
    var editingCellIndex by remember { mutableIntStateOf(-1) }
    var showDateTimeDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showDurationDialog by remember { mutableStateOf(false) }
    var showOptionSelectDialog by remember { mutableStateOf(false) }
    var showHeaderBottomSheet by remember { mutableStateOf(false) }

    val screenWidth = configuration.screenWidthDp
    val focusRequester = remember { FocusRequester() }
    var showAddColumnBottomSheet by remember { mutableStateOf(false) }
    val imeVisible = WindowInsets.Companion.isImeVisible
    LaunchedEffect(WindowInsets.isImeVisible) {
        if (!imeVisible) {
            Log.i("test", "输入法收起")
            focusManager.clearFocus()
            editingCellIndex = -1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = {
                    focusManager.clearFocus()
                    editingCellIndex = -1
                },
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                })
    ) {
        val noCellWidth = 40
        val columnWidth =
            columnDescriptionItems.filter { it.name != "create_ts" && it.name != "update_ts" }
                .map { it.width }.sumOf { it }
        val addHeaderWidth =
            if (noCellWidth + columnWidth + 120 < screenWidth) screenWidth - noCellWidth - columnWidth else 120
        val calculatedWidth = noCellWidth.dp + addHeaderWidth.dp + columnWidth.dp
        HorizontalDivider(modifier = Modifier.width(calculatedWidth))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            stickyHeader {
                LazyRow(
                    modifier = Modifier
                        .width(calculatedWidth)
                        .height(cellHeight.dp)
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        NoHeader(
                            isAllSelected = cellDataUiItems.isNotEmpty() && cellDataUiItems.size == uiState.selectedRowIdList.size,
                            onClick = {
                                if (cellDataUiItems.size == uiState.selectedRowIdList.size) {
                                    viewModel.clearSelectingRowId()
                                } else {
                                    viewModel.addAllSelectingRowId()
                                }
                            })
                        VerticalDivider()
                    }
                    items(columnDescriptionItems.filter { it.name != "create_ts" && it.name != "update_ts" }) {
                        CommonHeader(
                            name = it.name,
                            width = it.width,
                            iconPainter = painterResource(id = ColumnType.valueOf(it.type).iconId),
                            onClick = {
                                viewModel.setUpdatingColumn(it)
                                showHeaderBottomSheet = true
                            }
                        )
                        VerticalDivider()
                    }
                    item {
                        AddHeader(addHeaderWidth, onClick = {
                            showAddColumnBottomSheet = true
                        })
                        VerticalDivider()
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .width(calculatedWidth)
                        .shadow(6.dp)
                )
            }
            itemsIndexed(
                cellDataUiItems,
                key = { _, cellDataList -> cellDataList.first().data.rowId }) { rowIndex, cellDataList ->
                val rowId = cellDataList.first().data.rowId
                val isSelected = rowId in uiState.selectedRowIdList
                LazyRow(
                    modifier = Modifier
                        .width(calculatedWidth)
                        .height(cellHeight.dp)
                        .let { if (isSelected) it.background(MaterialTheme.colorScheme.secondaryContainer) else it },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colCount = columnDescriptionItems.size
                    item {
                        NoCell(
                            index = rowIndex + 1,
                            onClick = { viewModel.addSelectingRowId(rowId) },
                            onCheckBoxClick = { viewModel.delSelectingRowId(rowId) },
                            selected = isSelected
                        )
                        VerticalDivider()
                    }
                    itemsIndexed(
                        cellDataList.filter { it.columnName != "create_ts" && it.columnName != "update_ts" },
                        key = { _, cellData -> cellData.columnId }) { colIndex, cellData ->
                        when (cellData.columnType) {
                            ColumnType.TEXT.name, ColumnType.NUMBER.name -> {
                                TextOrNumberCell(
                                    type = ColumnType.valueOf(cellData.columnType),
                                    text = cellData.data.value,
                                    width = cellData.columnWidth,
                                    unit = cellData.columnExt,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    isEditable = (rowIndex * colCount + colIndex == editingCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                        editingCellIndex = rowIndex * colCount + colIndex
                                    },
                                    onValueChange = {
                                        viewModel.updateCellData(cellData.data, it)
                                    },
                                    focusRequester = focusRequester,
                                )
                            }

                            ColumnType.DATETIME.name, ColumnType.DATE.name, ColumnType.TIME.name -> {
                                DateTimeCell(
                                    timestamp = cellData.data.value,
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    format = cellData.columnExt,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                        when (cellData.columnType) {
                                            ColumnType.DATETIME.name -> {
                                                showDateTimeDialog = true
                                            }

                                            ColumnType.DATE.name -> {
                                                showDateDialog = true
                                            }

                                            else -> {
                                                showTimeDialog = true
                                            }
                                        }
                                        viewModel.setUpdatingRow(cellData.data)
                                    }
                                )
                            }

                            ColumnType.DURATION.name -> {
                                DurationCell(
                                    seconds = cellData.data.value,
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                        showDurationDialog = true
                                        viewModel.setUpdatingRow(cellData.data)
                                    },
                                    getResourceString = getResourceString,
                                )
                            }

                            ColumnType.CHECKBOX.name -> {
                                CheckboxCell(
                                    isChecked = cellData.data.value.toBoolean(),
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                    },
                                    onCheckboxClick = {
                                        viewModel.updateCellData(
                                            cellData.data,
                                            (!cellData.data.value.toBoolean()).toString()
                                        )
                                    }
                                )
                            }

                            ColumnType.SINGLE_SELECT.name, ColumnType.MULTIPLE_SELECT.name -> {
                                SingleOrMultipleSelectCell(
                                    selectedOptions = cellData.data.value.toSelectOptionListFromIds(
                                        cellData.columnExt.toSelectOptionList()
                                    ),
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                        showOptionSelectDialog = true
                                        viewModel.setUpdatingRow(cellData.data)
                                        viewModel.setUsingColumn(columnDescriptionItems[colIndex])
                                    },
                                )
                            }

                            ColumnType.COUNT.name -> {
                                val splitText = cellData.columnExt.split(",")
                                CountCell(
                                    initValue = cellData.data.value.let { if (it == "") splitText[1].toLong() else it.toLong() },
                                    countType = CountType.valueOf(splitText[0]),
                                    minValue = splitText[1].toLong(),
                                    maxValue = splitText[2].toLong(),
                                    step = splitText[3].toLong(),
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                    },
                                    getResourceString = getResourceString,
                                    onValueChange = {
                                        viewModel.updateCellData(cellData.data, it.toString())
                                    }
                                )
                            }

                            ColumnType.RATING.name -> {
                                val splitText = cellData.columnExt.split(",")
                                RatingCell(
                                    value = cellData.data.value.let { if (it == "") 0 else it.toInt() },
                                    maxValue = splitText[0].toInt(),
                                    halfStar = splitText[1].toBoolean(),
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                    },
                                    onValueChange = {
                                        viewModel.updateCellData(cellData.data, it.toString())
                                    }
                                )
                            }

                            ColumnType.IMAGE.name -> {
                                ImageCell(
                                    imageURIList = cellData.data.value.split(";"),
                                    width = cellData.columnWidth,
                                    height = cellHeight,
                                    isSelected = (rowIndex * colCount + colIndex == selectedCellIndex),
                                    onCellClick = {
                                        selectedCellIndex = rowIndex * colCount + colIndex
                                        viewModel.setUpdatingRow(cellData.data)
                                        viewModel.setShowImageDialog(true)
//                                        showImageDialog = true
                                    },
                                )
                            }
                        }
                        VerticalDivider()
                    }
                }
                HorizontalDivider(modifier = Modifier.width(calculatedWidth))
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .width(calculatedWidth)
                        .height(cellHeight.dp)
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    item {
                        NoCell(onClick = {}, onCheckBoxClick = {})
                        VerticalDivider()
                    }
                    items(columnDescriptionItems.filter { it.name != "create_ts" && it.name != "update_ts" }) {
                        AddCell(width = it.width, height = cellHeight) {
                            viewModel.createRowCells()
                        }
                        VerticalDivider()
                    }
                }
                HorizontalDivider(modifier = Modifier.width(calculatedWidth))
            }
        }
        if (showHeaderBottomSheet) {
            ColumnSheet(
                columnState = uiState.updatingColumnState,
                onUpdateColumnState = {
                    viewModel.setUpdatingColumnState(it)
                    viewModel.updateColumn()
                },
                isUpdating = true,
                onDismissRequest = {
                    showHeaderBottomSheet = false
                },
                onCreateColumn = { true },
                onDeleteColumn = viewModel::deleteColumn,
                getResourceString = getResourceString,
                canDeleteOption = {
                    !viewModel.findSelectOptionInColumn(uiState.updatingColumnState.id, it)
                }
            )
        }
        if (showAddColumnBottomSheet) {
            ColumnSheet(
                columnState = uiState.addingColumnState,
                onUpdateColumnState = viewModel::setAddingColumnState,
                isUpdating = false,
                onDismissRequest = {
                    showAddColumnBottomSheet = false
                },
                onCreateColumn = viewModel::createColumn,
                onDeleteColumn = {},
                getResourceString = getResourceString,
                canDeleteOption = {
                    viewModel.findSelectOptionInColumn(uiState.addingColumnState.id, it)
                }
            )
        }
        if (showDateTimeDialog || showDateDialog || showTimeDialog) {
            DateTimeDialog(
                initTs = uiState.updatingRow!!.value.let { if (it == "") -1L else it.toLong() },
                hasDatePicker = !showTimeDialog,
                hasTimePicker = !showDateDialog,
                onCancel = {
                    showDateTimeDialog = false
                    showDateDialog = false
                    showTimeDialog = false
                },
                onConfirm = { _, _, _, _, _, _, ts ->
                    showDateTimeDialog = false
                    showDateDialog = false
                    showTimeDialog = false
                    viewModel.updateCellData(uiState.updatingRow!!, ts.toString())
                },
                getResourceString = getResourceString
            )
        }
        if (showDurationDialog) {
            DurationDialog(
                initSeconds = uiState.updatingRow!!.value.let { if (it == "") 0 else it.toLong() },
                onCancel = {
                    showDurationDialog = false
                },
                onConfirm = {
                    showDurationDialog = false
                    viewModel.updateCellData(uiState.updatingRow!!, it.toString())
                },
                getResourceString = getResourceString
            )
        }
        if (showOptionSelectDialog) {
            OptionSelectDialog(
                options = uiState.usingColumnState.selectOptions,
                initialSelectedOptions = uiState.updatingRow!!.value.toSelectOptionListFromIds(
                    uiState.usingColumnState.selectOptions
                ),
                onCancel = { showOptionSelectDialog = false },
                onConfirm = {
                    showOptionSelectDialog = false
                    viewModel.updateCellData(uiState.updatingRow!!, it.toIdsText())
                },
                single = uiState.usingColumnState.type == ColumnType.SINGLE_SELECT,
                getResourceString = getResourceString,
            )
        }
        if (uiState.showImageDialog) {
            val imageList =
                if (uiState.updatingRow!!.value != "") {
                    uiState.updatingRow!!.value.split(";").toMutableList()
                } else mutableListOf()
            if (uiState.newTakenImageUri != "") {
                imageList.add(uiState.newTakenImageUri)
                viewModel.setNewTakenImageUri("")
            }
            ImageDialog(
                initImageList = imageList,
                onCancel = {
                    viewModel.setShowImageDialog(false)
//                    showImageDialog = false
                },
                onConfirm = {
                    viewModel.setShowImageDialog(false)
//                    showImageDialog = false
                    viewModel.updateCellData(uiState.updatingRow!!, it)
                },
                onCamera = {
                    onCamera()
                },
                getResourceString = getResourceString
            )
        }
    }
}
