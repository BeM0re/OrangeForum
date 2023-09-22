package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

data class PostDto(
    val id: Int,
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
    val timestamp: Int,
    val number: Int //order number 0 - 500(1000)
) {
    fun toModel(boardId: String, threadNum: Int) =
        Post(
            boardId = boardId,
            threadNum = threadNum,
            id = id,
            name = name,
            comment = comment,
            isOpPost = threadNum == id,
            date = date,
            email = email,
            files = files.map { it.toModel() },
            filesCount = filesCount,
            isAuthorOp = op == 1,
            postsCount = postsCount,
            subject = subject,
            timestamp = timestamp,
            number = number
        )

    fun toThread(boardId: String) =
        BoardThread(
            num = id,
            posts = listOf(toModel(boardId, id)),
            title = subject,
            boardId = boardId,
        )
}