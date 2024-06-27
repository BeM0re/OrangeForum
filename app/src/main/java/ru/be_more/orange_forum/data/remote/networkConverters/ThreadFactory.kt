package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.BaseUrl
import ru.be_more.network.models.dto.ThreadDto
import ru.be_more.network.models.dto.ThreadInfoDto
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.ThreadInfo

object ThreadFactory {
    fun fromDto(threadDto: ThreadDto, boardId: String, baseUrl: BaseUrl): BoardThread =
        BoardThread(
            boardId = boardId,
            num = threadDto.num,
            posts = threadDto.threads
                .getOrNull(0)
                ?.posts
                ?.map { PostFactory.fromDto(it, boardId, threadDto.num, baseUrl) }
                ?: emptyList(),
            title = threadDto.title,
            postCount = threadDto.postCount,
            fileCount = threadDto.fileCount,
            lastPostNumber = threadDto.lastPostNumber,
            newMessageAmount = 0,
            isPinned = (threadDto.threads.getOrNull(0)?.posts?.getOrNull(0)?.sticky ?: 0) > 0,
            lasthit = threadDto.threads.getOrNull(0)?.posts?.getOrNull(0)?.lasthit ?: 0,
        )

    fun fromDto(threadInfoDto: ThreadInfoDto, boardId: String, threadNum: Int): ThreadInfo =
        ThreadInfo(
            boardId = boardId,
            threadNum = threadNum,
            postCount = threadInfoDto.thread?.posts ?: 0,
            timestamp = threadInfoDto.thread?.timestamp ?: 0,
            isAlive = threadInfoDto.result > 0,
        )
}