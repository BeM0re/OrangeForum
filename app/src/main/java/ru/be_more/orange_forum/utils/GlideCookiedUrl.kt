package ru.be_more.orange_forum.utils

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import ru.be_more.orange_forum.consts.COOKIE

class GlideCookiedUrl {

    companion object {
        fun getGlideUrl(url: String) =
            GlideUrl(
                url,
                LazyHeaders.Builder()
                    .addHeader("Cookie", COOKIE)
                    .build()
            )
    }
}