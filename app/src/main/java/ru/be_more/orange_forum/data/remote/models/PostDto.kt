package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

data class PostDto(
    val num: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<FileDto>,
    @SerializedName("files_count")
    val filesCount: Int,
    val op: Int,
    @SerializedName("posts_count")
    val postsCount: Int,
    val subject: String,
    val timestamp: Long,
    val number: Int //order number 0 - 500(1000)
) {
    fun toModel(boardId: String, threadNum: Int) =
        Post(
            boardId = boardId,
            threadNum = threadNum,
            id = num,
            name = name,
            comment = comment,
            isOpPost = threadNum == num,
            date = date,
            email = email,
            files = files.map { it.toModel() },
            fileCount = filesCount,
            isAuthorOp = op == 1,
            postCount = postsCount,
            subject = subject,
            timestamp = timestamp,
            number = number
        )

    fun toThread(boardId: String) =
        BoardThread(
            boardId = boardId,
            num = num,
            posts = listOf(toModel(boardId, num)),
            title = subject,
            postCount = postsCount,
            fileCount = filesCount,
        )
}
