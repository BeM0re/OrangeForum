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
    fun getDvachCategoriesRx(@Query("task") task : String): Observable<DvachCategories>

    @GET("{board}/catalog.json")
    fun getDvachThreadsRx(@Path("board") board : String): Observable<DvachBoard>

    @GET("{board}/res/{threadNum}.json")
    fun getDvachPostsRx(
        @Path("board") board : String,
        @Path("threadNum") threadNum : Int,
        @Header("Cookie") cookie: String
    ): Observable<DvachThread>
}