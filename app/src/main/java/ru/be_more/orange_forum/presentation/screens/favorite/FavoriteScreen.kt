package ru.be_more.orange_forum.presentation.screens.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.be_more.orange_forum.R
import ru.be_more.ui.model.NavigationState
import ru.be_more.ui.composeViews.AppBarView
import ru.be_more.ui.composeViews.ShortBoardItemView
import ru.be_more.ui.composeViews.ShortThreadItemView
import ru.be_more.ui.composeViews.initArgs.ShortBoardInitArgs
import ru.be_more.ui.composeViews.initArgs.ShortThreadInitArgs

@Composable
fun FavoriteScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: FavoriteViewModel = viewModel(factory = viewModelFactory),
    onNavigate: (NavigationState) -> Unit,
) {
    with(viewModel) {
        val itemState = items.collectAsState()

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
                    text = stringResource(id = R.string.navigation_favorites),
                    isSearchVisible = false,
                ) { }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(itemState.value) { listItem ->
                    when (listItem) {
                        is ShortBoardInitArgs ->
                            ShortBoardItemView(listItem)
                        is ShortThreadInitArgs ->
                            ShortThreadItemView(listItem)
                    }
                }
            }
        }
    }
}
