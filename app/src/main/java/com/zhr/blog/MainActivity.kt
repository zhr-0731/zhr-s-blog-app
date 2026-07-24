package com.zhr.blog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zhr.blog.ui.detail.ArticleDetailScreen
import com.zhr.blog.ui.list.ArticleListScreen
import com.zhr.blog.ui.theme.BlogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlogTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "list") {
                    composable("list") {
                        ArticleListScreen { url ->
                            navController.navigate("detail/$url")
                        }
                    }
                    composable(
                        route = "detail/{url}",
                        arguments = listOf(navArgument("url") { defaultValue = "" })
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString("url") ?: ""
                        ArticleDetailScreen(url)
                    }
                }
            }
        }
    }
}