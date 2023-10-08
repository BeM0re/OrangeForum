package ru.be_more.orange_forum.presentation.screens.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialog
import ru.be_more.orange_forum.presentation.composeViews.PostView
import ru.be_more.orange_forum.presentation.screens.base.Screen
import ru.be_more.orange_forum.presentation.screens.board.OpPostView

@Composable
fun ThreadScreen(
    viewModel: ThreadViewModel
) {
    with(viewModel) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight(),
            topBar = {
                AppBarView(
                    text = screenTitle,
                    isSearchVisible = false,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DvachIcon(
                            painter = painterResource(
                                if (isFavorite) R.drawable.ic_favorite_accent_24dp
                                else R.drawable.ic_favorite_border_accent_24dp
                            ),
                            Modifier.clickable { setFavorite() }
                        )
                        DvachIcon(
                            painter = painterResource(
                                if (isQueued) R.drawable.ic_queue_added_accent_24
                                else R.drawable.ic_queue_add_accent_24
                            ),
                            Modifier.clickable { setQueued() }
                        )
                        DvachIcon(
                            painter = painterResource(
                                if (isDownloaded) R.drawable.ic_cloud_done_black_24dp
                                else R.drawable.ic_cloud_download_accent_24dp
                            ),
                            Modifier.clickable { download() }
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(items) { listItem ->
                    PostView(
                        args = listItem,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            modalContent?.let {
                ModalContentDialog(args = it)
            }
        }
    }
}

fun NavGraphBuilder.threadScreen() {
    composable(
        route = Screen.Thread.route + "?boardId={boardId}" + "?threadNum={threadNum}",
        arguments = listOf(
            navArgument(name = "boardId") {
                type = NavType.StringType
                nullable = true
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
            viewModel = koinViewModel(
                parameters = {
                    parametersOf(boardId, threadNum)
                }
            )
        )
    }
}



