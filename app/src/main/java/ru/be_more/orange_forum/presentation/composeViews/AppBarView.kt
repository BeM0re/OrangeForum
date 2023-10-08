package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun AppBarView(
    text: String,
    modifier: Modifier = Modifier,
    isSearchVisible: Boolean = false,
    onSearch: (String) -> Unit = {},
    iconListComposable: @Composable () -> Unit,
) {
    var isSearchFieldVisible by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .height(56.dp)

    ) {
        if (isSearchFieldVisible) SearchView(
            SearchViewInitArgs(
                query = "",
                onTextChanged = { onSearch(it) },
                placeholder = "Search",
                onClose = {
                    isSearchFieldVisible = false
                    onSearch("")
                }
            ),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                .wrapContentHeight(),
            )
        else Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (isSearchVisible)
            DvachIcon(
                painter = painterResource(id = R.drawable.ic_search),
                Modifier
                    .clickable { isSearchFieldVisible = !isSearchFieldVisible }
                    .padding(8.dp)
            )
        iconListComposable()
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
        AppBarView("Title", isSearchVisible = true) {
            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
                DvachIcon(painter = painterResource(id = R.drawable.ic_settings_accent_24dp))
                DvachIcon(painter = painterResource(id = R.drawable.ic_favorite_accent_24dp))
            }
        }
    }
}