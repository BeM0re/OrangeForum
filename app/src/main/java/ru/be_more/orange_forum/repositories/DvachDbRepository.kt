package ru.be_more.orange_forum.repositories

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class DvachDbRepository @Inject constructor(){

    private lateinit var dvachDbDao: DvachDao
    private lateinit var db: AppDatabase

    fun initDatabase() : DvachDao{
        db = App.getDatabase()
        dvachDbDao = db.dvachDao()
        return dvachDbDao
    }

    fun saveThread(thread: BoardThread, boardId: String) {
        dvachDbDao.insertThread(toStoredThread(thread, boardId))
        thread.posts.forEach { post ->
            savePost(post, thread.num, boardId)
        }
    }

    private fun savePost(post: Post, threadNum: Int, boardId: String){
        dvachDbDao.insertPost(toStoredPost(post, threadNum, boardId))
        post.files.forEach { file ->
            dvachDbDao.insertFile(toStoredFile(file, post.num, boardId))
        }
    }


    private fun downloadImage(url: String): String{
        val fileName = "${System.currentTimeMillis()}.jpg"
        val context = App.applicationContext()

        val requestOptions = RequestOptions().override(100)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        val bitmap = Glide.with(context)
            .asBitmap()
            .load(url)
            .apply(requestOptions)
            .submit()
            .get()

        return try {
            var file = context.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, fileName)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()
            Log.d("M_DvachDbRepository", "Image saved. Path = $fileName")
            fileName
        } catch (e: Exception) {
            Log.e("M_DvachDbRepository", "Image NOT saved.")
            ""
        }
    }

    private fun toStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
        num = thread.num,
        title = thread.title,
        boardId = boardId,
        isHidden = thread.isHidden,
        isDownloaded = thread.isHidden,
        isFavorite = thread.isFavorite
    )

    private fun toModelThread(thread: StoredThread): BoardThread = BoardThread(
        num = thread.num,
        title = thread.title,
        isHidden = thread.isHidden,
        isDownloaded = thread.isHidden,
        isFavorite = thread.isFavorite
    )

    private fun toStoredPost(post: Post, threadNum: Int, boardId: String): StoredPost = StoredPost(
        boardId = boardId,
        num = post.num,
        threadNum = threadNum,
        name = post.name,
        comment = post.comment,
        date = post.date,
        email = post.email,
        files_count = post.files_count,
        op = post.op,
        posts_count = post.posts_count,
        subject = post.subject,
        timestamp = post.timestamp,
        number = post.number,
        replies = post.replies
    )

    private fun toStoredFile(file: AttachFile, postNum: Int, boardId: String): StoredFile = StoredFile(
        boardId = boardId,
        postNum = postNum,
        displayName = file.displayName,
        height = file.height,
        width = file.width,
        tn_height = file.tn_height,
        tn_width = file.tn_width,
        webPath = file.path,
        localPath = downloadImage(file.path),
        webThumbnail = file.thumbnail,
        localThumbnail = downloadImage(file.thumbnail),
        duration = file.duration
    )
}