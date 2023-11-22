package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormattedDateTime(
    instant: Instant,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter
        .ofPattern("dd MMM yyyy, hh:mm")

    val localDateTime = instant
        .atZone(TimeZone.getDefault().toZoneId())
        .toLocalDateTime()
        .format(formatter)

    Text(
        text = localDateTime,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
fun FormattedDateTimePreview() {
    DvachTheme(dynamicColor = false) {
        FormattedDateTime(
            instant = Instant.ofEpochSecond(1700619604)
        )
    }
}