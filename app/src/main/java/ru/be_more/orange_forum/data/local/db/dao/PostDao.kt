package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredPost

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: StoredPost): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<StoredPost>): Completable


    @Query("SELECT * FROM posts WHERE boardId = :boardId AND isOpPost = 1")
    fun observeOp(boardId: String): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    fun observe(boardId: String, threadNum: Int): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    fun getThreadPosts(boardId: String, threadNum: Int): Single<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND id = :postId")
    fun get(boardId: String, postId: Int): Maybe<StoredPost>

    @Query("SELECT MAX(id) FROM posts WHERE boardId = :boardId AND threadNum = :threadNum")
    fun getLatestPostId(boardId: String, threadNum: Int): Maybe<Int>


    @Query("DELETE FROM posts WHERE boardId = :boardId AND threadNum = :threadNum AND isMyPost = 0")
    fun delete(boardId: String, threadNum: Int): Completable
}