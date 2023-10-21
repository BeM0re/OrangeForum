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
import ru.be_more.orange_forum.domain.model.*
import ru.be_more.orange_forum.utils.ParseHtml
import java.io.File
import java.util.*

//инфа по обезьяньему апи: https://2ch.hk/abu/res/42375.html

class ApiRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.ApiRepository{

    override fun getCategories(): Single<List<Category>> =
        dvachApi.getBoardList()
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
        dvachApi.getBoard(boardId)
            .map { it.toModel(boardId) }
//            .map { it } //без мапа почему то свитч проходит в ?, а не !

    override fun getThread(boardId: String, threadNum: Int, forceUpdate: Boolean): Single<BoardThread> =
        dvachApi.getThread(boardId, threadNum, COOKIE)
            .doOnError { throwable -> Log.e("DvachApiRepository", "ApiRepositoryImpl.getThread = \n$throwable") }
//                .onErrorReturn { ThreadDto() } //todo ?
            .map { it.toModel(boardId) }
            .map { findResponses(it) }

    override fun getPost(
        boardId: String,
        threadNum: Int,
        postNum: Int,
    ): Single<Post> =
        dvachApi.getPost(boardId, postNum, COOKIE)
            .doOnError { throwable -> Log.e("DvachApiRepository", "ApiRepositoryImpl.getPost = \n$throwable") }
            .map { it.post.toModel(boardId, threadNum) }

    override fun postResponse(
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

        return dvachApi.postThreadResponseRx(
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
        dvachApi
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