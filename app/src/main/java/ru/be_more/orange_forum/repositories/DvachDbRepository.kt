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

    private lateinit var dvachDbDao: DvachDao
    private var db: AppDatabase = App.getDatabase()
    private var disposable: LinkedList<Disposable> = LinkedList()

    init {

        dvachDbDao = db.dvachDao()
    }

    fun initDatabase() : DvachDao{
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

    fun getBoardNames(): Observable<List<Board>> =
        dvachDbDao.getBoards()
            .subscribeOn(Schedulers.io())
            .switchMap { boards ->
                val boardNames = LinkedList<Board>()
                boards.forEach { board -> boardNames.add(toModelBoardName(board)) }
                return@switchMap Observable.just(boardNames)
            }

    fun getBoards(): Observable<List<Board>> =
        dvachDbDao.getBoards()
            .subscribeOn(Schedulers.io())
            .flatMap { boards ->
//                Log.d("M_DvachDbRepository", "board = $boards")
                Observable.create<List<Board>> { emitter ->
                    val modelBoards = LinkedList<Board>()
                    boards.forEach { board ->
                        getThreadOpPosts(board.id)
                            .subscribeOn(Schedulers.io())
                            .subscribe ({ threads ->
//                                Log.d("M_DvachDbRepository", "threads = $threads")
                                modelBoards.remove(modelBoards.find { boardToFind -> boardToFind.id == board.id })
                                modelBoards.add(toModelBoard(board, threads))
                                emitter.onNext(modelBoards)
//                                Log.d("M_DvachDbRepository", "modelBoards = $modelBoards")
                            },
                                { Log.d("M_DvachDbRepository", "getThreadOpPosts error = $it")}
                            )
                    }
                }

            }

    //возвращает список тредов одной борды с одним оп-постом в каждом треде
    private fun getThreadOpPosts(boardId: String): Observable<List<BoardThread>> =
        dvachDbDao.getThreadOpPosts(boardId)
            .subscribeOn(Schedulers.io())
            .flatMap { threads ->
                Observable.create<List<BoardThread>> { emitter ->
                    val modelsOpPosts = LinkedList<BoardThread>()
                    threads.forEach { thread ->
                        getPost(thread.boardId, thread.num)
                            .subscribe ({ post ->
                                modelsOpPosts.add(opPostToThread(thread, post))
                                emitter.onNext(modelsOpPosts)
                            },
                                { Log.d("M_DvachDbRepository", "getPost error = $it")}
                            )
                    }
                }
            }

    fun getOpPosts(boardId: String): Observable<List<Post>> =
        dvachDbDao.getOpPosts(boardId)
            .subscribeOn(Schedulers.io())
            .switchMap { opPosts ->
                val modelsOpPosts = LinkedList<Post> ()
                    opPosts.forEach { opPost -> getPost(opPost.boardId, opPost.threadNum)
                        .subscribe { modelsOpPosts.add(it) }}
                return@switchMap Observable.just(modelsOpPosts)
            }

    fun getThread(boardId: String, threadNum: Int): Observable<BoardThread> =
        dvachDbDao.getThread(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .zipWith(getPosts(boardId, threadNum), BiFunction {thread, posts ->
                toModelThread(thread, posts)
            })

    fun getPosts(boardId: String, threadNum: Int): Observable<List<Post>> =
        dvachDbDao.getPosts(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .zipWith(dvachDbDao.getFiles(threadNum), BiFunction {posts, files ->
                posts.map { post -> toModelPost(post, files.filter { it.postNum == post.num }) }
            })

    fun getPost(boardId: String, postNum: Int): Observable<Post> =
        dvachDbDao.getPost(boardId, postNum)
            .subscribeOn(Schedulers.io())
            .zipWith(dvachDbDao.getFiles(postNum), BiFunction {post, files ->
                toModelPost(post, files)
            })

    private fun opPostToThread(thread: StoredThread, post: Post): BoardThread = BoardThread(
        num = post.num,
        title = post.subject,
        isHidden = thread.isHidden,
        isDownloaded = thread.isDownloaded,
        isFavorite = thread.isFavorite,
        posts = listOf(post)
    )

    private fun toModelBoards(boards: List<StoredBoard>): List<Board>{
        val modelBoards = LinkedList<Board>()
        boards.forEach { board ->
            getThreadOpPosts(board.id)
                .subscribe{ threads ->
                    modelBoards.add(toModelBoard(board,threads))
                }
        }
        return modelBoards
    }

    private fun toModelBoard(board: StoredBoard, threads: List<BoardThread>): Board = Board(
        name = board.name,
        id = board.id,
        threads = threads
    )

    private fun toModelBoardName(board: StoredBoard): Board = Board(
        name = board.name,
        id = board.id
    )

    private fun toStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
        num = thread.num,
        title = thread.title,
        boardId = boardId,
        isHidden = thread.isHidden,
        isDownloaded = thread.isHidden,
        isFavorite = thread.isFavorite
    )

    private fun toModelThread(thread: StoredThread, posts: List<Post>): BoardThread = BoardThread(
        num = thread.num,
        title = thread.title,
        isHidden = thread.isHidden,
        isDownloaded = thread.isHidden,
        isFavorite = thread.isFavorite,
        posts = posts
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
        path = file.localPath,
        thumbnail = file.localThumbnail,
        duration = file.duration
    )
}