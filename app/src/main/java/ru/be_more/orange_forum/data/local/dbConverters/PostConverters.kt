package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredPost
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import ru.be_more.model.model.Post

fun StoredPost.toModel(): Post =
    Post(
        boardId = boardId,
        threadNum = threadNum,
        id = id,
        isMyPost = isMyPost,
        name = name,
        comment = comment,
        isOpPost = isOpPost,
        date = date,
        email = email,
        files = files.map { it.toModel() },
        fileCount = filesCount,
        isAuthorOp = isAuthorOp,
        postCount = postsCount,
        subject = subject,
        timestamp = timestamp,
        number = number,
        replies = replies,
        imageboardType = ImageboardType.valueOf(imageboardType),
    )

fun Post.toEntity() =
    StoredPost(
        boardId = boardId,
        threadNum = threadNum,
        id = id,
        isMyPost = isMyPost,
        name = name,
        comment = comment,
        isOpPost = isOpPost,
        date = date,
        email = email,
        files = files.map { it.toEntity() },
        filesCount = fileCount,
        isAuthorOp = isAuthorOp,
        postsCount = postCount,
        subject = subject,
        timestamp = timestamp,
        number = number,
        replies = replies,
        imageboardType = imageboardType.name,
    )
