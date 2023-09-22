package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Maybe
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

class ApiRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.ApiRepository{

    private var lastThread: BoardThread? = null //todo delete
    private var lastThreadBoard = ""
    private var lastBoard: Board? = null

    override fun getCategories(): Single<List<Category>> =
        dvachApi.getBoardList()
            .map { dto ->
                dto.boardList
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
        Maybe
            .just(lastBoard?.takeIf { it.id == boardId })
            .switchIfEmpty(
                dvachApi.getBoard(boardId)
                    .map { it.toModel(boardId) }
                    .doAfterSuccess { lastBoard = it }
            )
            .map { it } //без мапа почему то свитч проходит в ?, а не !

    override fun getThread(boardId: String, threadNum: Int, forceUpdate: Boolean): Single<BoardThread> =
        if (boardId == lastThreadBoard && threadNum == lastThread?.num && !forceUpdate)
            Single.just(lastThread)
        else
            dvachApi.getThread(boardId, threadNum, COOKIE)
                .doOnError { throwable -> Log.e("DvachApiRepository", "get thread via api error = $throwable") }
//                .onErrorReturn { ThreadDto() } //todo ?
                .map { entity -> entity.toModel(boardId) }
                .map { entity -> findResponses(entity) }
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

    override fun getPost(boardId: String, postNum: Int, cookie: String): Single<Post> =
        dvachApi.getDvachPostRx("get_post", boardId, postNum, COOKIE)
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting post error = $throwable") }
            .map { entity -> entity[0].toModel(boardId, postNum) } //todo postNum ok?

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

    private fun findResponses(board: BoardThread): BoardThread {
        board.posts.forEach { post ->
            //replies - на какие посты ответы
            val replies = ParseHtml.findReply(post.comment)

            //пост с номером post.num отвечает на пост с номером reply
            //reply сохраняет, что на него ссылается post.num
            replies.forEach { reply ->
                board.posts.find { it.id == reply }
                    ?.replies?.add(post.id)
            }
        }

        return board
    }
}