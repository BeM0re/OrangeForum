package ru.be_more.orange_forum.repositories

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.bus.RefreshDownload
import ru.be_more.orange_forum.consts.DOWNLOAD_TAG
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.data.local.db.AppDatabase
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
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

    //TODO добавить toStoredBoard

}