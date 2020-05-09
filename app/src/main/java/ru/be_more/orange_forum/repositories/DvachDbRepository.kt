package ru.be_more.orange_forum.repositories

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.services.DVACH_ROOT_URL
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject


class DvachDbRepository @Inject constructor(){

    private lateinit var dvachDbDao: DvachDao
    private lateinit var db: AppDatabase
    private var disposable: LinkedList<Disposable> = LinkedList()

    fun initDatabase() : DvachDao{
        db = App.getDatabase()
        dvachDbDao = db.dvachDao()
        return dvachDbDao
    }

    fun saveThread(thread: BoardThread, boardId: String, boardName: String) {

        disposable.add(
            dvachDbDao.getBoardCount(boardId)
                .subscribeOn(Schedulers.io())
                .subscribe({boardCount ->
                    if (boardCount ==0){
                        dvachDbDao.insertBoard(StoredBoard(boardId, "", boardName))
                    }

                    dvachDbDao.insertThread(toStoredThread(thread, boardId))

                    thread.posts.forEach { post ->
                        savePost(post, thread.num, boardId)
                    }
                },
                    { Log.d("M_DvachDbRepository", "error = $it") },
                    { Log.d("M_DvachDbRepository", "Done") })
        )

    }

    private fun savePost(post: Post, threadNum: Int, boardId: String){
        dvachDbDao.insertPost(toStoredPost(post, threadNum, boardId))
        post.files.forEach { file ->
            Observable.fromCallable { dvachDbDao.insertFile(toStoredFile(file, post.num, boardId)) }
                .subscribeOn(Schedulers.io())
                .subscribe({},
                    { Log.d("M_DvachDbRepository", "$it") })
        }
    }


    private fun downloadImage(url: String): Uri? {
//        val fulUrl = DVACH_ROOT_URL+url
        val glideUrl = GlideUrl(
            DVACH_ROOT_URL+url.substring(1), LazyHeaders.Builder()
                .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                        "_ga=GA1.2.57010468.1498700728; " +
                        "ageallow=1; " +
                        "_gid=GA1.2.1910512907.1585793763; " +
                        "_gat=1")
                .build()
        )
        val context = App.applicationContext()

        val requestOptions = RequestOptions().override(100)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        val bitmap = Glide.with(context)
            .asBitmap()
            .load(glideUrl)
            .apply(requestOptions)
            .submit()
            .get()

        try {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.d("M_GalleryFragment", "$ex")
                return null
            }
            // Continue only if the File was successfully created
            var fileName: Uri? = null
            if(photoFile != null) {
                fileName = FileProvider.getUriForFile(
                    context,
                    "ru.be_more.orange_forum.fileprovider",
                    photoFile
                )
                val out = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                Log.d("M_DvachDbRepository", "Image saved. Path = $fileName")
            }
            return fileName
        } catch (e: Exception) {
            Log.e("M_DvachDbRepository", "Image NOT saved.")
            return null
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = "${System.currentTimeMillis()}"
        val storageDir: File = App.applicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
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
        localPath = if (file.duration == "") downloadImage(file.path).toString() else "",
        webThumbnail = file.thumbnail,
        localThumbnail = downloadImage(file.thumbnail).toString(),
        duration = file.duration
    )

}