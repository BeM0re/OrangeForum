package ru.be_more.orange_forum.services

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.be_more.orange_forum.data.DvachCategories

interface DvachApi{

    @GET("makaba/mobile.fcgi")
    suspend fun getDvachCategoriesAsync(@Query("task") task : String): Response<DvachCategories>
}