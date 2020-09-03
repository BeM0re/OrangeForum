package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

interface BoardDao {
    @Insert
    fun insertBoard(board: StoredBoard)

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun getBoardCount(boardId: String): Observable<Int>

    @Query("SELECT * FROM boards WHERE categoryId = :category")
    fun getBoards(category: String): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards")
    fun getBoards(): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoard(boardId: String): Observable<List<StoredBoard>>
//     список для того, чтобы 0 тоже возвращался. Тут либо 0, либо 1

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getThreadsOnBoard(boardId: String): Observable<List<StoredThread>>

    @Query("UPDATE boards SET isFavorite = 1 WHERE id = :boardId ")
    fun markBoardFavorite(boardId: String)

    @Query("UPDATE boards SET isFavorite = 0 WHERE id = :boardId")
    fun unmarkBoardFavorite(boardId: String)
}