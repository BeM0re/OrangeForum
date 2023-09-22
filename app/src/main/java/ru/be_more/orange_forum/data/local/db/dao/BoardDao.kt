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

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun getCount(boardId: String): Single<Int>

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun observeCount(boardId: String): Observable<Int>

    @Query("SELECT * FROM boards")
    fun getList(): Single<List<StoredBoard>>

    @Query("SELECT * FROM boards")
    fun observeList(): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun get(boardId: String): Maybe<StoredBoard>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun observe(boardId: String): Observable<StoredBoard>

    @Query("UPDATE boards SET isFavorite = :isFavorite WHERE id = :boardId")
    fun markFavorite(boardId: String, isFavorite: Boolean): Completable
}