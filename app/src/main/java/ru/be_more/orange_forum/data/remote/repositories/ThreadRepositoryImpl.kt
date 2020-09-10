package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.models.DvachPostResponse
import ru.be_more.orange_forum.data.remote.models.DvachThread
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.findResponses
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toPost
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toThread
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.data.remote.api.DvachApi
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepositoryImpl @Inject constructor(
    private val dvachApi : DvachApi
) : RemoteContract.ThreadRepository{

    override fun getThread(boardId: String, threadNum: Int): Single<BoardThread> =
        dvachApi.getDvachPostsRx(boardId, threadNum, COOKIE)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "get thread via api error = $throwable") }
            .onErrorReturn { DvachThread() }
            .map { entity -> toThread(entity, threadNum)}
            .map { entity -> findResponses(entity)}

    override fun getDvachPost(
        boardId: String,
        postNum: Int,
        cookie: String
    ): Single<Post> =
        dvachApi.getDvachPostRx("get_post", boardId, postNum, COOKIE)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting post error = $throwable") }
            .map { entity -> toPost(entity[0]) }

    override fun postThreadResponse(
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id : String,
        files : List<File>
    ): Single<DvachPostResponse> {

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