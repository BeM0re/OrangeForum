package ru.be_more.orange_forum.presentation.model

import androidx.compose.ui.graphics.Color
import ru.be_more.orange_forum.presentation.theme.greenText
import ru.be_more.orange_forum.presentation.theme.greyDark
import ru.be_more.orange_forum.presentation.theme.orangeMedium

data class TextColors(
    val regularTextColor: Color,
    val urlColor: Color,
    val quoteColor: Color,
) {
    companion object {
        @Deprecated("Don't use")
        val default = TextColors(
            regularTextColor = greyDark,
            urlColor = orangeMedium,
            quoteColor = greenText
        )
    }
}