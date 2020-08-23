package ru.be_more.orange_forum.services

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.be_more.orange_forum.data.*

interface DvachApi{

    @GET("makaba/mobile.fcgi")
    fun getDvachCategoriesRx(@Query("task") task : String): Observable<Map<String, List<DvachBoardName>>>

    @GET("{board}/catalog.json")
    fun getDvachThreadsRx(@Path("board") board : String): Observable<DvachBoard>

    @GET("{board}/res/{threadNum}.json")
    fun getDvachPostsRx(
        @Path("board") board : String,
        @Path("threadNum") threadNum : Int,
        @Header("Cookie") cookie: String
    ): Observable<DvachThread>

    @GET("makaba/mobile.fcgi")
    fun getDvachPostRx(
        @Query("task") task : String? = "get_post",
        @Query("board") board : String,
        @Query("post") post : Int,
        @Header("Cookie") cookie: String
    ): Observable<List<DvachPost>>

    @GET("api/captcha/settings/{board}")
    fun getDvachCaptchaTypesRx(
        @Path("board") board : String,
        @Header("Cookie") cookie: String
    ): Observable<CaptchaTypes>

    @GET("api/captcha/{captchaType}/id")
    fun getDvachCaptchaIdRx(
        @Path("captchaType") captchaType : String,
        @Header("Cookie") cookie: String
        ) : Observable<GetCaptchaResponse>

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
    ): Observable<DvachPostResponse>

    @GET("api/captcha/recaptcha/mobile")
    fun getMobileCaptchaRx() : Observable<ResponseBody>
}