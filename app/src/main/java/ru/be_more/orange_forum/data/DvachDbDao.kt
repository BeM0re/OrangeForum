package ru.be_more.orange_forum.data

import androidx.room.*
import io.reactivex.Observable
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.model.Post

@Dao
interface DvachDao {
    @Query("SELECT * FROM categories")
    fun getCategories(): Observable<List<StoredCategory>>

    @Query("SELECT COUNT(id) FROM boards WHERE id = :boardId")
    fun getBoardCount(boardId: String): Observable<Int>

    @Query("SELECT * FROM boards WHERE categoryId = :category")
    fun getBoards(category: String): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoard(boardId: String): Observable<StoredBoard>

    @Query("SELECT * FROM threads WHERE num = :threadNum")
    fun getThread(threadNum: Int): Observable<StoredThread>

    @Query("SELECT * FROM posts WHERE threadNum = :threadNum")
    fun getPost(threadNum: Int): Observable<StoredPost>

    @Query("SELECT * FROM files WHERE postNum = :postNum")
    fun getFiles(postNum: Int): Observable<StoredFile>


    @Insert
    fun insertCategory(category: StoredCategory)

    @Insert
    fun insertBoard(board: StoredBoard)

    @Insert
    fun insertThread(thread: StoredThread)

    @Insert
    fun insertPost(post: StoredPost)

    @Insert
    fun insertFile(file: StoredFile)

    @Delete
    fun deleteThread(thread: StoredThread)

    @Query("UPDATE files SET localPath = :url WHERE id = :id")
    fun setDownloadedFile(id: String, url: String)


}