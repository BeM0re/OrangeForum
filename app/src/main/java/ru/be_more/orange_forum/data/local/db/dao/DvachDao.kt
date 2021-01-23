package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

@Dao
interface DvachDao {
    //boards
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

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getThreads(boardId: String): Single<List<StoredThread>>

    @Query("UPDATE boards SET isFavorite = 1 WHERE id = :boardId ")
    fun markBoardFavorite(boardId: String)

    @Query("UPDATE boards SET isFavorite = 0 WHERE id = :boardId")
    fun unmarkBoardFavorite(boardId: String)

    //threads
    @Insert
    fun insertThread(thread: StoredThread)

    @Query("SELECT COUNT(num) FROM threads WHERE boardId = :boardId AND num =:threadNum")
    fun getThreadCount(boardId: String, threadNum: Int): Single<Int>

    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThread(boardId: String, threadNum: Int): Single<StoredThread>

    //возвращает список из 1 или 0 элементов
    @Query("SELECT * FROM threads WHERE boardId = :boardId AND num = :threadNum")
    fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isDownloaded = 1 OR isFavorite = 1")
    fun getDownloadedAndFavoritesThreads(): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE isFavorite = 1")
    fun getFavoriteThreads(): Single<List<StoredThread>>

    @Query("SELECT * FROM threads WHERE boardId = :boardId")
    fun getThreadOpPosts(boardId: String): Single<List<StoredThread>>

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

    //posts
    @Insert
    fun insertPost(post: StoredPost)

    @Insert
    fun insertFile(file: StoredFile)

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND num = threadNum")
    fun getOpPosts(boardId: String): Single<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE num = threadNum")
    fun getOpPosts(): Single<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId")
    fun getPosts(boardId: String): Single<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE threadNum = :threadNum AND boardId = :boardId")
    fun getPosts(boardId: String, threadNum: Int): Single<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE num = :postNum AND boardId = :boardId")
    fun getPost(boardId: String, postNum: Int): Single<StoredPost>

    //files
    @Query("SELECT * FROM files WHERE postNum = :postNum")
    fun getFiles(postNum: Int): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE postNum = :postNum AND boardId = :boardId ")
    fun getPostFiles(postNum: Int, boardId: String): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE boardId = :boardId AND threadNum = :threadNum")
    fun getThreadFiles(boardId: String, threadNum: Int): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE threadNum = :postNum AND boardId = :boardId")
    fun getAllFilesFromThread(postNum: Int, boardId: String): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE threadNum = :postNum AND boardId = :boardId AND postNum != threadNum")
    fun getNonOpFilesFromThread(postNum: Int, boardId: String): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE boardId = :boardId")
    fun getFiles(boardId: String): Single<List<StoredFile>>

    @Query("SELECT * FROM files WHERE isOpPostFile = 1")
    fun getOpFiles(): Single<List<StoredFile>>

    @Query("DELETE FROM posts WHERE boardId = :boardId AND threadNum = :threadNum AND threadNum != num")
    fun deletePosts(boardId: String, threadNum: Int)

    @Query("DELETE FROM files WHERE boardId = :boardId AND threadNum = :threadNum AND threadNum != postNum")
    fun deleteThreadFiles(boardId: String, threadNum: Int)

    @Query("DELETE FROM files WHERE boardId = :boardId AND threadNum = :threadNum AND threadNum != postNum")
    fun deletePostFiles(boardId: String, threadNum: Int)

//    @Query("UPDATE files SET localPath = :url WHERE id = :id")
//    fun setDownloadedFile(id: String, url: String)
//  надо?
}