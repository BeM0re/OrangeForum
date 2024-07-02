package ru.be_more.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.be_more.network.models.dvach.dto.BoardShortDto
import ru.be_more.network.models.dvach.dto.*

interface DvachApi{

    @GET("/api/mobile/v2/boards")
    suspend fun getBoardList(): List<BoardShortDto>

    @GET("/{board}/catalog.json")
    suspend fun getBoard(@Path("board") boardId: String): BoardDto

    //todo получать новые посты
    @GET("/api/mobile/v2/after/{board}/{thread}/{num}")
    suspend fun getPostsAfter(
        @Path("board") boardId: String,
        @Query("thread") thread: Int,
        @Query("num") num: Int,
        @Header("Cookie") cookie: String
    ): List<PostDto>

    //todo получать инфо о треде, если ли новые сообщения, наверное.
    @GET("/api/mobile/v2/info/{board}/{thread}")
    suspend fun getThreadInfo(
        @Path("board") boardId: String,
        @Path("thread") thread: Int,
        @Header("Cookie") cookie: String
    ): ThreadInfoDto

    @GET("/api/mobile/v2/post/{board}/{id}")
    suspend fun getPost(
        @Path("board") boardId: String,
        @Path("id") postId: Int,
        @Header("Cookie") cookie: String
    ): PostResponseDto

    @GET("/{board}/res/{id}.json")
    suspend fun getThread(
        @Path("board") boardId: String,
        @Path("id") postId: Int,
        @Header("Cookie") cookie: String
    ): ThreadDto

    //todo delete
    @Multipart
    @POST("/makaba/posting.fcgi?json=1")
    suspend fun postThreadResponse(
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
        @Part("g-recaptcha-response")gRecaptchaResponse: RequestBody,
        @Part("2chaptcha_id") chaptchaId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): ResponseDto

    @GET("/api/captcha/settings/{id}")
    suspend fun getBoardSettings(
        @Path("id") boardId: String,
    ): BoardCaptureSettingDto

    @GET("/api/captcha/2chcaptcha/id")
    suspend fun get2chCaptcha(
        @Query("board") boardId: String,
        @Query("thread") threadNum: Int?,
    ): DvachCaptchaDto

    @Multipart
    @POST("/user/posting")
    suspend fun postReply(
        @Part("captcha_type")   captchaType: RequestBody,
        @Part("board")          boardId: RequestBody,
        @Part("thread")         threadName: RequestBody,
        @Part("name")           name: RequestBody?,
        @Part("email")          email: RequestBody?,
        @Part("tags")           tags: RequestBody?,
        @Part("subject")        subject: RequestBody?,
        @Part("comment")        comment: RequestBody,
        @Part("icon")           icon: RequestBody?,
        @Part("op_mark")        isOp: Boolean?,
        @Part                   files: List<MultipartBody.Part>,
        @Part                   captchaFields: List<MultipartBody.Part>?,
    ): ReplyCreatedDto

    @POST("/user/posting")
    suspend fun postNewThread(
        @Part("captcha_type")   captchaType: RequestBody,
        @Part("board")          boardId: RequestBody,
        @Part("name")           name: RequestBody?,
        @Part("email")          email: RequestBody?,
        @Part("tags")           tags: RequestBody?,
        @Part("subject")        subject: RequestBody?,
        @Part("comment")        comment: RequestBody,
        @Part("icon")           icon: RequestBody?,
        @Part("op_mark")        isOp: RequestBody?,
        @Part                   files: List<MultipartBody.Part>,
        @Part                   captchaFields: List<MultipartBody.Part>?,
    ): ThreadCreatedDto

}