package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import ru.be_more.orange_forum.domain.model.Post
import java.util.*

@Entity(
    tableName = "posts",
    primaryKeys = ["boardId", "id"]
)
data class StoredPost(
    val boardId: String,
    val threadNum: Int,
    val id: Int,
    val isMyPost: Boolean,
    val name: String,
    val comment: String,
    val isOpPost: Boolean,
    val date: String,
    val email: String,
    val files: List<StoredFile> = listOf(),
    val filesCount: Int,
    val postsCount: Int,
    val isAuthorOp: Boolean,
    val subject: String,
    val timestamp: Long,
    val number: Int, //Порядковый номер в треде
    val replies: List<Int>,
) {
    constructor(post: Post): this(
        boardId = post.boardId,
        threadNum = post.threadNum,
        id = post.id,
        isMyPost = post.isMyPost,
        name = post.name,
        comment = post.comment,
        isOpPost = post.isOpPost,
        date = post.date,
        email = post.email,
        files = post.files.map { StoredFile(it) },
        filesCount = post.fileCount,
        isAuthorOp = post.isAuthorOp,
        postsCount = post.postCount,
        subject = post.subject,
        timestamp = post.timestamp,
        number = post.number,
        replies = post.replies,
    )

    fun toModel() = Post(
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
    )
}