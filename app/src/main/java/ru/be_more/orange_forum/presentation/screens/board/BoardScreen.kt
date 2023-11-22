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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.ContentStateView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialog
import ru.be_more.orange_forum.presentation.composeViews.initArgs.HiddenOpPostInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.OpPostInitArgs
import ru.be_more.orange_forum.presentation.model.ContentState
import ru.be_more.orange_forum.presentation.model.NavigationState
import java.lang.IllegalStateException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoardScreen(
    viewModel: BoardViewModel,
    onNavigate: (NavigationState) -> Unit,
) {
    with(viewModel) {
        val state = contentState.collectAsState()

        LaunchedEffect(key1 = true) {
            navState.collect { navigate ->
                onNavigate(navigate)
            }
        }

        val refreshState = rememberPullRefreshState(isLoading, ::refresh)
        //todo доделать когда будут мануалы

        Scaffold(
            modifier = Modifier
                .fillMaxHeight(),
            topBar = {
                AppBarView(
                    text = screenTitle,
                    isSearchVisible = true,
                    onSearch = { search(it) }
                ) {
                    DvachIcon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_favorite_accent_24dp
                            else R.drawable.ic_favorite_border_accent_24dp
                        ),
                        Modifier
                            .clickable { setFavorite() }
                            .padding(8.dp)
                    )
                }
            },
            floatingActionButton = {
                if (state.value == ContentState.Content)
                    FloatingActionButton(
                        onClick = { onNewThreadClicked() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_create_black_24dp),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
            }
        ) { paddingValues ->
            ContentStateView(state = state.value) {
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
                                )

                            is HiddenOpPostInitArgs ->
                                OpPostHiddenView(
                                    args = listItem,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                                )

                            else ->
                                //todo выделить отдельный супер-подкласс
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
}


