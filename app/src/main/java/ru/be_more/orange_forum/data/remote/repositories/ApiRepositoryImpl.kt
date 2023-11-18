package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.models.Captcha
import ru.be_more.orange_forum.data.remote.models.CaptureType
import ru.be_more.orange_forum.domain.model.*
import ru.be_more.orange_forum.utils.ParseHtml
import java.io.File
import java.util.*

//инфа по обезьяньему апи: https://2ch.hk/abu/res/42375.html

class ApiRepositoryImpl(
    private val api : DvachApi
) : RemoteContract.ApiRepository{
    //todo переделать модель капчи
    private var captcha: Captcha? = null

    override fun getCategories(): Single<List<Category>> =
        api.getBoardList()
            .map { dto ->
                dto
                    .map { it.toModel() }
                    .groupBy { it.category }
                    .map { (category, boards) ->
                        Category(
                            name = category,
                            boards = boards,
                            isExpanded = false
                        )
                    }
            }

    override fun getBoard(boardId: String): Single<Board> =
        api.getBoard(boardId)
            .map { it.toModel(boardId) }
//            .map { it } //без мапа почему то свитч проходит в ?, а не !

    override fun getThread(boardId: String, threadNum: Int, forceUpdate: Boolean): Single<BoardThread> =
        api.getThread(boardId, threadNum, COOKIE)
            .doOnError { throwable -> Log.e("DvachApiRepository", "ApiRepositoryImpl.getThread = \n$throwable") }
//                .onErrorReturn { ThreadDto() } //todo ?
            .map { it.toModel(boardId) }
            .map { findResponses(it) }

    override fun getPost(
        boardId: String,
        threadNum: Int,
        postNum: Int,
    ): Single<Post> =
        api.getPost(boardId, postNum, COOKIE)
            .doOnError { throwable -> Log.e("DvachApiRepository", "ApiRepositoryImpl.getPost = \n$throwable") }
            .map { it.post.toModel(boardId, threadNum) }

    override fun getCaptchaUrl(boardId: String, threadNum: Int?): Single<String> =
        api.getBoardSettings(boardId)
            .map { it.toModel() }
            .flatMap { boardSetting ->
                when (boardSetting.captchaType) {
                    CaptureType.DvachCaptcha -> {
                        api.get2chCaptcha(boardId, threadNum)
                            .map { it.toModel() }
                            .doOnSuccess {
                                captcha = Captcha.DvachCaptcha(id = it.id)
                            }
                            .map { DVACH_ROOT_URL + "/api/captcha/2chcaptcha/show/?id=${ it.id }" }
                    }

                    CaptureType.NoCaptcha -> {
                        captcha = Captcha.NoCaptcha()
                        Single.just("")
                    }

                    else -> {
                        Single.error(
                            Throwable("Capture method ${boardSetting.captchaType} is not yet implemented")
                        )
                    }
                }
            }

    override fun postReply(
        boardId: String,
        threadNum: Int,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?
    ): Single<Int> {
        captcha?.solveCapture(captchaSolvedString)

        val requestBoardId = boardId.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestThreadNum = (""+threadNum).toRequestBody("text/plain".toMediaTypeOrNull())
        val requestComment = comment.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestCaptchaType = (captcha?.type?.value ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        val requestSubject = subject.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestEmail = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestName = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestTag = tag.toRequestBody("text/plain".toMediaTypeOrNull())
        val captchaFields = captcha
            ?.additionalFields
            ?.map { (name, value) -> MultipartBody.Part.createFormData(name, value) }

        return api
            .postReply(
                captchaType = requestCaptchaType,
                boardId = requestBoardId,
                threadName = requestThreadNum,
                comment = requestComment,
                subject = requestSubject,
                email = requestEmail,
                name = requestName,
                tags = requestTag,
                icon = null,
                isOp = isOp,
                files = emptyList(),
                captchaFields = captchaFields,
            )
            .map { replyDto ->
                    if (replyDto.error != null && replyDto.error.code != 0)
                        throw Throwable("Replying error code ${replyDto.error.code}: ${replyDto.error.message}")
                    else
                        replyDto.num
                }
    }

    override fun postResponseOld(
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id: String,
        files: List<File>
    ): Single<PostResponse> {

        //todo redo for new capture
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

        return api.postThreadResponseRx(
            cookie = requestCookie,
            task = requestTask ,
            board = requestBoardId,
            thread = requestThreadNum,
            opMark = null,
            userCode = null,
            captchaType = requestCaptchaType,
            email = null,
            subject = null,
            comment = requestComment,
            gRecaptchaResponse = requestGRecaptchaResponse,
            chaptchaId = requestChaptchaId,
            files = requestFiles
        )
            .map { it.toModel() }
    }

    override fun getThreadInfo(boardId: String, threadNum: Int): Single<ThreadInfo> =
        api
            .getThreadInfo(boardId, threadNum, COOKIE)
            .map { it.toModel(boardId, threadNum) }
            .onErrorReturn {
                ThreadInfo(
                    boardId = boardId,
                    threadNum = threadNum,
                    isAlive = false,
                )
            }

    private fun findResponses(board: BoardThread): BoardThread {
        val replies = board.posts
            .map { post ->
                ParseHtml.findReply(post.id, post.comment)
            }
            .flatten()
            .groupBy { it.to }

        return board.copy(
            posts = board.posts.map { post ->
                post.copy(
                    replies = replies
                        .getOrDefault(post.id, emptyList())
                        .map { it.from }
                )
            }
        )
    }
}