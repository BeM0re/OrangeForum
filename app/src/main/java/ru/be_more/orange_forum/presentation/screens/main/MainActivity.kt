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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.App
import ru.be_more.ui.composeViews.NavigationIcon
import ru.be_more.ui.model.NavigationState
import ru.be_more.orange_forum.presentation.screens.base.Screen
import ru.be_more.orange_forum.presentation.screens.base.ScreenRout
import ru.be_more.orange_forum.presentation.screens.board.BoardScreen
import ru.be_more.orange_forum.presentation.screens.board.BoardViewModel
import ru.be_more.orange_forum.presentation.screens.category.CategoryScreen
import ru.be_more.orange_forum.presentation.screens.favorite.FavoriteScreen
import ru.be_more.orange_forum.presentation.screens.queue.QueueScreen
import ru.be_more.orange_forum.presentation.screens.posting.PostingScreen
import ru.be_more.orange_forum.presentation.screens.posting.PostingViewModel
import ru.be_more.orange_forum.presentation.screens.thread.ThreadScreen
import ru.be_more.orange_forum.presentation.screens.thread.ThreadViewModel
import ru.be_more.ui.theme.DvachTheme
import ru.be_more.orange_forum.utils.permissions.registerPermissionsLauncher
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var commonVmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var boardVmFactory: BoardViewModel.Factory.AFactory

    @Inject
    lateinit var threadVmFactory: ThreadViewModel.Factory.AFactory

    @Inject
    lateinit var postVmFactory: PostingViewModel.Factory.AFactory

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

        (application as App)
            .getAppComponent()
            .inject(this)

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
                        .any { it.destination.route?.contains(menuItem.route.toString()) == true },
                    selected = currentDestination?.hierarchy
                        ?.any { it.route?.contains(menuItem.route.toString()) == true } == true,
                    onClick = {
                        navController.navigate(menuItem.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        if (hasNewFavoriteMessage.value)
                            NavigationIcon(
                                painter = painterResource(id = menuItem.icon),
                                isMarked = menuItem is Screen.Favorite,
                                contentDescription = stringResource(id = menuItem.title),
                            )
                        else
                            NavigationIcon(
                                painter = painterResource(id = menuItem.icon),
                                isMarked = false,
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
                    viewModelFactory = commonVmFactory,
                    onNavigate = { navigate(navController, it) }
                )
            }

            //board with params
            composable<ScreenRout.BoardScreen> { entry ->
                val args = entry.toRoute<ScreenRout.BoardScreen>()
                BoardScreen(
                    viewModelFactory = boardVmFactory.create(args.imageboardType, args.boardId),
                    onNavigate = { navigate(navController, it) }
                )
            }

            //thread with params
            composable<ScreenRout.ThreadScreen> { entry ->
                val args = entry.toRoute<ScreenRout.ThreadScreen>()
                ThreadScreen(
                    viewModelFactory = threadVmFactory.create(args.imageboardType, args.boardId, args.threadNum),
                    onNavigate = { navigate(navController, it) }
                )
            }

            //Queue
            composable<ScreenRout.QueueScreen> {
                QueueScreen(
                    viewModelFactory = commonVmFactory,
                    onNavigate = { navigate(navController, it) }
                )
            }

            //favorite
            composable<ScreenRout.FavoriteScreen> {
                FavoriteScreen(
                    viewModelFactory = commonVmFactory,
                    onNavigate = { navigate(navController, it) }
                )
            }

            //reply into a thread
            composable<ScreenRout.PostingScreen> { entry ->
                val args = entry.toRoute<ScreenRout.PostingScreen>()
                PostingScreen(
                    viewModelFactory = postVmFactory.create(
                        imageboardType = args.imageboardType,
                        boardId = args.boardId,
                        threadNum = args.threadNum,
                        additionalString = args.additionalString ?: ""
                    ),
                    viewModel = viewModel(modelClass = PostingViewModel::class),
                )
            }

            //create a new thread
            composable<ScreenRout.PostingScreen> { entry ->
                val args = entry.toRoute<ScreenRout.PostingScreen>()
                PostingScreen(
                    viewModelFactory = postVmFactory.create(
                        imageboardType = args.imageboardType,
                        boardId = args.boardId,
                        threadNum = -1,
                        additionalString = ""
                    ),
                    viewModel = viewModel(modelClass = PostingViewModel::class),
                )
            }

            //todo setting
        }
    }

    //todo move into VM and pass params as InitArgs : Parselable
    private fun navigate(navController: NavHostController, navState: NavigationState) =
        when (navState) {
            is NavigationState.NavigateToBoard -> {
                navController.navigate(
                    ScreenRout.BoardScreen(
                        imageboardType = navState.imageboardType.name,
                        boardId = navState.boardId
                    )
                )
            }

            is NavigationState.NavigateToThread -> {
                navController.navigate(
                    ScreenRout.ThreadScreen(
                        imageboardType = navState.imageboardType.name,
                        boardId = navState.boardId,
                        threadNum = navState.threadNum
                    )
                )
            }

            is NavigationState.NavigateToThreadCreating -> {
                navController.navigate(
                    ScreenRout.PostingScreen(
                        imageboardType = navState.imageboardType.name,
                        boardId = navState.boardId
                    )
                )
            }

            is NavigationState.NavigateToReply -> {
                navController.navigate(
                    ScreenRout.PostingScreen(
                        imageboardType = navState.imageboardType.name,
                        boardId = navState.boardId,
                        threadNum = navState.threadNum,
                        additionalString = navState.additionalString
                    )
                )
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

