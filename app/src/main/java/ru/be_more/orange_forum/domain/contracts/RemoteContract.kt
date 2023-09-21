package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.domain.model.PostResponse
import java.io.File

interface RemoteContract {

    interface ApiRepository {
        fun getCategories(): Single<List<Category>>
        fun getBoard(boardId: String): Single<List<BoardThread>>
        fun getThread(boardId: String, threadNum: Int, forceUpdate: Boolean = false): Single<BoardThread>
        fun getThreadShort(boardId: String, threadNum: Int): Single<BoardThread>
        fun getPost(boardId: String, postNum: Int, cookie: String): Single<Post>
        fun postResponse(
            boardId: String,
            threadNum: Int,
            comment: String,
            captcha_type: String,
            g_recaptcha_response: String,
            chaptcha_id : String,
            files : List<File>): Single<PostResponse>
    }
}