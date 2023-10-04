package ru.be_more.orange_forum.data.remote.models

data class ThreadInfoResponseDto(
    val result: Int,
    val thread: ThreadInfoDto,
) {


/*    fun toModel(boardId: String) =
        BoardThread(
            boardId = boardId,
            num = num,
            posts = threads.posts.map { it.toModel(boardId, num) },
            title = title,
            postCount = postCount,
            fileCount = fileCount,
        )*/

    data class ThreadInfoDto(
        val num: Int,
        val posts: Int,
        val timestamp: Int,
    )
}