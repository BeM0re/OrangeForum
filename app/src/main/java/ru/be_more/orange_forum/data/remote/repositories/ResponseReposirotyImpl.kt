package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.models.DvachPostResponse
import java.io.File
import java.util.*

class ResponseReposirotyImpl  (
    private val dvachApi : DvachApi
) : RemoteContract.ResponseRepository{
    override fun postResponse(
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id: String,
        files: List<File>
    ): Single<DvachPostResponse> {

        Log.d("M_ResponseReposirotyImpl","post")

        val requestTask =
            RequestBody.create(MediaType.parse("text/plain"), "post")
        val requestCookie =
            RequestBody.create(MediaType.parse("text/plain"), COOKIE)
        val requestBoardId =
            RequestBody.create(MediaType.parse("text/plain"), boardId)
        val requestThreadNum =
            RequestBody.create(MediaType.parse("text/plain"), ""+threadNum)
        val requestComment =
            RequestBody.create(MediaType.parse("text/plain"), comment)
        val requestCaptchaType =
            RequestBody.create(MediaType.parse("text/plain"), captcha_type)
        val requestGRecaptchaResponse =
            RequestBody.create(MediaType.parse("text/plain"), g_recaptcha_response)
        val requestChaptchaId =
            RequestBody.create(MediaType.parse("text/plain"), chaptcha_id)

        val requestFiles: LinkedList<MultipartBody.Part> = LinkedList()

        files.forEach {file ->
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)

            requestFiles.add(
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            )
        }

        return dvachApi.postThreadResponseRx(
            cookie = requestCookie,
            task = requestTask ,
            board = requestBoardId,
            thread = requestThreadNum,
            op_mark = null,
            usercode = null,
            captcha_type = requestCaptchaType,
            email = null,
            subject = null,
            comment = requestComment,
            g_recaptcha_response = requestGRecaptchaResponse,
            chaptcha_id = requestChaptchaId,
            files = requestFiles
        )
    }
}