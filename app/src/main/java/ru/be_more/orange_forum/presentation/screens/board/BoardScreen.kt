package ru.be_more.orange_forum.presentation.screens.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialog
import java.lang.IllegalStateException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoardScreen(
    viewModel: BoardViewModel,
    onNavigateToThread: (String, Int) -> Unit
) {
    with(viewModel) {
        val refreshState = rememberPullRefreshState(isLoading, ::refresh)
        //todo доделать когда будут мануалы

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
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxHeight()
                    .pullRefresh(refreshState, enabled = true),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(items) { listItem ->
                    when (listItem) {
                        is OpPostInitArgs ->
                            OpPostView(
                                args = listItem,
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                            ) { boardId, threadNum ->
                                onNavigateToThread(boardId, threadNum)
                            }
                        is HiddenOpPostInitArgs ->
                            OpPostHiddenView(
                                args = listItem,
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                            )
                        else ->
                            throw IllegalStateException("BoardScreen.items is illegal type")
                    }
                }
            }

            modalContent?.let {
                ModalContentDialog(args = it)
            }

        }
    }
}


