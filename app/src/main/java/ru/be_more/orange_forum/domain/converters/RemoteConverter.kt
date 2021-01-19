package ru.be_more.orange_forum.domain.converters

import ru.be_more.orange_forum.data.remote.models.*
import ru.be_more.orange_forum.domain.model.*
import ru.be_more.orange_forum.utils.ParseHtml

class RemoteConverter {
    companion object{

        fun toCategories (allCategories: Map<String, List<BoardNameDto>>) : List<Category> =
            allCategories.map {
                Category(it.key, getBoardNames(it.value))
            }

        private fun getBoardNames(boardDtos : List<BoardNameDto>) =
            boardDtos.map { toBoard(it) }

        fun toBoard(boardDto: BoardNameDto) = Board(
            name = boardDto.name,
            id = boardDto.id
        )

        fun toBoard(boardDto: BoardDto)
                = boardDto.threads.map { toThread(it) }

        fun toThread(opPostDto: PostDto) = BoardThread(
            num = opPostDto.num,
            posts = listOf(toPost(opPostDto)),
            title = opPostDto.subject
        )
        fun toThread(threadDto: ThreadDto, threadNum: Int) = BoardThread(
            num = threadNum,
            posts = threadDto.threads[0].posts.map { toPost(it) },
            title = threadDto.title
        )

        fun toPost(post: PostDto) = Post(
            num = post.num,
            name = post.name,
            comment = post.comment,
            date = post.date,
            email = post.email,
            files = post.fileDtos.map { toFiles(it) },
            files_count = post.files_count,
            op = post.op,
            posts_count = post.posts_count,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number
        )

        fun toFiles (fileDto: FileDto) = AttachFile(
            path = fileDto.path,
            thumbnail = fileDto.thumbnail,
            duration = if(fileDto.duration.isNullOrEmpty()) "" else fileDto.duration
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