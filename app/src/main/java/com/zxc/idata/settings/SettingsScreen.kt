package com.zxc.idata.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.zxc.idata.R
import com.zxc.idata.components.NavigationDrawerLayout
import com.zxc.idata.enums.Language
import com.zxc.idata.ui.theme.IDataTheme
import com.zxc.idata.utils.StringResourceUtils
import kotlinx.coroutines.launch

data class SettingItemData(
    val key: String,
    val icon: Painter,
    val onClick: () -> Unit
)

@Composable
fun SettingsItem(key: String, icon: Painter, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(4.dp), shadowElevation = 2.dp,
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                Text(text = key)
            }
            Row {
                Icon(
                    painterResource(id = R.drawable.chevronright),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SettingItemGroup(settingItems: List<SettingItemData>) {
    val itemHeight = 48
    Surface(
        shape = RoundedCornerShape(4.dp), shadowElevation = 2.dp,
        modifier = Modifier
            .height((itemHeight * settingItems.size).dp)
            .fillMaxWidth()
    ) {
        Column {
            settingItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(
                            onClick = item.onClick
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = item.key)
                        }
                        Row {
                            Icon(
                                painterResource(id = R.drawable.chevronright),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                if (index != settingItems.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val stringResourceUtils = StringResourceUtils(context, userPreferences.language)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val getResourceString = stringResourceUtils::getString

    IDataTheme {
        NavigationDrawerLayout(
            selectedRoute = "settingsScreen",
            drawerState= drawerState,
            navHostController = navHostController,
            getResourceString = getResourceString
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        title = {
                            Text(text = getResourceString(R.string.settings))
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    val settingItems = listOf(
//                        SettingItemData(
//                            key = "Language",
//                            icon = painterResource(id = R.drawable.language),
//                            onClick = { viewModel.openBottomSheet() }
//                        ),
//                        SettingItemData(
//                            key = "Default View",
//                            icon = painterResource(id = R.drawable.dataview),
//                            onClick = { viewModel.openBottomSheet() }
//                        )
//                    )
//                    SettingItemGroup(settingItems = settingItems)
                    SettingsItem(
                        key = stringResourceUtils.getString(R.string.language),
                        icon = painterResource(id = R.drawable.language),
                        onClick = {
                            viewModel.setSelectingPreferenceKey("language")
                            viewModel.setOptionIds(Language.entries.map { it.i18nId })
                            viewModel.setOptionNames(Language.entries.map { it.name })
                            viewModel.openBottomSheet()
                        })
                }

                // Sheet content
                if (uiState.openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.closeBottomSheet() },
                        sheetState = bottomSheetState,
                    ) {
                        Column(
                            Modifier
                                .selectableGroup()
                                .padding(bottom = 16.dp)
                        ) {
                            uiState.optionIds.zip(uiState.optionNames).forEach { (id, name) ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = (id == userPreferences.language.i18nId),
                                            onClick = {
                                                viewModel.updatePreference(name)
                                            },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (id == userPreferences.language.i18nId),
                                        onClick = null // null recommended for accessibility with screenreaders
                                    )
                                    Text(
                                        text = stringResourceUtils.getString(id),
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
