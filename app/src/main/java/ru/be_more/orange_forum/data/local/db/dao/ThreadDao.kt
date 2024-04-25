package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

@Dao
interface ThreadDao {
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(thread: StoredThread)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(thread: List<StoredThread>)


    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getFlow(boardId: String, threadNum: Int): Flow<StoredThread>

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getListFlow(boardId: String): Flow<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isQueued = 1")
    fun getQueuedFlow(): Flow<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    fun getFavoriteFlow(): Flow<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    suspend fun get(boardId: String, threadNum: Int): StoredThread?

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    suspend fun getFavorites(): List<StoredThread>

    @Query("SELECT * FROM threads WHERE isQueued = 1")
    suspend fun getQueued(): List<StoredThread>

    @Query("SELECT num FROM threads WHERE isFavorite = 1")
    suspend fun getFavoriteIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isQueued = 1")
    suspend fun getQueuedIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isDownloaded = 1")
    suspend fun getDownloadIdsSync(): List<Int>

    @Query("SELECT num FROM threads WHERE isHidden = 1")
    suspend fun getHiddenIdsSync(): List<Int>

    @Query("SELECT * FROM threads WHERE NOT lastPostRead = 0")
    suspend fun getLastReadPosts(): List<StoredThread>

    suspend fun getLastReadPost(): Map<Int, Int> =
        getLastReadPosts()
            .associateBy { it.num }
            .mapValues { it.value.lastPostRead }


    @Query("UPDATE threads SET isFavorite = :isFavorite WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setIsFavorite(boardId: String, threadNum: Int, isFavorite: Boolean)

    @Query("UPDATE threads SET isQueued = :isQueued WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setIsQueue(boardId: String, threadNum: Int, isQueued: Boolean)

    @Query("UPDATE threads SET isQueued = :isQueued")
    suspend fun setIsQueueForAll(isQueued: Boolean)

    @Query("UPDATE threads SET isHidden = :isHidden WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setIsHidden(boardId: String, threadNum: Int, isHidden: Boolean)

    @Query("UPDATE threads SET postCount = :postCount WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setPostCount(boardId: String, threadNum: Int, postCount: Int)

    @Query("UPDATE threads SET lasthit = :lasthit WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setLasthit(boardId: String, threadNum: Int, lasthit: Long)

    @Query("UPDATE threads SET hasNewMessages = :hasNewPost WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setHasNewPost(boardId: String, threadNum: Int, hasNewPost: Boolean)

    @Query("UPDATE threads SET isDrown = :isDrown WHERE boardId = :boardId AND num = :threadNum")
    suspend fun setIsDrown(boardId: String, threadNum: Int, isDrown: Boolean)

    @Query("UPDATE threads SET lastPostRead = :postNum WHERE boardId = :boardId AND num = :threadNum AND lastPostRead < :postNum")
    suspend fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int)


    @Query("DELETE FROM threads WHERE boardId = :boardId AND num = :threadNum")
    suspend fun delete(boardId: String, threadNum: Int)

    @Query("DELETE FROM threads WHERE boardId = :boardId AND isFavorite = 0 AND isDownloaded = 0 AND isHidden = 0 AND isQueued = 0")
    suspend fun deleteKeepingState(boardId: String)

    @Query("DELETE FROM threads WHERE boardId = :boardId AND num NOT IN (:threadNumList)")
    suspend fun deleteExceptGiven(boardId: String, threadNumList: List<Int>)
}