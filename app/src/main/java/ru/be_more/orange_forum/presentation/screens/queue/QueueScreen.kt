package ru.be_more.orange_forum.presentation.screens.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ShortBoardItemView
import ru.be_more.orange_forum.presentation.composeViews.ShortThreadItemView
import ru.be_more.orange_forum.presentation.data.ShortBoardInitArgs
import ru.be_more.orange_forum.presentation.data.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.screens.base.Screen

@Composable
fun QueueScreen(
    viewModel: QueueViewModel,
    onNavigateToBoard: (String) -> Unit,
    onNavigateToThread: (String, Int) -> Unit,
) {
    with(viewModel) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight(),
            topBar = {
                AppBarView(
                    text = stringResource(id = R.string.navigation_queue),
                    isSearchVisible = false,
                ) {
                    DvachIcon(
                        painter = painterResource(id = R.drawable.ic_delete_outline_24),
                        modifier = Modifier
                            .clickable { clear() }
                            .padding(8.dp)
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(items) { listItem ->
                    when (listItem) {
                        is ShortBoardInitArgs ->
                            ShortBoardItemView(listItem, onNavigateToBoard)
                        is ShortThreadInitArgs ->
                            ShortThreadItemView(listItem, onNavigateToThread)
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.queueScreen(
    onNavigateToBoard: (String) -> Unit,
    onNavigateToThread: (String, Int) -> Unit,
) {
    composable(route = Screen.Queue.route) {
        QueueScreen(
            onNavigateToBoard = onNavigateToBoard,
            onNavigateToThread = onNavigateToThread,
            viewModel = koinViewModel()
        )
    }
}



