package ru.be_more.orange_forum.data.remote.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.be_more.orange_forum.data.remote.models.*

interface DvachApi{

    @GET("/api/mobile/v2/boards")
    fun getBoardList(): Single<List<BoardShortDto>>

    @GET("/{board}/catalog.json")
    fun getBoard(@Path("board") boardId: String): Single<BoardDto>

    //todo получать новые посты
    @GET("/api/mobile/v2/after/{board}/{thread}/{num}")
    fun getPostsAfter(
        @Path("board") boardId: String,
        @Query("thread") thread: Int,
        @Query("num") num: Int,
        @Header("Cookie") cookie: String
    ): Single<List<PostDto>>

    //todo получать инфо о треде, если ли новые сообщения, наверное.
    @GET("/api/mobile/v2/info/{board}/{thread}")
    fun getThreadInfo(
        @Path("board") boardId: String,
        @Query("thread") thread: Int,
        @Header("Cookie") cookie: String
    ): Single<List<PostDto>>

    @GET("/api/mobile/v2/post/{board}/{id}")
    fun getPost(
        @Path("board") boardId: String,
        @Path("id") postId: Int,
        @Header("Cookie") cookie: String
    ): Single<PostResponseDto>

    @GET("/{board}/res/{id}.json")
    fun getThread(
        @Path("board") boardId: String,
        @Path("id") postId: Int,
        @Header("Cookie") cookie: String
    ): Single<ThreadDto>

    @Multipart
    @POST("/makaba/posting.fcgi?json=1")
    fun postThreadResponseRx(
        @Header("Cookie") cookie: RequestBody,
        @Part("task") task: RequestBody,
        @Part("board") board: RequestBody,
        @Part("thread") thread: RequestBody,
        @Part("op_mark") opMark: RequestBody?, //1 или пусто (или 0)
        @Part("usercode") userCode: RequestBody?, //passcode
        @Part("captcha_type") captchaType: RequestBody,
        @Part("email") email: RequestBody?, //имейл или сажа
        @Part("subject") subject: RequestBody?,
        @Part("comment") comment: RequestBody,
        @Part("g-recaptcha-response") gRecaptchaResponse: RequestBody,
        @Part("2chaptcha_id") chaptchaId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Single<ResponseDto>

}