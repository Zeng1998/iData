package com.zxc.idata.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zxc.idata.R
import kotlinx.coroutines.launch

data class DrawerMenuItem(
    val label: String,
    val icon: Int,
    val route: String
)


@Composable
fun NavigationDrawerLayout(
    selectedRoute: String,
    drawerState: DrawerState,
    navHostController: NavHostController,
    getResourceString: (Int) -> String,
    innerContent: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    // 底部返回按钮关闭 drawer
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(256.dp),
            ) {
                Text(
                    stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(16.dp)
                        .height(32.dp)
                )
                HorizontalDivider()
                val items = arrayListOf(
                    DrawerMenuItem(getResourceString(R.string.home), R.drawable.home, "homeScreen"),
                    DrawerMenuItem(
                        "${getResourceString(R.string.template)} (WIP)",
                        R.drawable.typepattern,
                        "template"
                    ),
                    DrawerMenuItem(
                        "${getResourceString(R.string.cloud)} (WIP)",
                        R.drawable.cloud,
                        "cloud"
                    ),
                    DrawerMenuItem(
                        getResourceString(R.string.settings),
                        R.drawable.settings,
                        "settingsScreen"
                    )
                )
                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.label) },
                        selected = item.route == selectedRoute,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                // 确保 close 之后再跳转，否则按键返回时 drawer 仍然打开着
                                navHostController.navigate(item.route)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        shape = RectangleShape
                    )
                }
            }
        },
    ) {
        innerContent()
    }
}