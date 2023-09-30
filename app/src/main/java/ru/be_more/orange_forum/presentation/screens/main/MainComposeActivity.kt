package ru.be_more.orange_forum.presentation.screens.main

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.screens.base.Screen
import ru.be_more.orange_forum.presentation.screens.board.boardScreen
import ru.be_more.orange_forum.presentation.screens.category.categoryScreen
import ru.be_more.orange_forum.presentation.screens.favorite.favoriteScreen
import ru.be_more.orange_forum.presentation.screens.queue.queueScreen
import ru.be_more.orange_forum.presentation.screens.thread.threadScreen
import ru.be_more.orange_forum.presentation.theme.DvachTheme

class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      /*  startKoin{
            androidLogger()
            androidContext(this@MainComposeActivity)
            modules(listOf(
                appModule,
                viewModelModule,
                repositoryModule,
                storageModule,
                databaseModule,
                interactorModule,
                networkModule
            ))
        }*/

        setContent {
            DvachTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Ui()
                    }
                }
            }
        }
    }

    @Composable
    fun Ui() {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { NavBar(navController) },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) { innerPadding ->
            Host(navController, innerPadding)
        }

     }

    @Composable
    private fun NavBar(navController: NavController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val bottomMenuItemList = listOf(
            Screen.Category,
            Screen.Board,
            Screen.Thread,
            Screen.Favorite,
            Screen.Queue,
        )

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            bottomMenuItemList.forEach { menuItem ->
                NavigationBarItem(
                    enabled = menuItem.isAlwaysActive || navController.currentBackStack.value
                        .any { it.destination.route?.contains(menuItem.route) == true },
                    selected = currentDestination?.hierarchy
                        ?.any { it.route?.contains(menuItem.route) == true } == true,
                    onClick = {
                        Toast.makeText(
                            this@MainComposeActivity,
                            this@MainComposeActivity.resources.getString(menuItem.title),
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(menuItem.route) {
                            /*popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }*/
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = menuItem.icon),
                            contentDescription = stringResource(id = menuItem.title),
                        )
                    },
                    label = {
                        Text(text = stringResource(id = menuItem.title))
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        indicatorColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                )
            }
        }
    }

    @Composable
    fun Host(navController: NavHostController, innerPadding: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = Screen.Category.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            categoryScreen { boardId ->
                navController.navigate(
                    route = Screen.Board.route + "?boardId=$boardId"
                ) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            boardScreen { boardId, threadNum ->
                navController.navigate(
                    route = Screen.Thread.route + "?boardId=$boardId" + "?threadNum=$threadNum"
                ) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            threadScreen()
            queueScreen()
            favoriteScreen()
            //todo setting
        }
    }

    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        name = "Light Mode"
    )
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        name = "Dark Mode",
    )
    @Composable
    fun MainPreview() {
        DvachTheme{
            Ui()
        }
    }
}

