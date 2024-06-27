package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.BaseUrl
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Post
import ru.be_more.network.models.dto.PostDto

object PostFactory {
    fun fromDto(postDto: PostDto, boardId: String, threadNum: Int, baseUrl: BaseUrl): Post =
        Post(
            boardId = boardId,
            threadNum = threadNum,
            id = postDto.num,
            name = postDto.name,
            comment = postDto.comment,
            isOpPost = threadNum == postDto.num,
            date = postDto.date,
            email = postDto.email,
            files = postDto.files?.map { FileFactory.fromDto(it, baseUrl) } ?: emptyList(),
            fileCount = postDto.filesCount ?: postDto.files?.size ?: 0,
            isAuthorOp = postDto.op == 1,
            postCount = postDto.postsCount ?: 1,
            subject = postDto.subject,
            timestamp = postDto.timestamp,
            number = postDto.number
        )

    fun toThread(postDto: PostDto, boardId: String, baseUrl: BaseUrl): BoardThread =
        BoardThread(
            boardId = boardId,
            num = postDto.num,
            posts = listOf(
                fromDto(
                    postDto = postDto,
                    boardId = boardId,
                    threadNum = postDto.num,
                    baseUrl = baseUrl
                )
            ),
            title = postDto.subject.ifEmpty { postDto.comment },
            postCount = postDto.postsCount ?: 1,
            fileCount = postDto.filesCount ?: postDto.files?.size ?: 0,
            isPinned = postDto.sticky > 0,
            lasthit = postDto.lasthit,
            newMessageAmount = 0,
            lastPostNumber = 0,
        )
}