package ru.be_more.orange_forum.presentation.screens.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import ru.be_more.orange_forum.R

sealed class Screen(
    val route: ScreenRout,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val isAlwaysActive: Boolean,
) {
    data object Category : Screen(
        route = ScreenRout.CategoryScreen,
        title = R.string.navigation_category,
        icon = R.drawable.ic_menu_book_accent_24dp,
        isAlwaysActive = true,
    )
    data object Board : Screen(
        route = ScreenRout.BoardScreen("", ""),
        title = R.string.navigation_board,
        icon = R.drawable.ic_dashboard_accent_24dp,
        isAlwaysActive = false,
    )
    data object Thread : Screen(
        route = ScreenRout.ThreadScreen(
            imageboardType = "",
            boardId = "",
            threadNum = -1,
        ),
        title = R.string.navigation_thread,
        icon = R.drawable.ic_chat_accent_24dp,
        isAlwaysActive = false,
    )
    data object Favorite : Screen(
        route = ScreenRout.FavoriteScreen,
        title = R.string.navigation_favorites,
        icon = R.drawable.ic_favorite_border_accent_24dp,
        isAlwaysActive = true,
    )
    data object Queue : Screen(
        route = ScreenRout.QueueScreen,
        title = R.string.navigation_queue,
        icon = R.drawable.ic_list_numbered_accent_24dp,
        isAlwaysActive = true,
    )
    data object Posting : Screen(
        route = ScreenRout.PostingScreen("",""),
        title = R.string.navigation_posting,
        icon = R.drawable.ic_settings_accent_24dp,
        isAlwaysActive = true,
    )
    data object Setting : Screen(
        route = ScreenRout.SettingScreen,
        title = R.string.navigation_settings,
        icon = R.drawable.ic_settings_accent_24dp,
        isAlwaysActive = true,
    )
}
open class ScreenRout {
    @Serializable
    object CategoryScreen : ScreenRout()

    @Serializable
    data class BoardScreen(
        val imageboardType: String,
        val boardId: String
    ) : ScreenRout()

    @Serializable
    data class ThreadScreen(
        val imageboardType: String,
        val boardId: String,
        val threadNum: Int,
    ) : ScreenRout()

    @Serializable
    object FavoriteScreen : ScreenRout()

    @Serializable
    object QueueScreen : ScreenRout()

    @Serializable
    data class PostingScreen(
        val imageboardType: String,
        val boardId: String,
        val threadNum: Int = -1,
        val additionalString: String? = null,
    ) : ScreenRout()

    @Serializable
    object SettingScreen : ScreenRout()
}