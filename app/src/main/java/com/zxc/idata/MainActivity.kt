package com.zxc.idata

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zxc.idata.home.HomeScreen
import com.zxc.idata.settings.SettingsScreen
import com.zxc.idata.table.TableScreen
import dagger.hilt.android.AndroidEntryPoint

// for hilt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController, startDestination = "homeScreen",
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                },
            ) {
                composable("homeScreen") {
                    HomeScreen(navController)
                }
                composable("settingsScreen") {
                    SettingsScreen(navController)
                }
                composable(
                    "tableScreen/{tableId}/{tableName}",
                    arguments = listOf(
                        navArgument("tableId") { type = NavType.LongType },
                        navArgument("tableName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    TableScreen(
                        navController,
                        backStackEntry.arguments?.getLong("tableId") ?: -1,
                        backStackEntry.arguments?.getString("tableName") ?: ""
                    )
                }
            }
        }
    }
}

