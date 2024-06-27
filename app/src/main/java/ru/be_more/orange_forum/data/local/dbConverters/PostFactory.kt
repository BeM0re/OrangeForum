package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredPost
import ru.be_more.model.model.Post

object PostFactory {

    fun fromEntity(storedPost: StoredPost): Post =
        Post(
            boardId = storedPost.boardId,
            threadNum = storedPost.threadNum,
            id = storedPost.id,
            isMyPost = storedPost.isMyPost,
            name = storedPost.name,
            comment = storedPost.comment,
            isOpPost = storedPost.isOpPost,
            date = storedPost.date,
            email = storedPost.email,
            files = storedPost.files.map { FileFactory.fromEntity(it) },
            fileCount = storedPost.filesCount,
            isAuthorOp = storedPost.isAuthorOp,
            postCount = storedPost.postsCount,
            subject = storedPost.subject,
            timestamp = storedPost.timestamp,
            number = storedPost.number,
            replies = storedPost.replies,
        )

    fun toEntity(post: Post) =
        StoredPost(
            boardId = post.boardId,
            threadNum = post.threadNum,
            id = post.id,
            isMyPost = post.isMyPost,
            name = post.name,
            comment = post.comment,
            isOpPost = post.isOpPost,
            date = post.date,
            email = post.email,
            files = post.files.map { FileFactory.toEntity(it) },
            filesCount = post.fileCount,
            isAuthorOp = post.isAuthorOp,
            postsCount = post.postCount,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number,
            replies = post.replies,
        )

}