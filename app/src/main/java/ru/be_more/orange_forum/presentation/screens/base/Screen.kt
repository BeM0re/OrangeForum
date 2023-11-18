package ru.be_more.orange_forum.presentation.screens.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.be_more.orange_forum.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val isAlwaysActive: Boolean,
) {
    data object Category : Screen(
        route = "CategoryScreen",
        title = R.string.navigation_category,
        icon = R.drawable.ic_menu_book_accent_24dp,
        isAlwaysActive = true,
    )
    data object Board : Screen(
        route = "BoardScreen",
        title = R.string.navigation_board,
        icon = R.drawable.ic_dashboard_accent_24dp,
        isAlwaysActive = false,
    )
    data object Thread : Screen(
        route = "ThreadScreen",
        title = R.string.navigation_thread,
        icon = R.drawable.ic_chat_accent_24dp,
        isAlwaysActive = false,
    )
    data object Favorite : Screen(
        route = "FavoriteScreen",
        title = R.string.navigation_favorites,
        icon = R.drawable.ic_favorite_border_accent_24dp,
        isAlwaysActive = true,
    )
    data object Queue : Screen(
        route = "QueueScreen",
        title = R.string.navigation_queue,
        icon = R.drawable.ic_list_numbered_accent_24dp,
        isAlwaysActive = true,
    )
    data object Posting : Screen(
        route = "PostingScreen",
        title = R.string.navigation_posting,
        icon = R.drawable.ic_settings_accent_24dp,
        isAlwaysActive = true,
    )
    data object Setting : Screen(
        route = "SettingScreen",
        title = R.string.navigation_settings,
        icon = R.drawable.ic_settings_accent_24dp,
        isAlwaysActive = true,
    )
}