package ru.be_more.orange_forum.data

import android.media.ThumbnailUtils
import androidx.room.*

@Entity(tableName = "categories")
data class StoredCategory(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(
    tableName = "boards"
    /*foreignKeys = [ForeignKey(entity = StoredCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE)]*/)
data class StoredBoard(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String
)

@Entity(
    tableName = "threads",
    foreignKeys = [ForeignKey(entity = StoredBoard::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("boardId"),
        onDelete = ForeignKey.CASCADE)])
data class StoredThread(
    @PrimaryKey val num: Int,
    val title: String,
    val boardId: String,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(
    tableName = "posts",
    foreignKeys = [ForeignKey(entity = StoredThread::class,
        parentColumns = arrayOf("num"),
        childColumns = arrayOf("threadNum"),
        onDelete = ForeignKey.CASCADE)])
data class StoredPost(
    val boardId: String,
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
    @TypeConverters(ReplyConverter::class) val replies: List<Int> = listOf()
//    @PrimaryKey(autoGenerate = true) val id: Long? = null
)

@Entity(
    tableName = "files",
    foreignKeys = [ForeignKey(entity = StoredPost::class,
        parentColumns = arrayOf("num"),
        childColumns = arrayOf("postNum"),
        onDelete = ForeignKey.CASCADE)])
data class StoredFile(
    val boardId: String,
    val postNum: Int,
    var displayName: String = "",
    var height: Int = 0,
    var width: Int = 0,
    var tn_height: Int = 0,
    var tn_width: Int = 0,
    var webPath: String = "",
    var localPath: String = "",
    var webThumbnail: String = "",
    var localThumbnail: String = "",
    var duration : String = "",
    @PrimaryKey(autoGenerate = true) val id: Long? = null
)
