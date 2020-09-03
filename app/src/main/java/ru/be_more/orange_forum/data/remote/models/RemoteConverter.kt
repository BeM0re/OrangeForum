package ru.be_more.orange_forum.data.remote.models

import android.util.Log
import ru.be_more.orange_forum.model.*
import ru.be_more.orange_forum.utils.ParseHtml

class RemoteConverter {
    companion object{

        fun toCategories (allCategories: Map<String, List<DvachBoardName>>) : List<Category> =
            allCategories.map {
                Category(it.key, getBoardNames(it.value))
            }

        private fun getBoardNames(dvachBoards : List<DvachBoardName>) =
            dvachBoards.map { toBoard(it) }

        fun toBoard(dvachBoard: DvachBoardName) = Board(
            name = dvachBoard.name,
            id = dvachBoard.id
        )

        fun toBoard(dvachBoard: DvachBoard)
                = dvachBoard.threads.map { toThread(it) }

        fun toThread(dvachOpPost: DvachPost) = BoardThread(
            num = dvachOpPost.num,
            posts = listOf(toPost(dvachOpPost)),
            title = dvachOpPost.subject
        )
        fun toThread(dvachThread: DvachThread, threadNum: Int) = BoardThread(
            num = threadNum,
            posts = dvachThread.threads[0].posts.map { toPost(it) },
            title = dvachThread.title
        )

        fun toPost(post: DvachPost) = Post(
            num = post.num,
            name = post.name,
            comment = post.comment,
            date = post.date,
            email = post.email,
            files = post.files.map { toFiles(it) },
            files_count = post.files_count,
            op = post.op,
            posts_count = post.posts_count,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number
        )

        fun toFiles (file: DvachFile) = AttachFile(
            path = file.path,
            thumbnail = file.thumbnail,
            duration = if(file.duration.isNullOrEmpty()) "" else file.duration
        )

        fun findResponses(board: BoardThread): BoardThread {

            board.posts.forEach { post ->
                //replies - на какие посты ответы
                val replies = ParseHtml.findReply(post.comment)

                //пост с номером post.num отвечает на пост с номером reply
                //reply сохраняет, что на него ссылается post.num
                replies.forEach { reply ->
                    board.posts.find { it.num == reply }
                        ?.replies?.add(post.num)
                }
            }

            return board
        }
    }


}