package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost

interface PostDao {
    @Insert
    fun insertPost(post: StoredPost)

    @Insert
    fun insertFile(file: StoredFile)

    @Query("SELECT * FROM posts WHERE boardId = :boardId AND num = threadNum")
    fun getOpPosts(boardId: String): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE num = threadNum")
    fun getOpPosts(): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE boardId = :boardId")
    fun getPosts(boardId: String): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE threadNum = :threadNum AND boardId = :boardId")
    fun getPosts(boardId: String, threadNum: Int): Observable<List<StoredPost>>

    @Query("SELECT * FROM posts WHERE num = :postNum AND boardId = :boardId")
    fun getPost(boardId: String, postNum: Int): Observable<StoredPost>

    @Query("SELECT * FROM files WHERE postNum = :postNum")
    fun getFiles(postNum: Int): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE boardId = :boardId AND threadNum = :threadNum")
    fun getFiles(boardId: String, threadNum: Int): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE threadNum = :postNum")
    fun getAllFilesFromThread(postNum: Int): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE boardId = :boardId")
    fun getFiles(boardId: String): Observable<List<StoredFile>>

    @Query("SELECT * FROM files WHERE isOpPostFile = 1")
    fun getOpFiles(): Observable<List<StoredFile>>

    @Query("DELETE FROM posts WHERE boardId = :boardId AND threadNum = :threadNum AND threadNum != num")
    fun deletePosts(boardId: String, threadNum: Int)

    @Query("DELETE FROM files WHERE boardId = :boardId AND threadNum = :threadNum AND threadNum != postNum")
    fun deleteFiles(boardId: String, threadNum: Int)

//    @Query("UPDATE files SET localPath = :url WHERE id = :id")
//    fun setDownloadedFile(id: String, url: String)
//  надо?
}