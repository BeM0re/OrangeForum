package ru.be_more.orange_forum.services

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
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

    @GET("api/captcha/settings/{board}")
    fun getDvachCaptchaTypesRx(
        @Path("board") board : String,
        @Header("Cookie") cookie: String
    ): Observable<DvachBoard>

    @GET("api/captcha/{captchaType}/id")
    fun getDvachCaptchaIdRx(
        @Path("captchaType") captchaType : String,
        @Header("Cookie") cookie: String
        ) : Observable<DvachBoard>

    @Multipart
    @POST("makaba/posting.fcgi?json=1")
    fun postThreadResponse(
        @Header("Cookie") cookie: String,
        @Field("task") task: String = "post",
        @Field("board") board: String ,
        @Field("thread") thread: Int ,
        @Field("op_mark") op_mark: String , //1 или пусто (или 0)
        @Field("usercode") usercode: String , //passcode
        @Field("captcha_type") captcha_type: String ,
        @Field("email") email: String , //имейл или сажа
        @Field("subject") subject: String ,
        @Field("comment") comment: String ,
        @Field("g-recaptcha-response") g_recaptcha_response: String ,
        @Field("2chaptcha_id") chaptcha_id: String,
        @Part ("formimages") files: List<MultipartBody.Part>
    ): Observable<DvachBoard>


}