package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard

@Dao
interface BoardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoard(board: StoredBoard): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoardList(boardList: List<StoredBoard>): Completable


    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun observe(boardId: String): Observable<StoredBoard>

    @Query("SELECT * FROM boards")
    fun observeList(): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun get(boardId: String): Maybe<StoredBoard>

    @Query("SELECT * FROM boards")
    fun getList(): Single<List<StoredBoard>>

    @Query("SELECT id FROM boards WHERE isFavorite = 1")
    fun getFavorites(): Single<List<String>>


    @Query("UPDATE boards SET isFavorite = :isFavorite WHERE id = :boardId")
    fun markFavorite(boardId: String, isFavorite: Boolean): Completable


    @Query("DELETE FROM boards WHERE isFavorite = 0")
    fun deleteKeepingState(): Completable
}