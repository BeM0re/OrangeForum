package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.Imageboard
import ru.be_more.network.models.dvach.dto.ThreadDto
import ru.be_more.network.models.dvach.dto.ThreadInfoDto
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.ThreadInfo
import ru.be_more.network.models.fourchan.dto.FourchanThreadDto


fun FourchanThreadDto.toModel(boardId: String, imageboard: Imageboard): BoardThread =
    BoardThread(
        boardId = boardId,
        num = posts.first().no,
        posts = posts
            .map {
                it.toModel(
                    boardId = boardId,
                    threadNum = posts.first().no,
                    imageboard = imageboard,
                )
            },
        title = posts.first().sub ?: posts.first().com?.substring(50) ?: "",
        postCount = posts.first().replies ?: 0,
        fileCount = posts.first().images ?: 0,
        lastPostNumber = posts.last().no,
        newMessageAmount = 0,
        isPinned = posts.first().sticky > 0,
        lasthit = posts.last().timestamp,
        imageboard = imageboard,
    )

fun FourchanThreadDto.toThreadInfo(boardId: String, threadNum: Int, imageboard: Imageboard): ThreadInfo =
    ThreadInfo(
        boardId = boardId,
        threadNum = threadNum,
        postCount = posts.size,
        timestamp = posts.first().timestamp,
        isAlive = true,
        imageboard = imageboard,
    )

fun ThreadDto.toModel(boardId: String, imageboard: Imageboard): BoardThread =
    BoardThread(
        boardId = boardId,
        num = num,
        posts = threads
            .getOrNull(0)
            ?.posts
            ?.map { it.toModel(boardId, num, imageboard) }
            ?: emptyList(),
        title = title,
        postCount = postCount,
        fileCount = fileCount,
        lastPostNumber = lastPostNumber,
        newMessageAmount = 0,
        isPinned = (threads.getOrNull(0)?.posts?.getOrNull(0)?.sticky ?: 0) > 0,
        lasthit = threads.getOrNull(0)?.posts?.getOrNull(0)?.lasthit ?: 0,
        imageboard = imageboard,
    )

fun ThreadInfoDto.toModel(boardId: String, threadNum: Int, imageboard: Imageboard): ThreadInfo =
    ThreadInfo(
        boardId = boardId,
        threadNum = threadNum,
        postCount = thread?.posts ?: 0,
        timestamp = thread?.timestamp ?: 0,
        isAlive = result > 0,
        imageboard = imageboard,
    )
