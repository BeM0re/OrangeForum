package ru.be_more.orange_forum.presentation.screens.thread

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialog
import ru.be_more.orange_forum.presentation.composeViews.PostView
import ru.be_more.orange_forum.presentation.screens.base.NavigationState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThreadScreen(
    viewModel: ThreadViewModel,
    onNavigate: (NavigationState) -> Unit,
) {
    with(viewModel) {

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var firstVisibleItemIndex = 0
        val listFirstVisibleItemState = remember { derivedStateOf { listState.firstVisibleItemIndex } }

        LaunchedEffect(key1 = true) {
            navState.collect { navigate ->
                onNavigate(navigate)
            }
        }

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
                            Modifier
                                .clickable { setFavorite() }
                                .padding(8.dp)
                        )
                        DvachIcon(
                            painter = painterResource(
                                if (isQueued) R.drawable.ic_queue_added_accent_24
                                else R.drawable.ic_queue_add_accent_24
                            ),
                            Modifier
                                .clickable { setQueued() }
                                .padding(8.dp)
                        )
                        DvachIcon(
                            painter = painterResource(
                                if (isDownloaded) R.drawable.ic_cloud_done_black_24dp
                                else R.drawable.ic_cloud_download_accent_24dp
                            ),
                            Modifier
                                .clickable { download() }
                                .padding(8.dp)
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onReplyClicked() }
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
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
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

                if (firstVisibleItemIndex < listFirstVisibleItemState.value) {
                    firstVisibleItemIndex = listFirstVisibleItemState.value
                    DvachIcon(
                        painter = painterResource(id = R.drawable.ic_keyboard_arrow_down_white_24dp),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
                                }
                            }
                            .padding(8.dp)
                    )
                } else {
                    firstVisibleItemIndex = listFirstVisibleItemState.value
                    DvachIcon(
                        painter = painterResource(id = R.drawable.ic_keyboard_arrow_up_white_24dp),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }

            modalContent?.let {
                ModalContentDialog(args = it)
            }
        }
    }
}



