package ru.be_more.orange_forum.repositories

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.services.DVACH_ROOT_URL
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject


class DvachDbRepository @Inject constructor(){

    enum class Purpose{
        DOWNLOAD,
        FAVORITE
    }

    private var dvachDbDao: DvachDao
    private var db: AppDatabase = App.getDatabase()
    private var disposables: LinkedList<Disposable> = LinkedList()

    init {
        dvachDbDao = db.dvachDao()
    }

    fun destroy(){
        disposables.forEach { it.dispose() }
    }

    fun initDatabase() : DvachDao{
        return dvachDbDao
    }

    fun saveThread(thread: BoardThread, boardId: String, boardName: String, purpose: Purpose) {

        disposables.add(
            dvachDbDao.getBoardCount(boardId)
                .subscribeOn(Schedulers.io())
                .subscribe({boardCount ->
                    if (boardCount ==0){
                        dvachDbDao.insertBoard(StoredBoard(boardId, "", boardName))
                    }

                    when (purpose){
                        Purpose.DOWNLOAD ->
                            dvachDbDao.insertThread(downloadedToStoredThread(thread, boardId))
                        Purpose.FAVORITE ->
                            dvachDbDao.insertThread(favoriteToStoredThread(thread, boardId))
                    }


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
            Observable.fromCallable {
                dvachDbDao.insertFile(toStoredFile(file, post.num, boardId, post.number, threadNum))
            }
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

        val bitmap = Glide.with(context)
            .asBitmap()
            .load(glideUrl)
            .submit()
            .get()

        try {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.d("M_GalleryFragment", "$ex")
                return null
            }
            var fileName: Uri? = null
            if(photoFile != null) {
                fileName = FileProvider.getUriForFile(
                    context,"ru.be_more.orange_forum.fileprovider", photoFile)
                val out = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
//                Log.d("M_DvachDbRepository", "Image saved. Path = $fileName")
            }
            return fileName
        } catch (e: Exception) {
            Log.e("M_DvachDbRepository", "Image NOT saved. Error = $e")
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

    fun getDownloads(): Observable<List<Board>> =
        Observable.zip(
            dvachDbDao.getBoards(),
            dvachDbDao.getDownloadedThreads(),
            dvachDbDao.getOpPosts(),
            dvachDbDao.getOpFiles(),
            Function4 { boards, threads, posts, files ->
                val boardResult = LinkedList<Board>()
                boards.forEach{ board ->
                    val thread = threads.filter { it.boardId == board.id }
                    if (thread.isNotEmpty())
                        boardResult.add(toModelBoard(
                            board,
                            toModelThreads(thread)
                        ))
                }

                posts.forEach { post ->
                    boardResult.find { it.id == post.boardId }
                        ?.threads?.find { it.num == post.threadNum }
                        ?.posts = listOf(toModelPost(
                        post,
                        files.filter { it.postNum == post.num && it.boardId == post.boardId})
                    )
                }

                boardResult
            }
        )

    fun getFavorites(): Observable<List<Board>> =
        Observable.zip(
            dvachDbDao.getBoards(),
            dvachDbDao.getFavoriteThreads(),
            dvachDbDao.getOpPosts(),
            dvachDbDao.getOpFiles(),
            Function4 { boards, threads, posts, files ->
                val boardResult = LinkedList<Board>()
                boards.forEach{ board ->
                    val thread = threads.filter { it.boardId == board.id }
                    if (thread.isNotEmpty())
                        boardResult.add(toModelBoard(
                            board,
                            toModelThreads(thread)
                        ))
                }

                posts.forEach { post ->
                    boardResult.find { it.id == post.boardId }
                        ?.threads?.find { it.num == post.threadNum }
                        ?.posts = listOf(toModelPost(
                        post,
                        files.filter { it.postNum == post.num && it.boardId == post.boardId})
                    )
                }

                boardResult
            }
        )


    fun getThreadOrEmpty(boardId: String, threadNum: Int): Observable<BoardThread?> =
        dvachDbDao.getThreadOrEmpty(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .zipWith(getPosts(boardId, threadNum), BiFunction {threads, posts ->
                if (threads.isNotEmpty())
                    toModelThread(threads[0], posts)
                else
                    BoardThread(0)
            })

    fun getThreadsOnBoard(boardId: String): Observable<List<BoardThread>> =
        dvachDbDao.getThreadsOnBoard(boardId)
            .subscribeOn(Schedulers.io())
            .map { threads -> toModelThreads(threads) }

    fun deleteThread(boardId: String, threadNum: Int) {
        dvachDbDao.deleteThread(boardId, threadNum)
    }

    fun getPosts(boardId: String, threadNum: Int): Observable<List<Post>> =
        dvachDbDao.getPosts(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .zipWith(dvachDbDao.getAllFilesFromThread(threadNum), BiFunction {posts, files ->
                posts.map { post -> toModelPost(post, files.filter { it.postNum == post.num }) }
            })

    fun getPost(boardId: String, postNum: Int): Observable<Post> =
        dvachDbDao.getPost(boardId, postNum)
            .subscribeOn(Schedulers.io())
            .zipWith(dvachDbDao.getFiles(postNum), BiFunction {post, files ->
                toModelPost(post, files)
            })



    fun markThreadFavorite(boardId: String, threadNum: Int) {
        disposables.add(
            dvachDbDao.getThreadOrEmpty(boardId, threadNum)
                .subscribe { thread ->
                    if (thread.isNotEmpty())
                        dvachDbDao.markThreadFavorite(boardId, threadNum)
                    else
                        dvachDbDao.insertThread(
                            StoredThread(
                                threadNum,
                                "",
                                boardId,
                                isFavorite = true)
                        )
                }
        )
    }

    fun unmarkThreadFavorite(boardId: String, threadNum: Int) {
        dvachDbDao.unmarkThreadFavorite(boardId, threadNum)
    }

    fun markThreadHidden(boardId: String, threadNum: Int) {
        disposables.add(
            dvachDbDao.getThreadOrEmpty(boardId, threadNum)
                .subscribe { thread ->
                    if (thread.isNotEmpty())
                        dvachDbDao.markThreadHidden(boardId, threadNum)
                    else
                        dvachDbDao.insertThread(
                            StoredThread(
                            threadNum,
                            "",
                            boardId,
                            isHidden = true)
                        )
                }
        )
    }

    fun unmarkThreadHidden(boardId: String, threadNum: Int) {
        dvachDbDao.unmarkThreadHidden(boardId, threadNum)
    }




    private fun toModelBoard(board: StoredBoard, threads: List<BoardThread>): Board = Board(
        name = board.name,
        id = board.id,
        threads = threads
    )

    private fun downloadedToStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
        num = thread.num,
        title = thread.title,
        boardId = boardId,
        isHidden = thread.isHidden,
        isDownloaded = true,
        isFavorite = thread.isFavorite
    )

    private fun favoriteToStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
        num = thread.num,
        title = thread.title,
        boardId = boardId,
        isHidden = thread.isHidden,
        isDownloaded = thread.isDownloaded,
        isFavorite = true
    )

    private fun toModelThread(thread: StoredThread, posts: List<Post>): BoardThread = BoardThread(
        num = thread.num,
        title = thread.title,
        isHidden = thread.isHidden,
        isDownloaded = thread.isHidden,
        isFavorite = thread.isFavorite,
        posts = posts
    )

    private fun toModelThreads(threads: List<StoredThread>): List<BoardThread> {
        val result =  LinkedList<BoardThread>()
        threads.forEach { thread ->
            result.add(BoardThread(
                num = thread.num,
                title = thread.title,
                isHidden = thread.isHidden,
                isDownloaded = thread.isHidden,
                isFavorite = thread.isFavorite)
        ) }
        return result
    }

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

    private fun toStoredFile(
        file: AttachFile,
        postNum: Int,
        boardId: String,
        postNumber: Int = 0,
        threadNum: Int
    ): StoredFile = StoredFile(
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
        duration = file.duration,
        isOpPostFile = postNumber == 1,
        threadNum = threadNum
    )

    private fun toModelPost(post: StoredPost, files: List<StoredFile>): Post = Post(
        num = post.num,
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
        replies = post.replies,
        files = files.map { toModelFile(it) }
    )

    private fun toModelFile(file: StoredFile): AttachFile = AttachFile(
        path = file.webPath,
        thumbnail = file.webThumbnail,
        localPath = file.localPath,
        localThumbnail = file.localThumbnail,
        duration = file.duration
    )
}