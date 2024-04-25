package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard

@Dao
interface BoardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: StoredBoard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardList(boardList: List<StoredBoard>)

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getFlow(boardId: String): Flow<StoredBoard>

    @Query("SELECT * FROM boards")
    fun getListFlow(): Flow<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    suspend fun get(boardId: String): StoredBoard?

    @Query("SELECT * FROM boards")
    suspend fun getList(): List<StoredBoard>

    @Query("SELECT id FROM boards WHERE isFavorite = 1")
    suspend fun getFavorites(): List<String>


    @Query("UPDATE boards SET isFavorite = :isFavorite WHERE id = :boardId")
    suspend fun markFavorite(boardId: String, isFavorite: Boolean)


    @Query("DELETE FROM boards WHERE isFavorite = 0")
    suspend fun deleteKeepingState()
}