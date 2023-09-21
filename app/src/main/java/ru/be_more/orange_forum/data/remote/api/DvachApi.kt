package ru.be_more.orange_forum.data.remote.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.be_more.orange_forum.data.remote.models.*

interface DvachApi{

    @GET("makaba/mobile.fcgi")
    fun getCategories(@Query("task") task : String): Single<Map<String, List<BoardShortDto>>>

    @GET("index.json")
    fun getBoardList(): Single<BoardListDto>

    @GET("{board}/catalog.json")
    fun getBoard(@Path("board") board : String): Single<BoardDto>

    @GET("{board}/res/{threadNum}.json")
    fun getThread(
        @Path("board") board : String,
        @Path("threadNum") threadNum : Int,
        @Header("Cookie") cookie: String
    ): Single<ThreadDto>

    @GET("makaba/mobile.fcgi")
    fun getDvachPostRx(
        @Query("task") task : String? = "get_post",
        @Query("board") board : String,
        @Query("post") post : Int,
        @Header("Cookie") cookie: String
    ): Single<List<PostDto>>

    @Multipart
    @POST("makaba/posting.fcgi?json=1")
    fun postThreadResponseRx(
        @Header("Cookie") cookie: RequestBody,
        @Part("task") task: RequestBody,
        @Part("board") board: RequestBody,
        @Part("thread") thread: RequestBody,
        @Part("op_mark") op_mark: RequestBody?, //1 или пусто (или 0)
        @Part("usercode") usercode: RequestBody?, //passcode
        @Part("captcha_type") captcha_type: RequestBody,
        @Part("email") email: RequestBody?, //имейл или сажа
        @Part("subject") subject: RequestBody?,
        @Part("comment") comment: RequestBody,
        @Part("g-recaptcha-response") g_recaptcha_response: RequestBody,
        @Part("2chaptcha_id") chaptcha_id: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Single<PostResponseDto>

}