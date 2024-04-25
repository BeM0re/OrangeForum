package ru.be_more.orange_forum.domain.contracts

import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.domain.model.PostResponse
import ru.be_more.orange_forum.domain.model.ThreadInfo
import java.io.File

interface RemoteContract {

    interface ApiRepository {
        suspend fun getCategories(): List<Category>
        suspend fun getBoard(boardId: String): Board
        /** Thread without posts, only OP post*/
        suspend fun getEmptyThread(boardId: String, threadNum: Int): BoardThread
        suspend fun getThread(boardId: String, threadNum: Int): BoardThread
        suspend fun getThreadInfo(boardId: String, threadNum: Int): ThreadInfo
        suspend fun getPost(
            boardId: String,
            threadNum: Int,
            postNum: Int,
        ): Post
        suspend fun getCaptchaUrl(boardId: String, threadNum: Int?): String
        suspend fun postReply(
            boardId: String,
            threadNum: Int,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        ): Int

        suspend fun postResponseOld(
            boardId: String,
            threadNum: Int,
            comment: String,
            captcha_type: String,
            g_recaptcha_response: String,
            chaptcha_id : String,
            files : List<File>,
        ): PostResponse
    }
}