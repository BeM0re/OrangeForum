package ru.be_more.orange_forum.presentation.screens.posting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.composeViews.IconPickerView

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostingScreen(
    viewModel: PostingViewModel,
) {
    val isOpSelected by viewModel.isOpSelected.collectAsState()

    val isSubjectEnabled by viewModel.isSubjectEnabled.collectAsState()
    val isSubjectVisible by viewModel.isSubjectVisible.collectAsState()
    val subject by viewModel.subject.collectAsState()

    val isEmailVisible by viewModel.isEmailVisible.collectAsState()
    val email by viewModel.email.collectAsState()
    val isSageEnabled by viewModel.isSageEnabled.collectAsState()
    val isSageSelected by viewModel.isSageSelected.collectAsState()

    val isNameEnabled by viewModel.isNameEnabled.collectAsState()
    val isNameVisible by viewModel.isNameVisible.collectAsState()
    val name by viewModel.name.collectAsState()

    val isTagEnabled by viewModel.isTagEnabled.collectAsState()
    val isTagVisible by viewModel.isTagVisible.collectAsState()
    val tag by viewModel.tag.collectAsState()

    val isIconEnabled by viewModel.isIconEnabled.collectAsState()
    val isIconVisible by viewModel.isIconVisible.collectAsState()
    val iconTitle by viewModel.iconTitle.collectAsState()
    val iconUrl by viewModel.iconUrl.collectAsState()

    val comment by viewModel.comment.collectAsState()

    val captchaUrl by viewModel.captchaUrl.collectAsState()
    val captcha by viewModel.captcha.collectAsState()

    val isIconListVisible by viewModel.isIconListVisible.collectAsState()

    Box(
        Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
            ) {
                if (isSageEnabled)
                    TextButton(
                        onClick = { viewModel.onSageClick() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.sage),
                            color =
                            if (isSageSelected) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.wrapContentSize()
                        )
                    }

                TextButton(
                    onClick = { viewModel.onOpClick() }
                ) {
                    Text(
                        text = stringResource(id = R.string.op),
                        color =
                        if (isOpSelected) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.wrapContentSize()
                    )
                }

                if (isSubjectEnabled)
                    TextButton(
                        onClick = { viewModel.onSubjectClick() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.subject),
                            color =
                            if (isSubjectVisible) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.wrapContentSize()
                        )
                    }

                TextButton(
                    onClick = { viewModel.onEmailClick() }
                ) {
                    Text(
                        text = stringResource(id = R.string.email),
                        color =
                        if (isEmailVisible) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.wrapContentSize()
                    )
                }

                if (isNameEnabled)
                    TextButton(
                        onClick = { viewModel.onNameClick() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.name),
                            color =
                            if (isNameVisible) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.wrapContentSize()
                        )
                    }

                if (isTagEnabled)
                    TextButton(
                        onClick = { viewModel.onTagClick() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.tag),
                            color =
                            if (isTagVisible) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.wrapContentSize()
                        )
                    }

                if (isIconEnabled)
                    TextButton(
                        onClick = { viewModel.onIconClick() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.icon),
                            color =
                            if (isIconVisible) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.wrapContentSize()
                        )
                    }
            }

            if (isSubjectVisible)
                TextField(
                    value = subject,
                    placeholder = { Text(stringResource(id = R.string.subject)) },
                    onValueChange = { viewModel.onSubjectEdit(it) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

            if (isEmailVisible && !isSageSelected)
                TextField(
                    value = email,
                    placeholder = { Text(stringResource(id = R.string.email)) },
                    onValueChange = { viewModel.onEmailEdit(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

            if (isNameVisible)
                TextField(
                    value = name,
                    placeholder = { Text(stringResource(id = R.string.name)) },
                    onValueChange = { viewModel.onNameEdit(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

            if (isTagVisible)
                TextField(
                    value = tag,
                    placeholder = { Text(stringResource(id = R.string.tag)) },
                    onValueChange = { viewModel.onTagEdit(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { viewModel.onIconPickerClicked() }
                )

            if (isIconVisible)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onIconPickerClicked() }
                ) {
                    GlideImage(
                        model = iconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp, 0.dp)
                            .size(32.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = iconTitle.ifEmpty { stringResource(id = R.string.select_icon) },
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .align(Alignment.CenterVertically)
                    )
                    IconButton(
                        onClick = { viewModel.onIconClear() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_clear_24),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null
                        )
                    }
                }

            TextField(
                value = comment,
                placeholder = { Text(stringResource(id = R.string.response_form_comment_field_hint)) },
                onValueChange = { viewModel.onCommentEdit(it) },
                modifier = Modifier.fillMaxWidth()
            )

            GlideImage(
                contentScale = ContentScale.FillWidth,
                model = captchaUrl,
                contentDescription = null
            )

            TextField(
                value = captcha,
                placeholder = { Text(stringResource(id = R.string.captcha)) },
                onValueChange = { viewModel.onCaptchaEdit(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { viewModel.onSendClicked() }
            ) {
                Text(
                    text = "Send",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }

    if (isIconListVisible) {
        Dialog(
            onDismissRequest = { viewModel.onIconDismiss() },
        ) {
            /*BackPressHandler(
                onBackPressed = { onBack() }
            )*/

            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(viewModel.iconList) { listItem ->
                        IconPickerView(
                            title = listItem.icon.name,
                            url = listItem.icon.url,
                            modifier = Modifier
                                .padding(8.dp, 0.dp)
                                .clickable { listItem.onClick(listItem.icon) }
                        )
                    }
                }
            }
        }
    }
}
