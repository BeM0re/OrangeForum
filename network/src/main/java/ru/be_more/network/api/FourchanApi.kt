package ru.be_more.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.be_more.network.models.dvach.dto.BoardShortDto
import ru.be_more.network.models.dvach.dto.*
import ru.be_more.network.models.fourchan.dto.*

interface FourchanApi{

    @GET("/boards.json")
    suspend fun getBoardList(): FourchanBoardListDto

    @GET("/{board}/catalog.json")
    suspend fun getBoard(@Path("board") boardId: String): List<FourchanBoardPageDto>

    @GET("/{board}/res/{id}.json")
    suspend fun getThread(
        @Path("board") boardId: String,
        @Path("id") postId: Int,
        @Header("Cookie") cookie: String
    ): FourchanThreadDto

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