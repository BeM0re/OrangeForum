package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

interface BoardDao {
    @Insert
    fun insertBoard(board: StoredBoard)

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun getBoardCount(boardId: String): Single<Int>

    @Query("SELECT * FROM boards WHERE categoryId = :category")
    fun getBoards(category: String): Single<List<StoredBoard>>

    @Query("SELECT * FROM boards")
    fun getBoards(): Single<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoard(boardId: String): Single<List<StoredBoard>>
//     список для того, чтобы 0 тоже возвращался. Тут либо 0, либо 1

    @Query("UPDATE boards SET isFavorite = 1 WHERE id = :boardId ")
    fun markBoardFavorite(boardId: String)

    @Query("UPDATE boards SET isFavorite = 0 WHERE id = :boardId")
    fun unmarkBoardFavorite(boardId: String)
}