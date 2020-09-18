package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.utils.ReplyConverter
import java.util.*

@Entity(
    tableName = "posts"/*,
    foreignKeys = [ForeignKey(entity = StoredThread::class,
        parentColumns = arrayOf("num"),
        childColumns = arrayOf("threadNum"),
        onDelete = ForeignKey.CASCADE)]*/)
data class StoredPost(
    val boardId: String,
//    @PrimaryKey val num: Int,
    @PrimaryKey val num: Int,
    val threadNum: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files_count: Int,
    val op: Int,
    val posts_count: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int, //Порядковый номер в треде
    @TypeConverters(ReplyConverter::class) val replies: Stack<Int> = Stack()
)