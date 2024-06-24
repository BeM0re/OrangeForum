package ru.be_more.orange_forum.presentation.screens.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.presentation.composeViews.NavigationIcon
import ru.be_more.orange_forum.presentation.model.NavigationState
import ru.be_more.orange_forum.presentation.screens.base.Screen
import ru.be_more.orange_forum.presentation.screens.base.ScreenRout
import ru.be_more.orange_forum.presentation.screens.board.BoardScreen
import ru.be_more.orange_forum.presentation.screens.category.CategoryScreen
import ru.be_more.orange_forum.presentation.screens.favorite.FavoriteScreen
import ru.be_more.orange_forum.presentation.screens.queue.QueueScreen
import ru.be_more.orange_forum.presentation.screens.posting.PostingScreen
import ru.be_more.orange_forum.presentation.screens.thread.ThreadScreen
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import ru.be_more.orange_forum.utils.ViewModelProvider
import ru.be_more.orange_forum.utils.permissions.registerPermissionsLauncher

class MainActivity : ComponentActivity() {

    private val vmProvider: ViewModelProvider by inject()
    private val viewModel: MainViewModel by inject()

    private val permissionsLauncher = registerPermissionsLauncher {

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestPermissionsFlow.collect {
                    permissionsLauncher.request(it)
                }
            }
        }

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

    @RequiresApi(Build.VERSION_CODES.O)
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
        val hasNewFavoriteMessage = viewModel.hasFavoriteNewMessageFlow.collectAsState()

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
                        .any { it.destination.route?.contains(menuItem.route::class.java.toString()) == true },
                    selected = currentDestination?.hierarchy
                        ?.any { it.route?.contains(menuItem.route.toString()) == true } == true,
                    onClick = {
                        navController.navigate(menuItem.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        NavigationIcon(
                            painter = painterResource(id = menuItem.icon),
                            isMarked = menuItem is Screen.Favorite && hasNewFavoriteMessage.value,
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Host(navController: NavHostController, innerPadding: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = Screen.Category.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<ScreenRout.CategoryScreen> {
                CategoryScreen(
                    viewModel = vmProvider.getVM(createNew = false),
                    onNavigate = { navigate(navController, it) }
                )
            }

            //board with params
            composable<ScreenRout.BoardScreen> { entry ->
                val args = entry.toRoute<ScreenRout.BoardScreen>()
                BoardScreen(
                    viewModel = vmProvider.getVM(args.boardId, createNew = true),
                    onNavigate = { navigate(navController, it) }
                )
            }


            //thread with params
            composable<ScreenRout.ThreadScreen> { entry ->
                val args = entry.toRoute<ScreenRout.ThreadScreen>()
                ThreadScreen(
                    viewModel = vmProvider.getVM(args.boardId, args.threadNum, createNew = true),
                    onNavigate = { navigate(navController, it) }
                )
            }
/*            composable(
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

                ThreadScreen(
                    viewModel = vmProvider.getVM(boardId, threadNum, createNew = true),
                    onNavigate = { navigate(navController, it) }
                )
            }

            //thread w/o params
            composable(
                route = Screen.Thread.route,
            ) {
                ThreadScreen(
                    viewModel = vmProvider.getVM(createNew = false),
                    onNavigate = { navigate(navController, it) }
                )
            }*/

            //Queue
            composable<ScreenRout.QueueScreen> {
                QueueScreen(
                    viewModel = vmProvider.getVM(createNew = true),
                    onNavigate = { navigate(navController, it) }
                )
            }
            /*composable(route = Screen.Queue.route) {
                QueueScreen(
                    viewModel = vmProvider.getVM(createNew = false),
                    onNavigate = { navigate(navController, it) }
                )
            }*/

            //favorite
            composable<ScreenRout.FavoriteScreen> {
                FavoriteScreen(
                    viewModel = vmProvider.getVM(createNew = true),
                    onNavigate = { navigate(navController, it) }
                )
            }
            /*composable(route = Screen.Favorite.route) {
                FavoriteScreen(
                    viewModel = vmProvider.getVM(createNew = false),
                    onNavigate = { navigate(navController, it) }
                )
            }*/

            //reply into a thread
            composable<ScreenRout.PostingScreen> { entry ->
                val args = entry.toRoute<ScreenRout.PostingScreen>()
                PostingScreen(
                    viewModel = vmProvider.getVM(args.boardId, args.threadNum, args.additionalString, createNew = true),
                )
            }
/*            composable(
                route = Screen.Posting.route
                        + "?boardId={boardId}"
                        + "?threadNum={threadNum}"
                        + "?additionalString={additionalString}",
                arguments = listOf(
                    navArgument(name = "boardId") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument(name = "threadNum") {
                        type = NavType.IntType
                        nullable = false
                    },
                    navArgument(name = "additionalString") {
                        type = NavType.StringType
                        nullable = false
                    },
                )
            ) { entry ->
                val boardId = entry.arguments?.getString("boardId") ?: ""
                val threadNum = entry.arguments?.getInt("threadNum") ?: 0
                val additionalString = entry.arguments?.getString("additionalString") ?: ""

                PostingScreen(
                    vmProvider.getVM(boardId, threadNum, additionalString, createNew = false)
                )
            }*/

            //create a new thread
            composable<ScreenRout.PostingScreen> { entry ->
                val args = entry.toRoute<ScreenRout.PostingScreen>()
                PostingScreen(
                    viewModel = vmProvider.getVM(args.boardId, -1, createNew = true),
                )
            }
/*            composable(
                route = Screen.Posting.route + "?boardId={boardId}",
                arguments = listOf(
                    navArgument(name = "boardId") {
                        type = NavType.StringType
                        nullable = false
                    },
                )
            ) { entry ->
                val boardId = entry.arguments?.getString("boardId") ?: ""

                PostingScreen(
                    vmProvider.getVM(boardId, -1, createNew = false)
                )
            }*/

            //todo setting
        }
    }

    //todo move into VM and pass params as InitArgs : Parselable
    private fun navigate(navController: NavHostController, navState: NavigationState) =
        when (navState) {
            is NavigationState.NavigateToBoard -> {
                /*navController.navigate(
                    route = Screen.Board.route + "?boardId=${navState.boardId}"
                ) {
                    launchSingleTop = true
                    restoreState = true
                }*/
                navController.navigate(
                    ScreenRout.BoardScreen(boardId = navState.boardId)
                )
            }

            is NavigationState.NavigateToThread -> {
                navController.navigate(
                    ScreenRout.ThreadScreen(boardId = navState.boardId, threadNum = navState.threadNum)
                )
                /*navController.navigate(
                    route = Screen.Thread.route
                            + "?boardId=${navState.boardId}"
                            + "?threadNum=${navState.threadNum}"
                ) {
                    launchSingleTop = true
                    restoreState = false
                }*/
            }

            is NavigationState.NavigateToThreadCreating -> {
                navController.navigate(
                    ScreenRout.PostingScreen(boardId = navState.boardId)
                )
                /*navController.navigate(
                    route = Screen.Posting.route + "?boardId=${navState.boardId}"
                ) {
                    launchSingleTop = true
                    restoreState = false
                }*/
            }

            is NavigationState.NavigateToReply -> {
                navController.navigate(
                    ScreenRout.PostingScreen(
                        boardId = navState.boardId,
                        threadNum = navState.threadNum,
                        additionalString = navState.additionalString
                    )
                )
                /*navController.navigate(
                    route = Screen.Posting.route
                            + "?boardId=${navState.boardId}"
                            + "?threadNum=${navState.threadNum}"
                            + "?additionalString=${navState.additionalString}"
                ) {
                    launchSingleTop = true
                    restoreState = false
                }*/
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
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

