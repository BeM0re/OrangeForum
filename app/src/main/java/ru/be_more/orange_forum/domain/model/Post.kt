package ru.be_more.orange_forum.domain.model

import android.os.Build
import java.time.Instant
import java.util.*

data class Post(
    val boardId: String,
    val threadNum: Int,
    val id: Int,
    val name: String,
    val comment: String,
    val isOpPost: Boolean,
    val date: String,
    val email: String,
    val files: List<AttachedFile> = listOf(),
    val fileCount: Int,
    val isAuthorOp: Boolean,
    val postCount: Int,
    val subject: String,
    val timestamp: Long,
    val number: Int, //Порядковый номер в треде
    val replies: Stack<Int> = Stack()
) : ModalContent {
    val dateTimeString =
        if (Build.VERSION.SDK_INT >= 26) Instant.ofEpochSecond(timestamp.toLong()).toString()
        else "todo" //todo
}