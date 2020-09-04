package ru.be_more.orange_forum.data.local.repositories

import android.annotation.SuppressLint
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.downloadedToStoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.favoriteToStoredThread
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.model.BoardThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepositoryImpl @Inject constructor(
    private val dao: DvachDao
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
            Single.zip(dao.getBoardCount(boardId),
                dao.getThreadCount(boardId, thread.num),
                BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                    if (boardCount == 0){
                        dao.insertBoard(StoredBoard(boardId, "", boardId, false))
                    }
                    if (threadCount == 0) {
                        dao.insertThread(downloadedToStoredThread(thread, boardId))
                    }
                    else{
                        dao.markThreadDownload(boardId, thread.num)
                    }
                }
            ).processSingle()


    override fun deleteThread(boardId: String, threadNum: Int) =
        Single.fromCallable { dao.deleteThread(boardId, threadNum) }
            .processSingle()


    //TODO добавить сохранение 1 поста
    @SuppressLint("CheckResult")
    override fun markThreadFavorite(
        thread: BoardThread,
        boardId: String,
        boardName: String )=
        Single.zip(dao.getBoardCount(boardId),
            dao.getThreadCount(boardId, thread.num),
            BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                if (boardCount == 0){
                    dao.insertBoard(StoredBoard(boardId, "", boardName, false))
                }
                if (threadCount == 0) {
                    dao.insertThread(favoriteToStoredThread(thread, boardId))
                }
                else
                    dao.markThreadFavorite(boardId, thread.num)
            }
        ).processSingle()


    override fun unmarkThreadFavorite(boardId: String, threadNum: Int) =
        Single.fromCallable { dao.unmarkThreadFavorite(boardId, threadNum) }
            .processSingle()

    override fun markThreadHidden(boardId: String, threadNum: Int) =
        dao.getThreadOrEmpty(boardId, threadNum)
            .doOnSuccess { thread ->
                if (thread.isNotEmpty())
                    dao.markThreadHidden(boardId, threadNum)
                else
                    dao.insertThread(
                        StoredThread(
                            threadNum,
                            "",
                            boardId,
                            isHidden = true)
                    )
            }
            .processSingle()

    override fun unmarkThreadHidden(boardId: String, threadNum: Int) =
        Single.fromCallable { dao.unmarkThreadHidden(boardId, threadNum) }
            .processSingle()
}