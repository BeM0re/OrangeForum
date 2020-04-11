package ru.be_more.orange_forum.services

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import ru.be_more.orange_forum.data.DvachBoard
import ru.be_more.orange_forum.data.DvachCategories
import ru.be_more.orange_forum.data.DvachThread

interface DvachApi{

    @GET("makaba/mobile.fcgi")
    suspend fun getDvachCategoriesAsync(@Query("task") task : String): Response<DvachCategories>

    @GET("{board}/catalog.json")
    suspend fun getDvachThreadsAsync(@Path("board") board : String): Response<DvachBoard>

    @GET("{board}/res/{threadNum}.json")
    suspend fun getDvachPostsAsync(
        @Path("board") board : String,
        @Path("threadNum") threadNum : Int,
        @Header("Cookie") cookie: String
    ): Response<DvachThread>

    @GET("{board}/res/{threadNum}.json")
    fun getDvachPostsRx(
        @Path("board") board : String,
        @Path("threadNum") threadNum : Int,
        @Header("Cookie") cookie: String
    ): Observable<DvachThread>
}