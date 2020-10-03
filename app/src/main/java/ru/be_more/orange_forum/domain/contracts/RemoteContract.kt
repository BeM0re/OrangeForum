package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Single
import ru.be_more.orange_forum.data.remote.models.*
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.domain.model.PostResponse
import java.io.File

interface RemoteContract {

    interface CategoryRepository {
        fun getDvachCategories(): Single<Map<String, List<DvachBoardName>>>
    }

    interface BoardRepository {
        fun getDvachThreads(boardId: String): Single<List<BoardThread>>
    }

    interface ThreadRepository {

        fun getThread(boardId: String, threadNum: Int): Single<BoardThread>

        fun getDvachPost(
            boardId: String,
            postNum: Int,
            cookie: String
        ): Single<Post>
    }

    interface ResponseRepository {
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