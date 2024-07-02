package ru.be_more.orange_forum.data.remote.repositories

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Category
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.Post
import ru.be_more.model.model.PostResponse
import ru.be_more.model.model.ThreadInfo
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.network.api.FourchanApi
import ru.be_more.network.models.dvach.Captcha
import ru.be_more.network.models.dvach.CaptureType
import ru.be_more.orange_forum.consts.FOURCHAN_CATEGORY
import ru.be_more.orange_forum.data.remote.networkConverters.*
import ru.be_more.orange_forum.di.Fourchan
import ru.be_more.ui.utils.ParseHtml
import java.io.File
import java.lang.Exception
import java.net.ConnectException
import java.util.*
import javax.inject.Inject

//инфа по 4chan api https://github.com/4chan/4chan-API/blob/master/README.md

class FourchanApiRepositoryImpl @Inject constructor(
    private val api: FourchanApi,
    @Fourchan private val imageboard: Imageboard,
) : RemoteContract.ApiRepository{
    //todo переделать модель капчи
    private var captcha: Captcha? = null

    override suspend fun getCategories(): List<Category> =
        api.getBoardList()
            .let { it.boards }
            .map { it.toModel(imageboard) }
            .let {
                listOf(
                    Category(
                        name = FOURCHAN_CATEGORY,
                        boards = it,
                        isExpanded = false,
                        imageboard = imageboard,
                    )
                )
            }

    override suspend fun getBoard(boardName: String, boardId: String): Board =
        api.getBoard(boardId)
            .let {
                it.toModel(
                    boardName = boardName,
                    boardId = boardId,
                    imageboard = imageboard,
                )
            }

    override suspend fun getEmptyThread(boardId: String, threadNum: Int): BoardThread =
        api.getThread(boardId, threadNum, COOKIE)
            .let { it.toModel(boardId, imageboard) }
            .let { it.copy(posts = listOf(it.posts.first())) }

    override suspend fun getThread(boardId: String, threadNum: Int): BoardThread =
        api.getThread(boardId, threadNum, COOKIE)
            .let { it.toModel(boardId, imageboard) }
            .let { findResponses(it) }

    override suspend fun getPost(
        boardId: String,
        threadNum: Int,
        postNum: Int,
    ): Post =
        api.getThread(boardId, threadNum, COOKIE)
            .let { thread -> thread.posts.first { it.no == postNum } }
            .let { it.toModel(boardId, threadNum, imageboard) }

    override suspend fun getCaptchaUrl(boardId: String, threadNum: Int?): String =
        TODO()

    override suspend fun postReply(
        boardId: String,
        threadNum: Int,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?
    ): Int {
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
            .let { replyDto ->
                    if (replyDto.error != null && replyDto.error?.code != 0)
                        throw Throwable("Replying error code ${replyDto.error?.code}: ${replyDto.error?.message}")
                    else
                        requireNotNull(replyDto.num)
                }
    }

    @Deprecated("delete")
    override suspend fun postResponseOld(
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id: String,
        files: List<File>
    ): PostResponse {
        TODO()
    }

    override suspend fun getThreadInfo(boardId: String, threadNum: Int): ThreadInfo =
        try {
            api.getThread(boardId, threadNum, COOKIE)
                .let { it.toThreadInfo(boardId, threadNum, imageboard) }
        } catch (e: Exception) {
            if (e is ConnectException)
                throw e

            ThreadInfo(
                boardId = boardId,
                threadNum = threadNum,
                isAlive = false,
                imageboard = imageboard
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