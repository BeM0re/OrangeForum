package ru.be_more.orange_forum.services

import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query
import ru.be_more.orange_forum.data.GoogleCaptchaResponse

interface CaptchaApi{

    @POST("recaptcha/api/siteverify")
    fun getGCaptchaResponse(
        @Query("secret") secret : String,
        @Query("response") response : String
    ): Observable<GoogleCaptchaResponse>
}