package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

@Dao
interface ThreadDao {
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun insert(thread: StoredThread): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(thread: List<StoredThread>): Completable


    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun observe(boardId: String, threadNum: Int): Observable<StoredThread>

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun observeList(boardId: String): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isQueued = 1")
    fun observeQueue(): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    fun observeFavorites(): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun get(boardId: String, threadNum: Int): Maybe<StoredThread>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    fun getFavorites(): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isQueued = 1")
    fun getQueued(): Single<List<StoredThread>>


    @Query("SELECT num FROM threads WHERE isFavorite = 1")
    fun getFavoriteIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isQueued = 1")
    fun getQueuedIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isDownloaded = 1")
    fun getDownloadIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isHidden = 1")
    fun getHiddenIdsSync(): List<Int>


    @Query("UPDATE threads SET isFavorite = :isFavorite WHERE boardId = :boardId AND num = :threadNum")
    fun setIsFavorite(boardId: String, threadNum: Int, isFavorite: Boolean): Completable

    @Query("UPDATE threads SET isQueued = :isQueued WHERE boardId = :boardId AND num = :threadNum")
    fun setIsQueue(boardId: String, threadNum: Int, isQueued: Boolean): Completable

    @Query("UPDATE threads SET isQueued = :isQueued")
    fun setIsQueueForAll(isQueued: Boolean): Completable

    @Query("UPDATE threads SET isHidden = :isHidden WHERE boardId = :boardId AND num = :threadNum")
    fun setIsHidden(boardId: String, threadNum: Int, isHidden: Boolean): Completable

    @Query("UPDATE threads SET lastPostNumber = :postNum WHERE boardId = :boardId AND num = :threadNum")
    fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int): Completable


    @Query("DELETE FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun delete(boardId: String, threadNum: Int): Completable

    @Query("DELETE FROM threads WHERE boardId = :boardId AND isFavorite = 0 AND isDownloaded = 0 AND isHidden = 0 AND isQueued = 0")
    fun deleteKeepingState(boardId: String): Completable

    @Query("DELETE FROM threads WHERE boardId = :boardId AND num NOT IN (:threadNumList)")
    fun deleteExceptGiven(boardId: String, threadNumList: List<Int>): Completable
}