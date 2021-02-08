package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.models.ThreadDto
import ru.be_more.orange_forum.data.remote.converters.RemoteConverter
import ru.be_more.orange_forum.data.remote.converters.RemoteConverter.Companion.toCategories
import ru.be_more.orange_forum.domain.model.*
import java.io.File
import java.util.*

class ApiRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.ApiRepository{

    private var lastThread: BoardThread? = null
    private var lastThreadBoard = ""
    private var lastBoard: Board? = null

    override fun getDvachCategories(): Single<List<Category>> =
        dvachApi.getDvachCategories("get_boards")
            .map { toCategories(it) }
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting category error = $throwable") }

    override fun getDvachThreads(boardId: String): Single<List<BoardThread>> =
        if (lastBoard?.id == boardId)
            Single.just(lastBoard!!.threads)
        else
            dvachApi.getDvachThreads(boardId)
                .map { entity -> RemoteConverter.toBoard(entity) }
                .doAfterSuccess { lastBoard = Board(name = "", id = boardId, threads = it) }

    override fun getThread(boardId: String, threadNum: Int): Single<BoardThread> =
        if (boardId == lastThreadBoard && threadNum == lastThread?.num)
            Single.just(lastThread)
        else
            dvachApi.getDvachPosts(boardId, threadNum, COOKIE)
                .doOnError { throwable -> Log.e("M_DvachApiRepository", "get thread via api error = $throwable") }
                .onErrorReturn { ThreadDto() }
                .map { entity -> RemoteConverter.toThread(entity, threadNum) }
                .map { entity -> RemoteConverter.findResponses(entity) }
                .doAfterSuccess {
                    lastThread = it
                    lastThreadBoard = boardId
                }

    override fun getThreadShort(boardId: String, threadNum: Int): Single<BoardThread> =
        when {
            boardId == lastBoard?.id -> {
                lastBoard?.threads
                    ?.firstOrNull { it.num == threadNum }
                    ?.let { return Single.just(it) }
                getThread(boardId, threadNum)
            }
            boardId == lastThreadBoard && threadNum == lastThread?.num ->
                Single.just(lastThread)
            else ->
                getThread(boardId, threadNum)
        }


    override fun getDvachPost(
        boardId: String,
        postNum: Int,
        cookie: String
    ): Single<Post> =
        dvachApi.getDvachPostRx("get_post", boardId, postNum, COOKIE)
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting post error = $throwable") }
            .map { entity -> RemoteConverter.toPost(entity[0]) }

    override fun postResponse(
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id: String,
        files: List<File>
    ): Single<PostResponse> {

        val requestTask = "post".toRequestBody("text/plain".toMediaTypeOrNull())
        val requestCookie = COOKIE.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestBoardId = boardId.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestThreadNum = (""+threadNum).toRequestBody("text/plain".toMediaTypeOrNull())
        val requestComment = comment.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestCaptchaType = captcha_type.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestGRecaptchaResponse = g_recaptcha_response.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestChaptchaId = chaptcha_id.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestFiles: LinkedList<MultipartBody.Part> = LinkedList()

        files.forEach {file ->
            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

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
            .map { it.toModel() }
    }
}