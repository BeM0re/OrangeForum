package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Post
import ru.be_more.network.models.dvach.dto.PostDto
import ru.be_more.network.models.fourchan.dto.FourchanPostDto

fun FourchanPostDto.toModel(boardId: String, threadNum: Int, imageboard: Imageboard): Post =
    Post(
        boardId = boardId,
        threadNum = threadNum,
        id = no,
        name = name,
        comment = com ?: "",
        isOpPost = threadNum == no,
        date = now,
        email = "",
        files = listOfNotNull(
            toAttachedFile(imageboard, boardId)
        ),
        fileCount = images ?: 0,
        isAuthorOp = false,
        postCount = replies ?: 1,
        subject = sub ?: "",
        timestamp = timestamp,
        number = number,
        imageboard = imageboard,
    )

fun FourchanPostDto.toThread(boardId: String, imageboard: Imageboard): BoardThread =
    BoardThread(
        boardId = boardId,
        num = no,
        posts = listOf(
            toModel(
                boardId = boardId,
                threadNum = no,
                imageboard = imageboard
            )
        ),
        title = sub ?: com ?: "",
        postCount = replies ?: 1,
        fileCount = images ?: 0,
        isPinned = sticky > 0,
        lasthit = lastModified,
        newMessageAmount = 0,
        lastPostNumber = 0,
        imageboard = imageboard,
    )

fun PostDto.toModel(boardId: String, threadNum: Int, imageboard: Imageboard): Post =
    Post(
        boardId = boardId,
        threadNum = threadNum,
        id = num,
        name = name,
        comment = comment,
        isOpPost = threadNum == num,
        date = date,
        email = email,
        files = files?.map { it.toModel(imageboard) } ?: emptyList(),
        fileCount = filesCount ?: files?.size ?: 0,
        isAuthorOp = op == 1,
        postCount = postsCount ?: 1,
        subject = subject,
        timestamp = timestamp,
        number = number,
        imageboard = imageboard,
    )

fun PostDto.toThread(boardId: String, imageboard: Imageboard): BoardThread =
    BoardThread(
        boardId = boardId,
        num = num,
        posts = listOf(
            toModel(
                boardId = boardId,
                threadNum = num,
                imageboard = imageboard
            )
        ),
        title = subject.ifEmpty { comment },
        postCount = postsCount ?: 1,
        fileCount = filesCount ?: files?.size ?: 0,
        isPinned = sticky > 0,
        lasthit = lasthit,
        newMessageAmount = 0,
        lastPostNumber = 0,
        imageboard = imageboard,
    )
