package ru.be_more.orange_forum.presentation.screens.main

import android.content.res.Configuration
import android.os.Bundle
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.presentation.screens.base.Screen
import ru.be_more.orange_forum.presentation.screens.board.BoardScreen
import ru.be_more.orange_forum.presentation.screens.category.CategoryScreen
import ru.be_more.orange_forum.presentation.screens.favorite.FavoriteScreen
import ru.be_more.orange_forum.presentation.screens.queue.QueueScreen
import ru.be_more.orange_forum.presentation.screens.thread.ThreadScreen
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import ru.be_more.orange_forum.utils.ViewModelProvider

class MainActivity : ComponentActivity() {

    private val vmProvider: ViewModelProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        navController.navigate(menuItem.route) {
                            launchSingleTop = true
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
            composable(route = Screen.Category.route) {
                CategoryScreen(
                    viewModel = vmProvider.getVM(),
                    onNavigateToBoard = { boardId ->
                        navController.navigate(
                            route = Screen.Board.route + "?boardId=$boardId"
                        ) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            //board with params
            composable(
                route = Screen.Board.route + "?boardId={boardId}",
                arguments = listOf(
                    navArgument(name = "boardId") {
                        type = NavType.StringType
                        nullable = false
                    },
                )
            ) { entry ->
                val id = entry.arguments?.getString("boardId") ?: return@composable
                BoardScreen(
                    viewModel = vmProvider.getVM(id),
                    onNavigateToThread = { boardId, threadNum ->
                        navController.navigate(
                            route = Screen.Thread.route + "?boardId=$boardId" + "?threadNum=$threadNum"
                        ) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                )
            }

            //board w/o params
            composable(
                route = Screen.Board.route,
            ) {
                BoardScreen(
                    viewModel = vmProvider.getVM(),
                    onNavigateToThread = { boardId, threadNum ->
                        navController.navigate(
                            route = Screen.Thread.route + "?boardId=$boardId" + "?threadNum=$threadNum"
                        ) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                )
            }

            //thread with params
            composable(
                route = Screen.Thread.route + "?boardId={boardId}" + "?threadNum={threadNum}",
                arguments = listOf(
                    navArgument(name = "boardId") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(name = "threadNum") {
                        type = NavType.IntType
                        nullable = false
                    },
                )
            ) { entry ->
                val boardId = entry.arguments?.getString("boardId") ?: ""
                val threadNum = entry.arguments?.getInt("threadNum") ?: 0

                ThreadScreen(vmProvider.getVM(boardId, threadNum))
            }

            //thread w/o params
            composable(
                route = Screen.Thread.route,
            ) {
                ThreadScreen(vmProvider.getVM())
            }

            //Queue
            composable(route = Screen.Queue.route) {
                QueueScreen(
                    onNavigateToBoard = { boardId ->
                        navController.navigate(
                            route = Screen.Board.route + "?boardId=$boardId"
                        ) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToThread = { boardId, threadNum ->
                        navController.navigate(
                            route = Screen.Thread.route + "?boardId=$boardId" + "?threadNum=$threadNum"
                        ) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    viewModel = vmProvider.getVM()
                )
            }

            //favorite
            composable(route = Screen.Favorite.route) {
                FavoriteScreen(
                    onNavigateToBoard = { boardId ->
                        navController.navigate(
                            route = Screen.Board.route + "?boardId=$boardId"
                        ) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToThread = { boardId, threadNum ->
                        navController.navigate(
                            route = Screen.Thread.route + "?boardId=$boardId" + "?threadNum=$threadNum"
                        ) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    viewModel = vmProvider.getVM()
                )
            }

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

