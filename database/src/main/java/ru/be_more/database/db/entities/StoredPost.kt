package ru.be_more.database.db.entities

import androidx.room.Entity

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
)