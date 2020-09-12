package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.db.DbContract
import ru.be_more.orange_forum.data.db.db.entities.StoredThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelThread
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.extentions.processCompletable
import ru.be_more.orange_forum.extentions.processSingle
import javax.inject.Inject

class ThreadInteractorImpl @Inject constructor(
    private val apiRepository: RemoteContract.ThreadRepository,
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository,
    private val dbPostRepository: DbContract.PostRepository,
    private val dbFileRepository: DbContract.FileRepository,
    private val fileRepository: LocalContract.FileRepository
): InteractorContract.ThreadInteractor {

    override fun getThread(boardId: String, threadNum: Int): Single<BoardThread> =
        Single.zip(
            dbThreadRepository.getThreadOrEmpty(boardId, threadNum),
            apiRepository.getThread(boardId, threadNum),
            dbPostRepository.getPosts(boardId, threadNum),
            Function3 { localThread, webThread, posts ->
                when{
                    localThread.isEmpty() -> // в базе вообще нет данных о треде
                        return@Function3 webThread
                    localThread[0].isDownloaded -> // тред полностью скачан
                        return@Function3 localThread[0].copy(posts = posts) //TODO переделать, чтобы новые посты доставлялись в старый тред
                    else -> // о треде есть заметки (избранное, скрытое)
                        return@Function3 webThread.copy(
                            isHidden = localThread[0].isHidden,
                            isFavorite = localThread[0].isFavorite
                        )
                }
            }
        )

    override fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Completable =
        Completable.create { emitter ->
            Single.zip(dbBoardRepository.getBoardCount(boardId),
                dbThreadRepository.getThreadOrEmpty(boardId, thread.num),
                BiFunction <Int, List<BoardThread>, Unit> { boardCount, probablyThread ->

                    if(boardCount == 0 )
                        dbBoardRepository.insertBoard(boardId, boardName)

                    if (probablyThread.isEmpty()){//тред еще не сохранен
                        dbThreadRepository.insertThread(thread, boardId)

                        dbPostRepository.savePost(thread.posts[0], thread.num, boardId)

                        //Сохраняем файлы в ФС и сохраняем ссылки на файлы в модель
                        thread.posts[0].files = thread.posts[0].files.map { file ->
                            return@map file.copy(
                                localPath = fileRepository.saveFile(file.path).toString(),
                                localThumbnail = fileRepository.saveFile(file.thumbnail).toString()
                            )
                        }

                        thread.posts[0].files.forEach { file ->
                            dbFileRepository.saveFile(file, thread.num, thread.num, boardId)
                        }
                    }
                    else
                        dbThreadRepository.markThreadFavorite(thread, boardId, boardName)

                }
            )
                .processSingle()
                .subscribe({emitter.onComplete()}, emitter::onError)
        }
            .processCompletable()

    override fun unmarkThreadFavorite(boardId: String, threadNum: Int): Completable =
        dbThreadRepository.unmarkThreadFavorite(boardId, threadNum)
            .processCompletable()

    override fun downloadThread(thread: BoardThread, boardId: String, boardName: String): Completable =
            dbThreadRepository.downloadThread(thread.copy(isDownloaded = true), boardId)
                .doOnComplete {
                    thread.posts.forEach { post ->
                        dbPostRepository.savePost(post, thread.num, boardId)

                        post.files.map { file ->
                            file.copy( //сохраняем файл в ФС, ссылку сохраняем в модель
                                localPath = fileRepository.saveFile(file.path).toString(),
                                localThumbnail = fileRepository.saveFile(file.thumbnail).toString()
                            )
                        }.forEach { file -> //файл с ссылкой на файл в ФС кладем в БД
                            dbFileRepository.saveFile(file, thread.num, thread.num, boardId)
                        }
                    }
                }
            .processCompletable()

    override fun deleteThread(boardId: String, threadNum: Int): Completable =
        dbThreadRepository.deleteThread(boardId, threadNum)
        .processCompletable()

    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?> {
        App.showToast("Не считаю нужным реализовывать")
        return Single.fromCallable { return@fromCallable BoardThread(1 ) }.processSingle()
    }

    override fun markThreadHidden(boardId: String, boardName: String, threadNum: Int): Completable =
        Completable.create { emitter ->
            Single.zip(dbBoardRepository.getBoardCount(boardId),
                dbThreadRepository.getThreadOrEmpty(boardId, threadNum),
                BiFunction <Int, List<BoardThread>, Unit> { boardCount, probablyThread ->

                    if(boardCount == 0 )//борда еще не сохранена
                        dbBoardRepository.insertBoard(boardId, boardName)

                    if (probablyThread.isEmpty()){//тред еще не сохранен
                        dbThreadRepository.insertThread(BoardThread(threadNum, isHidden = true), boardId)
                    }
                    else
                        dbThreadRepository.markThreadHidden(boardId, threadNum)
                }
            )
                .processSingle()
                .subscribe({emitter.onComplete()}, emitter::onError)
        }
            .processCompletable()

    override fun unmarkThreadHidden(boardId: String, threadNum: Int): Completable =
        dbThreadRepository.unmarkThreadFavorite(boardId, threadNum)
            .processCompletable()
}