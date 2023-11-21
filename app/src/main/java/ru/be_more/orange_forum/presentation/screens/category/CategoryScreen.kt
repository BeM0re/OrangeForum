package ru.be_more.orange_forum.presentation.screens.category

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.AppBarView
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.initArgs.BoardShortListItemViewInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.CategoryListItemViewInitArgs
import ru.be_more.orange_forum.presentation.screens.base.NavigationState
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import java.lang.ref.WeakReference

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    onNavigate: (NavigationState) -> Unit,
) {
    val context = WeakReference(LocalContext.current)

    with(viewModel) {

        LaunchedEffect(key1 = true) {
            navState.collect { navigate ->
                onNavigate(navigate)
            }
        }

        Scaffold(
            topBar = {
                AppBarView(
                    text = stringResource(id = R.string.app_name),
                    isSearchVisible = true,
                    onSearch = { search(it) }
                ) {
                    DvachIcon(
                        painter = painterResource(id = R.drawable.ic_settings_accent_24dp),
                        modifier = Modifier
                            .clickable {
                                Toast.makeText(
                                    context.get(),
                                    "Will be later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .padding(8.dp)
                    )
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(items) { listItem ->
                    when (listItem) {
                        is CategoryListItemViewInitArgs ->
                            CategoryListItem(listItem)

                        is BoardShortListItemViewInitArgs ->
                            BoardShortListItem(listItem)
                    }
                }
            }

        }
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
        CategoryScreen(koinViewModel()) {}
    }
}