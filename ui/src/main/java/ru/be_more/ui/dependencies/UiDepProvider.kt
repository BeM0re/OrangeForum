package ru.be_more.ui.dependencies

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

//todo добавить квалификатор для разных апи
object UiDepProvider {

    var cookie: String = ""

    internal fun getGlideUrlWithCookies(url: String): GlideUrl =
        if (cookie.isNotEmpty())
            GlideUrl(
                url,
                LazyHeaders.Builder()
                    .addHeader("Cookie", cookie)
                    .build()
            )
        else throw IllegalStateException("Cookies wasn't provided")
}