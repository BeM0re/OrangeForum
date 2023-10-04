package ru.be_more.orange_forum.presentation.screens.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.window.Dialog
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
import ru.be_more.orange_forum.presentation.screens.base.Screen

@Composable
fun BoardScreen(
    viewModel: BoardViewModel,
    onNavigateToThread: (String, Int) -> Unit
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
                    DvachIcon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_favorite_accent_24dp
                            else R.drawable.ic_favorite_border_accent_24dp
                        ),
                        Modifier.clickable { setFavorite() }
                    )
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
                items(viewModel.items) { listItem ->
                    OpPostView(
                        args = listItem,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    ) { boardId, threadNum ->
                        onNavigateToThread(boardId, threadNum)
                    }
                }
            }

            modalContent?.let {
                ModalContentDialog(args = it)
            }
        }
    }
}

fun NavGraphBuilder.boardScreen(onNavigateToThread: (String, Int) -> Unit) {
    composable(
        route = Screen.Board.route + "?boardId={boardId}",
        arguments = listOf(
            navArgument(name = "boardId") {
                type = NavType.StringType
                nullable = true
            },
        )
    ) { entry ->
        val id = entry.arguments?.getString("boardId") ?: return@composable
        BoardScreen(
            viewModel = koinViewModel(
                parameters = { parametersOf(id) }
            ),
            onNavigateToThread = onNavigateToThread,
        )
    }
}



