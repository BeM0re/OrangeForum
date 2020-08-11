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
import ru.be_more.orange_forum.bus.RefreshDownload
import ru.be_more.orange_forum.consts.DOWNLOAD_TAG
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.services.DVACH_ROOT_URL
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
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
            Observable.zip(dvachDbDao.getBoardCount(boardId),
                dvachDbDao.getThreadCount(boardId, thread.num),
                BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                    if (boardCount == 0){
                        dvachDbDao.insertBoard(StoredBoard(boardId, "", boardName, false))
                    }
                    when (purpose){
                        Purpose.DOWNLOAD -> {
                            if (threadCount == 0) {
                                Log.d("M_DvachDbRepository", "1")
                                dvachDbDao.insertThread(downloadedToStoredThread(thread, boardId))
                                //вставляем все, в т.ч. оп-пост
                                thread.posts.forEach { post -> savePost(post, thread.num, boardId) }
                            }
                            else {
                                //оп-пост уже есть в базе. вставляем, начиная со 2-го поста
                                dvachDbDao.markThreadDownload(boardId, thread.num)
                                thread.posts.subList(1, thread.posts.size)
                                    .forEach { post ->
                                        Log.d("M_DvachDbRepository", "2")
                                        savePost(post, thread.num, boardId)

                                    }
                            }
                        }
                        Purpose.FAVORITE ->{
                            if (threadCount == 0) {
                                dvachDbDao.insertThread(favoriteToStoredThread(thread, boardId))
                                savePost(thread.posts[0], thread.num, boardId)
                            }
                            else
                                dvachDbDao.markThreadFavorite(boardId, thread.num)

                        }
                    }

                }
            )
            .observeOn(Schedulers.io())
            .subscribe({},
                {Log.d("M_DvachDbRepository", "on save thread error = $it")})
        )
    }

    fun markBoardFavorite(boardId: String, boardName: String) {
        disposables.add(
            dvachDbDao.getBoardCount(boardId)
                .observeOn(Schedulers.io())
                .subscribe ({ boardCount ->
                    if (boardCount == 0)
                        dvachDbDao.insertBoard(StoredBoard(boardId, "", boardName, true))
                    else
                        dvachDbDao.markBoardFavorite(boardId)
                },
                    {
                        Log.d("M_DvachDbRepository","on favoriting a board error = $it")
                    })
        )
    }

    private fun savePost(post: Post, threadNum: Int, boardId: String){
        dvachDbDao.insertPost(toStoredPost(post, threadNum, boardId))
        post.files.forEach { file ->
            dvachDbDao.insertFile(toStoredFile(file, post.num, boardId, threadNum))
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
            }
            return fileName
        } catch (e: Exception) {
            Log.e("M_DvachDbRepository", "Image NOT saved. Error = $e")
            return null
        }
    }

    private fun deleteImage(path: String){
        try {
            App.applicationContext().contentResolver.delete(Uri.parse(path), null, null)
        }
        catch (e: java.lang.Exception){
            Log.d("M_DvachDbRepository", "on delete error = $e")
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
                    else if (board.isFavorite)
                        boardResult.add(toModelBoard(
                            board,
                            listOf()
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
            .zipWith(getPosts(boardId, threadNum),
                BiFunction {threads, posts ->
                if (threads.isNotEmpty())
                    toModelThread(threads[0], posts)
                else
                    BoardThread(0)
            })

    fun getBoard(boardId: String): Observable<Board> =
        dvachDbDao.getBoard(boardId)
            .observeOn(Schedulers.io())
            .zipWith(dvachDbDao.getThreadsOnBoard(boardId), BiFunction { probablyBoard, threads ->
                return@BiFunction if (probablyBoard.isNotEmpty())
                    toModelBoard(probablyBoard[0], toModelThreads(threads))
                else
                    Board("", boardId, toModelThreads(threads), false)
            })

/*    fun getThreadsOnBoard(boardId: String): Observable<List<BoardThread>> =
        dvachDbDao.getThreadsOnBoard(boardId)
            .subscribeOn(Schedulers.io())
            .map { threads -> toModelThreads(threads) }*/

    fun deleteThread(boardId: String, threadNum: Int): Disposable =
        dvachDbDao.getFiles(boardId, threadNum)
            .subscribe (
                { files ->

                    files.forEach { file ->
                        if (file.localPath.isNotEmpty())
                            deleteImage(file.localPath)
                        if (file.localThumbnail.isNotEmpty())
                            deleteImage(file.localThumbnail)
                    }
                    dvachDbDao.deleteThread(boardId, threadNum)
                    dvachDbDao.deletePosts(boardId, threadNum)
                    dvachDbDao.deleteFiles(boardId, threadNum)
                    dvachDbDao.unmarkThreadDownload(boardId, threadNum)
                    App.getBus().onNext(Pair(RefreshDownload, DOWNLOAD_TAG))
                },
                { Log.d("M_DvachDbRepository", "on delete thread error = $it") }
            )

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

    fun unmarkThreadFavorite(boardId: String, threadNum: Int) {
        dvachDbDao.unmarkThreadFavorite(boardId, threadNum)
    }

    fun unmarkBoardFavorite(boardId: String) {
        dvachDbDao.unmarkBoardFavorite(boardId)
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

    //TODO добавить toStoredBoard
    private fun toModelBoard(board: StoredBoard, threads: List<BoardThread>): Board = Board(
        name = board.name,
        id = board.id,
        threads = threads,
        isFavorite = board.isFavorite
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
        isDownloaded = thread.isDownloaded,
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
                isDownloaded = thread.isDownloaded,
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
        isOpPostFile = postNum == threadNum,
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