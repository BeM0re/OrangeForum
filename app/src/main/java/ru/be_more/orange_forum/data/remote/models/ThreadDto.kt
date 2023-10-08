package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.BoardThread

data class ThreadDto(
    @SerializedName("current_thread")
    val num: Int,
    @SerializedName("posts_count")
    val postCount: Int = 0,
    @SerializedName("files_count")
    val fileCount: Int = 0,
    val title: String = "",
    @SerializedName("threads")
    val threads: List<InnerThreadDto>
) {
    fun toModel(boardId: String) =
        BoardThread(
            boardId = boardId,
            num = num,
            posts = threads
                .getOrNull(0)
                ?.posts
                ?.map { it.toModel(boardId, num) }
                ?: emptyList(),
            title = title,
            postCount = postCount,
            fileCount = fileCount,
        )

    data class InnerThreadDto(
        val posts: List<PostDto> = emptyList()
    )
}