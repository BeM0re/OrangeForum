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
    val files: List<FileDto>? = null,
    @SerializedName("files_count")
    val filesCount: Int? = null,
    val op: Int,
    @SerializedName("posts_count")
    val postsCount: Int? = null,
    val subject: String,
    val timestamp: Long,
    val number: Int, //order number 0 - 500(1000)
    val sticky: Int,
    val lasthit: Long,
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
            files = files?.map { it.toModel() } ?: emptyList(),
            fileCount = filesCount ?: files?.size ?: 0,
            isAuthorOp = op == 1,
            postCount = postsCount ?: 1,
            subject = subject,
            timestamp = timestamp,
            number = number
        )

    fun toThread(boardId: String) =
        BoardThread(
            boardId = boardId,
            num = num,
            posts = listOf(toModel(boardId, num)),
            title = subject.ifEmpty { comment },
            postCount = postsCount ?: 1,
            fileCount = filesCount ?: files?.size ?: 0,
            isPinned = sticky > 0,
            lasthit = lasthit,
        )
}
