package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.local.storage.StorageContract
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.processCompletable
import ru.be_more.orange_forum.extentions.processSingle

class ThreadInteractorImpl (
    private val apiRepository: RemoteContract.ThreadRepository,
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository,
    private val dbPostRepository: DbContract.PostRepository,
    private val dbFileRepository: DbContract.FileRepository,
    private val fileRepository: StorageContract.FileRepository
): InteractorContract.ThreadInteractor, BaseInteractorImpl() {

    override fun getThread(boardId: String, threadNum: Int): Single<BoardThread> =
        Single.zip(
            dbThreadRepository.getThreadOrEmpty(boardId, threadNum),
            apiRepository.getThread(boardId, threadNum),
            dbPostRepository.getPosts(boardId, threadNum),
            Function3 <List<BoardThread>, BoardThread, List<Post>, BoardThread>
            { localThreads, webThread, posts ->
                when{
                    localThreads.isEmpty() -> // в базе вообще нет данных о треде
                        return@Function3 webThread
                    localThreads[0].isDownloaded -> // тред полностью скачан
                        return@Function3 localThreads[0].copy(posts = posts) //TODO переделать, чтобы новые посты доставлялись в старый тред
                    else -> // о треде есть заметки (избранное, скрытое)
                        return@Function3 webThread.copy(
                            isHidden = localThreads[0].isHidden,
                            isFavorite = localThreads[0].isFavorite
                        )
                }
            }
        )
            .processSingle()

    override fun markThreadFavorite(threadNum: Int, boardId: String, boardName: String): Completable =
        Completable.fromSingle(
            apiRepository.getThread(boardId, threadNum)
                .doOnSuccess { thread ->
                    dbPostRepository.savePost(thread.posts[0], threadNum, boardId)
                }
                .doOnSuccess { thread ->
                    dbFileRepository.saveFiles(thread.posts[0].files, thread.num, thread.num, boardId)
                }
                .flatMap { thread ->
                    dbThreadRepository.insertThreadSafety(thread.copy(isFavorite = true), boardId)
                        .doOnSuccess { isSaved ->
                            if (!isSaved) dbThreadRepository.markThreadFavorite(boardId, threadNum) }
                }
                .flatMap {
                    dbBoardRepository.getBoardCount(boardId)
                        .doOnSuccess {
                            if (it == 0)
                                dbBoardRepository.insertBoard(boardId, boardName, false)
                        }
                }
        )
            .processCompletable()

   /* override fun markThreadFavorite(threadNum: Int, boardId: String): Completable =
        Completable.fromSingle(
            apiRepository.getThread(boardId, threadNum)
                .doOnSuccess { thread ->
                    dbPostRepository.savePost(thread.posts[0], threadNum, boardId)
                }
                .doOnSuccess { thread ->
                    dbFileRepository.saveFiles(thread.posts[0].files, thread.num, thread.num, boardId)
                }
                .flatMap { thread ->
                    dbThreadRepository.insertThreadSafety(thread.copy(isFavorite = true), boardId)
                        .doOnSuccess { isSaved ->
                            if (!isSaved) dbThreadRepository.markThreadFavorite(boardId, threadNum) }
                }
        )
            .processCompletable()*/

    override fun unmarkThreadFavorite(boardId: String, threadNum: Int): Completable =
        Completable.fromCallable {
            dbThreadRepository.unmarkThreadFavorite(boardId, threadNum)
        }
            .processCompletable()

    override fun downloadThread(threadNum: Int, boardId: String, boardName: String): Completable =
        Completable.fromSingle(
            apiRepository.getThread(boardId, threadNum)
                .doOnSuccess { thread ->
                    dbPostRepository.savePosts(thread.posts, threadNum, boardId)
                }
                .doOnSuccess { thread ->
                    thread.posts.forEach { post ->
                        dbFileRepository.saveFiles(post.files, post.num, thread.num, boardId)
                    }
                }
                .flatMap { thread ->
                    dbThreadRepository.insertThreadSafety(thread.copy(isDownloaded = true), boardId)
                        .doOnSuccess { isSaved ->
                            if (!isSaved) dbThreadRepository.markThreadFavorite(boardId, threadNum) }
                }
                .flatMap {
                    dbBoardRepository.getBoardCount(boardId)
                        .doOnSuccess {
                            if (it == 0)
                                dbBoardRepository.insertBoard(boardId, boardName, false)
                        }
                }
        )
            .processCompletable()

/*    override fun downloadThread(threadNum: Int, boardId: String): Completable =
        Completable.fromSingle(
            apiRepository.getThread(boardId, threadNum)
                .doOnSuccess { thread ->
                    dbPostRepository.savePosts(thread.posts, threadNum, boardId)
                }
                .doOnSuccess { thread ->
                    thread.posts.forEach { post ->
                        dbFileRepository.saveFiles(post.files, post.num, thread.num, boardId)
                    }
                }
                .flatMap { thread ->
                    dbThreadRepository.insertThreadSafety(thread.copy(isDownloaded = true), boardId)
                        .doOnSuccess { isSaved ->
                            if (!isSaved) dbThreadRepository.markThreadFavorite(boardId, threadNum) }
                }
        )
            .processCompletable()*/

    override fun deleteThread(boardId: String, threadNum: Int) =
        Completable.fromCallable {
            dbThreadRepository.deleteThread(boardId, threadNum)
        }
            .processCompletable()

    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?> {
        App.showToast("Не считаю нужным реализовывать")
        return Single.fromCallable { return@fromCallable BoardThread(1 ) }.processSingle()
    }

    override fun markThreadHidden(boardId: String, boardName: String, threadNum: Int): Completable =
        Completable.fromSingle (
            Single.zip(dbBoardRepository.getBoardCount(boardId),
                dbThreadRepository.getThreadOrEmpty(boardId, threadNum),
                apiRepository.getThread(boardId, threadNum),
                Function3 <Int, List<BoardThread>, BoardThread, Unit> {
                        boardCount, probablyThread, webThread ->

                    if(boardCount == 0 )//борда еще не сохранена
                        dbBoardRepository.insertBoard(boardId, boardName, false)

                    if (probablyThread.isEmpty()){//тред еще не сохранен
                        dbThreadRepository.insertThread(webThread.copy(isHidden = true), boardId)
                    }
                    else
                        dbThreadRepository.markThreadHidden(boardId, threadNum)

                }
            )
        )
            .processCompletable()

    override fun unmarkThreadHidden(boardId: String, threadNum: Int) =
        Completable.fromCallable {
            dbThreadRepository.unmarkThreadFavorite(boardId, threadNum)
        }
            .processCompletable()
}