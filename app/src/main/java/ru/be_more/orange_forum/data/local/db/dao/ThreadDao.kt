package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

interface ThreadDao {
    @Insert
    fun insertThread(thread: StoredThread)

    @Query("SELECT COUNT(num) FROM threads WHERE boardId = :boardId AND num =:threadNum")
    fun getThreadCount(boardId: String, threadNum: Int): Observable<Int>

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThread(boardId: String, threadNum: Int): Observable<StoredThread>

    //возвращает список из 1 или 0 элементов
    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThreadOrEmpty(boardId: String, threadNum: Int): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1")
    fun getDownloadedThreads(): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isFavorite = 1")
    fun getFavoriteThreads(): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getThreadOpPosts(boardId: String): Observable<List<StoredThread>>

    @Query("UPDATE threads SET isDownloaded = 1 WHERE boardId = :boardId AND num = :threadNum")
    fun markThreadDownload(boardId: String, threadNum: Int)

    @Query("UPDATE threads SET isFavorite = 1 WHERE boardId = :boardId AND num = :threadNum")
    fun markThreadFavorite(boardId: String, threadNum: Int)

    @Query("UPDATE threads SET isFavorite = 0 WHERE boardId = :boardId AND num = :threadNum")
    fun unmarkThreadFavorite(boardId: String, threadNum: Int)

    @Query("UPDATE threads SET isHidden = 1 WHERE boardId = :boardId AND num = :threadNum")
    fun markThreadHidden(boardId: String, threadNum: Int)

    @Query("UPDATE threads SET isHidden = 0 WHERE boardId = :boardId AND num = :threadNum")
    fun unmarkThreadHidden(boardId: String, threadNum: Int)

    @Query("UPDATE threads SET isDownloaded = 0 WHERE boardId = :boardId AND num = :threadNum")
    fun unmarkThreadDownload(boardId: String, threadNum: Int)

    @Query("DELETE FROM threads WHERE boardId = :boardId AND num = :threadNum AND isFavorite = 0")
    fun deleteThread(boardId: String, threadNum: Int)
}