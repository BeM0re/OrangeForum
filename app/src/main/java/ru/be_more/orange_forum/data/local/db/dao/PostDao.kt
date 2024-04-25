package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.data.local.db.entities.StoredPost

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: StoredPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<StoredPost>)


    @Query("SELECT * FROM posts WHERE boardId = :boardId AND isOpPost = 1")
    fun getOpListFlow(boardId: String): Flow<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    fun getListFlow(boardId: String, threadNum: Int): Flow<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    suspend fun getThreadPosts(boardId: String, threadNum: Int): List<StoredPost>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND id = :postId")
    suspend fun get(boardId: String, postId: Int): StoredPost?

    @Query("SELECT MAX(id) FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    suspend fun getLatestPostId(boardId: String, threadNum: Int): Int?


    @Query("DELETE FROM posts WHERE boardId = :boardId AND threadNum = :threadNum AND isMyPost = 0")
    suspend fun delete(boardId: String, threadNum: Int)
}