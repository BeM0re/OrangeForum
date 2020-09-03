package ru.be_more.orange_forum.data.remote

import io.reactivex.Observable
import ru.be_more.orange_forum.data.remote.models.*
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.model.Post
import java.io.File

interface RemoteContract {

    interface CategoryRepository {
        fun getDvachCategories(task: String): Observable<List<Category>>
    }

    interface BoardRepository {
        fun getDvachThreads(boardId: String): Observable<List<BoardThread>>
    }

    interface ThreadRepository {

        fun getThread(boardId: String, threadNum: Int): Observable<BoardThread>

        fun getDvachPost(
            boardId: String,
            postNum: Int,
            cookie: String
        ): Observable<Post>

        fun postThreadResponse(
            boardId: String,
            threadNum: Int,
            comment: String,
            captcha_type: String,
            g_recaptcha_response: String,
            chaptcha_id : String,
            files : List<File>
        ): Observable<DvachPostResponse>
    }
}