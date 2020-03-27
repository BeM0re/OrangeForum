package ru.be_more.orange_forum.services

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import ru.be_more.orange_forum.data.DvachCategories

interface BashQuoteApi{

    @GET("makaba/mobile.fcgi")
    fun getBashQuotesAsync(): Deferred<Response<DvachCategories>>
}