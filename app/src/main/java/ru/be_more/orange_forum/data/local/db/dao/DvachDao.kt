package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

@Dao
interface DvachDao {
    //boards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoard(board: StoredBoard)

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun getBoardCount(boardId: String): Single<Int>

    @Query("SELECT * FROM boards")
    fun getBoards(): Single<List<StoredBoard>>

    @Query("SELECT * FROM boards")
    fun getBoardsObservable(): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoard(boardId: String): Maybe<StoredBoard>

    @Query("UPDATE boards SET isFavorite = 1 WHERE id = :boardId ")
    fun markBoardFavorite(boardId: String)

    @Query("UPDATE boards SET isFavorite = 0 WHERE id = :boardId")
    fun unmarkBoardFavorite(boardId: String)

    //threads
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThread(thread: StoredThread)

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThread(boardId: String, threadNum: Int): Maybe<StoredThread>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    fun getDownloadedAndFavoritesThreads(): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isQueued = 1")
    fun getQueuedThreads(): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isFavorite = 1")
    fun getFavoriteThreads(): Single<List<StoredThread>>

    @Query("UPDATE threads SET lastPostNumber = :postNum WHERE boardId = :boardId AND num = :threadNum")
    fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int)
}