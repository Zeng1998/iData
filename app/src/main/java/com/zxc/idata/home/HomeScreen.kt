package com.zxc.idata.home

import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.zxc.idata.R
import com.zxc.idata.components.DeleteDialog
import com.zxc.idata.components.NavigationDrawerLayout
import com.zxc.idata.components.TextFieldDialog
import com.zxc.idata.enums.FileDescriptionType
import com.zxc.idata.enums.OrderType
import com.zxc.idata.enums.SortType
import com.zxc.idata.ui.theme.IDataTheme
import com.zxc.idata.utils.StringResourceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.zxc.idata.data.model.FileDescription as FileDescription1

data class DropdownMenuItem(
    val text: String,
    val onClick: () -> Unit,
    val leadingIconDrawableId: Int
)

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun FileDescriptionListItem(
    item: FileDescription1,
    onRenameClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    whenAnimationFinished: Boolean = false,
    getResourceString: (Int) -> String,
) {
    val selected = remember { mutableStateOf(false) }
    val scale =
        animateFloatAsState(if (selected.value) 0.8f else 1f, label = "", finishedListener = {
            if (whenAnimationFinished) {
                onClick()
            }
        })
    Surface(
        shape = RoundedCornerShape(8.dp), shadowElevation = 8.dp,
        modifier = Modifier
            .scale(scale.value)
            .clip(RoundedCornerShape(8.dp))
            // border要放在scale后面，不然内容缩小了但是border不缩小
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    // 按下缩小动画
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected.value = true
                            }

                            MotionEvent.ACTION_UP -> {
                                selected.value = false
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                selected.value = false
                            }

                        }
                        true
                    },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {}
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        if (item.type == FileDescriptionType.FOLDER.name) {
                            painterResource(id = R.drawable.folder)
                        } else {
                            painterResource(id = R.drawable.datatable)
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = item.name, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        fontSize = if (item.name.length < 64) 16.sp else 14.sp,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
            ) {
                var cardMoreMenusExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.CenterEnd)
                ) {
                    IconButton(onClick = { cardMoreMenusExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "item menus (rename, delete and copy)",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    val menuItems = listOf(
                        DropdownMenuItem(
                            text = getResourceString(R.string.rename),
                            onClick = onRenameClick,
                            leadingIconDrawableId = R.drawable.edit
                        ),
                        DropdownMenuItem(
                            text = if (item.pinned == 0) getResourceString(R.string.pin)
                            else getResourceString(R.string.unpin),
                            onClick = onPinClick,
                            leadingIconDrawableId = R.drawable.pin
                        ),
                        DropdownMenuItem(
                            text = getResourceString(R.string.delete),
                            onClick = onDeleteClick,
                            leadingIconDrawableId = R.drawable.delete
                        ),
                    )
                    DropdownMenu(
                        expanded = cardMoreMenusExpanded,
                        onDismissRequest = { cardMoreMenusExpanded = false }) {
                        for (menuItem in menuItems) {
                            DropdownMenuItem(
                                text = { Text(menuItem.text) },
                                onClick = {
                                    menuItem.onClick()
                                    cardMoreMenusExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(id = menuItem.leadingIconDrawableId),
                                        contentDescription = menuItem.text,
                                        modifier = Modifier.size(24.dp),
                                    )
                                },
                                colors = if (menuItem.text == getResourceString(R.string.delete))
                                    MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.error,
                                        leadingIconColor = MaterialTheme.colorScheme.error
                                    ) else if (menuItem.text == getResourceString(R.string.unpin)) {
                                    MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.primary,
                                        leadingIconColor = MaterialTheme.colorScheme.primary
                                    )
                                } else
                                    MenuDefaults.itemColors()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sortTypePreference by viewModel.sortTypePreference.collectAsStateWithLifecycle()
    val sortOrderPreference by viewModel.sortOrderPreference.collectAsStateWithLifecycle()
    val languagePreference by viewModel.languagePreference.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val fileDescriptionItems by viewModel.filteredFileDescriptionItems.collectAsStateWithLifecycle()
    var sortMenusExpanded by remember { mutableStateOf(false) }
    var searchFieldExpanded by remember { mutableStateOf(false) }
    var showRightActions by remember { mutableStateOf(true) }
    val stringResourceUtils = StringResourceUtils(context, languagePreference)
    val getResourceString = stringResourceUtils::getString
    var isBackPressed by remember { mutableStateOf(false) }
    BackHandler(enabled = !isBackPressed) {
        if (viewModel.isHomeFolder()) {
            isBackPressed = true
            Toast.makeText(
                context,
                getResourceString(R.string.press_back_again_to_exit_tip),
                Toast.LENGTH_SHORT
            ).show()
            scope.launch {
                delay(2000L)
                isBackPressed = false
            }
        } else {
            viewModel.outFolder()
        }
    }

    IDataTheme {
        NavigationDrawerLayout(
            selectedRoute = "homeScreen",
            drawerState = drawerState,
            navHostController = navHostController,
            getResourceString = getResourceString,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { if (!searchFieldExpanded) Text(stringResource(id = R.string.app_name)) },
                        navigationIcon = {
                            if (!searchFieldExpanded) IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.menu),
                                    contentDescription = null
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        actions = {
                            if (!searchFieldExpanded && showRightActions) IconButton(onClick = {
                                searchFieldExpanded = true
                                showRightActions = false
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.search),
                                    contentDescription = null,
                                )
                            }
                            if (!searchFieldExpanded && showRightActions) IconButton(onClick = {
                                sortMenusExpanded = true
                                showRightActions = false
                            }) {
                                Icon(
                                    painterResource(
                                        id = if (sortOrderPreference == OrderType.ASC) {
                                            R.drawable.sortascending
                                        } else {
                                            R.drawable.sortdescending
                                        }
                                    ),
                                    contentDescription = null,
                                )
                            }
                            val keyboardController = LocalSoftwareKeyboardController.current
                            SearchField(
                                showSearchField = searchFieldExpanded,
                                focusRequester = focusRequester,
                                searchText = uiState.searchText,
                                onTextChange = { viewModel.setSearchText(it) },
                                onCloseBtnClick = {
                                    // 手动关闭软键盘，减少卡顿
                                    keyboardController!!.hide()
                                    searchFieldExpanded = false
                                    viewModel.setSearchText("")
                                },
                                onDisposable = {
                                    showRightActions = true
                                }
                            )

                            val menuItems = SortType.entries.toTypedArray()
                            DropdownMenu(
                                expanded = sortMenusExpanded,
                                onDismissRequest = { sortMenusExpanded = false }) {
                                for (menuItem in menuItems) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                getResourceString(R.string.sort_option_tips).format(
                                                    getResourceString(menuItem.i18nId)
                                                ),
                                                color = if (sortTypePreference == menuItem) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    Color.Unspecified
                                                }
                                            )
                                        },
                                        onClick = {
                                            if (sortTypePreference == menuItem) {
                                                viewModel.updateSortOrder(
                                                    if (sortOrderPreference == OrderType.ASC) {
                                                        OrderType.DESC
                                                    } else {
                                                        OrderType.ASC
                                                    }
                                                )
                                            } else {
                                                viewModel.updateSortType(menuItem)
                                            }
                                        },
                                        leadingIcon = {
                                            if (sortTypePreference == menuItem) {
                                                Icon(
                                                    painterResource(
                                                        id = if (sortOrderPreference == OrderType.ASC) {
                                                            R.drawable.sortascending
                                                        } else {
                                                            R.drawable.sortdescending
                                                        }
                                                    ),
                                                    contentDescription = getResourceString(menuItem.i18nId),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FabButton(
                        showMenus = uiState.openFabMenus,
                        openAddDialog = viewModel::openAddDialog,
                        openFabMenus = viewModel::openFabMenus,
                        closeFabMenus = viewModel::closeFabMenus,
                        getResourceString = getResourceString,
                    )
                }) { innerPadding ->
                // main contents
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Breadcrumbs(
                            path = uiState.path.drop(1),
                            onHomeClick = viewModel::homeFolder,
                            onPathClick = viewModel::intoFolder
                        )
                        if (!viewModel.isHomeFolder()) {
                            PathBackButton(viewModel::outFolder)
                        }
                    }
                    if (!uiState.isCompleted) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getResourceString(R.string.loading),
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    } else {
                        if (fileDescriptionItems.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getResourceString(R.string.no_data),
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(fileDescriptionItems) { item ->
                                    FileDescriptionListItem(
                                        item,
                                        onRenameClick = {
                                            viewModel.setUpdatingItem(item)
                                            viewModel.setName(item.name)
                                            viewModel.openRenameDialog()
                                        },
                                        onPinClick = {
                                            viewModel.pinFileDescription(item)
                                        },
                                        onDeleteClick = {
                                            viewModel.setUpdatingItem(item)
                                            viewModel.openDeleteDialog()
                                        },
                                        onClick = {
                                            if (item.type == FileDescriptionType.TABLE.name) {
                                                navHostController.navigate("tableScreen/${item.id}/${item.name}")
                                            } else {
                                                viewModel.intoFolder(item)
                                            }
                                        },
                                        whenAnimationFinished = true,
                                        getResourceString = getResourceString
                                    )
                                }
                            }
                        }
                    }
                }
                // dialogs
                if (uiState.openAddDialog) {
                    TextFieldDialog(
                        title = getResourceString(R.string.new_) + " " + getResourceString(uiState.type.i18nId),
                        value = uiState.name,
                        focusRequester = focusRequester,
                        onDismissRequest = { viewModel.closeDialog() },
                        onConfirm = {
                            if (viewModel.checkFileDescriptionExistInCurrentFolder(it)) {
                                Toast.makeText(
                                    context,
                                    getResourceString(R.string.same_name_file_tips),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.createFileDescription(it)
                                viewModel.closeDialog()
                            }
                        },
                        getResourceString = getResourceString
                    )
                }
                if (uiState.openRenameDialog) {
                    TextFieldDialog(
                        title = getResourceString(R.string.rename),
                        value = uiState.name,
                        focusRequester = focusRequester,
                        onDismissRequest = { viewModel.closeDialog() },
                        onConfirm = {
                            viewModel.renameFileDescription(it)
                            viewModel.closeDialog()
                        },
                        getResourceString = getResourceString
                    )
                }
                if (uiState.openDeleteDialog) {
                    DeleteDialog(
                        title = "${getResourceString(R.string.delete)} ${uiState.updatingItem?.name}?",
                        onCancel = { viewModel.closeDialog() },
                        onConfirm = {
                            viewModel.deleteFileDescription()
                            viewModel.closeDialog()
                        },
                        getResourceString = getResourceString
                    )
                }
            }
        }
    }
}