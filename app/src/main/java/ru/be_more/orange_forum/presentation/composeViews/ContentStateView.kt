package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.model.ContentState
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun ContentStateView(
    state: ContentState,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (state) {
            ContentState.Loading -> {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            is ContentState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_clear_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = state.message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(24.dp, 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            ContentState.Content -> content.invoke()
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
fun ContentStateViewErrorPreview() {
    DvachTheme(dynamicColor = false) {
        ContentStateView(
            state = ContentState.Error("Error")
        ) {}
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
fun ContentStateViewLoadingPreview() {
    DvachTheme(dynamicColor = false) {
        ContentStateView(
            state = ContentState.Loading
        ) {}
    }
}