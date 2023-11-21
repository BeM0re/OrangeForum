package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.domain.model.PostResponse
import ru.be_more.orange_forum.domain.model.ThreadInfo
import java.io.File

interface RemoteContract {

    interface ApiRepository {
        fun getCategories(): Single<List<Category>>
        fun getBoard(boardId: String): Single<Board>
        /** Thread without posts, only OP post*/
        fun getEmptyThread(boardId: String, threadNum: Int): Single<BoardThread>
        fun getThread(boardId: String, threadNum: Int): Single<BoardThread>
        fun getThreadInfo(boardId: String, threadNum: Int): Single<ThreadInfo>
        fun getPost(
            boardId: String,
            threadNum: Int,
            postNum: Int,
        ): Single<Post>
        fun getCaptchaUrl(boardId: String, threadNum: Int?): Single<String>
        fun postReply(
            boardId: String,
            threadNum: Int,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        ): Single<Int>

        fun postResponseOld(
            boardId: String,
            threadNum: Int,
            comment: String,
            captcha_type: String,
            g_recaptcha_response: String,
            chaptcha_id : String,
            files : List<File>
        ): Single<PostResponse>
    }
}