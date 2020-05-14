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

    @Query("SELECT * FROM boards")
    fun getBoards(): Observable<List<StoredBoard>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoard(boardId: String): Observable<StoredBoard>

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThread(boardId: String, threadNum: Int): Observable<StoredThread>

    //возвращает список из 1 или 0 элементов
    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThreadOrEmpty(boardId: String, threadNum: Int): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads")
    fun getThread(): Observable<List<StoredThread>>

//    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = (SELECT num FROM posts WHERE num = threadNum and boardId = :boardId)")
//    fun getThreadOpPosts(boardId: String): Observable<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getThreadOpPosts(boardId: String): Observable<List<StoredThread>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND num = threadNum")
    fun getOpPosts(boardId: String): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE number = 1")
    fun getOpPosts(): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId")
    fun getPosts(boardId: String): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE threadNum = :threadNum AND boardId = :boardId")
    fun getPosts(boardId: String, threadNum: Int): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE num = :postNum AND boardId = :boardId")
    fun getPost(boardId: String, postNum: Int): Observable<StoredPost>

    @Query("SELECT * FROM files WHERE postNum = :postNum")
    fun getFiles(postNum: Int): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE threadNum = :postNum")
    fun getAllFilesFromThread(postNum: Int): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE boardId = :boardId")
    fun getFiles(boardId: String): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE isOpPostFile = 1")
    fun getFiles(): Observable<List<StoredFile>>


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