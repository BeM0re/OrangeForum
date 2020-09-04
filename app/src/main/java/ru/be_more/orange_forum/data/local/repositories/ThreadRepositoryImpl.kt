package ru.be_more.orange_forum.data.local.repositories

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.downloadedToStoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.favoriteToStoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredFile
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredPost
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachDbRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepositoryImpl @Inject constructor(
    private val threadDao: ThreadDao,
    private val boardDao: BoardDao
) : LocalContract.ThreadRepository {

//    @SuppressLint("CheckResult")
//    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?> =
//        threadDao.getThreadOrEmpty(boardId, threadNum)
//            .map {threadList ->
//                Log.d("M_ThreadRepositoryImpl","")
//                return@map if (threadList.isNotEmpty())
//                    toModelThread(threadList[0])
//                else
//
//            }
//            .processSingle()


    @SuppressLint("CheckResult")
    //TODO вынести обращение в интерактор
    override fun downloadThread(
        thread: BoardThread,
        boardId: String) =
            Single.zip(boardDao.getBoardCount(boardId),
                threadDao.getThreadCount(boardId, thread.num),
                BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                    if (boardCount == 0){
                        boardDao.insertBoard(StoredBoard(boardId, "", boardId, false))
                    }
                    if (threadCount == 0) {
                        threadDao.insertThread(downloadedToStoredThread(thread, boardId))
                    }
                    else{
                        threadDao.markThreadDownload(boardId, thread.num)
                    }
                }
            ).processSingle()


    override fun deleteThread(boardId: String, threadNum: Int) =
        Single.fromCallable { threadDao.deleteThread(boardId, threadNum) }
            .processSingle()


    //TODO добавить сохранение 1 поста
    @SuppressLint("CheckResult")
    override fun markThreadFavorite(
        thread: BoardThread,
        boardId: String,
        boardName: String )=
        Single.zip(boardDao.getBoardCount(boardId),
            threadDao.getThreadCount(boardId, thread.num),
            BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                if (boardCount == 0){
                    boardDao.insertBoard(StoredBoard(boardId, "", boardName, false))
                }
                if (threadCount == 0) {
                    threadDao.insertThread(favoriteToStoredThread(thread, boardId))
                }
                else
                    threadDao.markThreadFavorite(boardId, thread.num)
            }
        ).processSingle()


    override fun unmarkThreadFavorite(boardId: String, threadNum: Int) =
        Single.fromCallable { threadDao.unmarkThreadFavorite(boardId, threadNum) }
            .processSingle()

    override fun markThreadHidden(boardId: String, threadNum: Int) =
        threadDao.getThreadOrEmpty(boardId, threadNum)
            .doOnSuccess { thread ->
                if (thread.isNotEmpty())
                    threadDao.markThreadHidden(boardId, threadNum)
                else
                    threadDao.insertThread(
                        StoredThread(
                            threadNum,
                            "",
                            boardId,
                            isHidden = true)
                    )
            }
            .processSingle()

    override fun unmarkThreadHidden(boardId: String, threadNum: Int) =
        Single.fromCallable { threadDao.unmarkThreadHidden(boardId, threadNum) }
            .processSingle()
}