package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val postRepository: DbContract.PostRepository,
): InteractorContract.BoardInteractor {

    override fun get(boardId: String): Observable<Board> =
        downloadBoard(boardId)
            .andThen(observeBoard(boardId))

    override fun markFavorite(boardId: String): Completable =
        boardRepository
            .get(boardId)
            .flatMapCompletable {
                boardRepository.markFavorite(boardId, !it.isFavorite)
            }

    override fun refresh(boardId: String): Completable =
        threadRepository.delete(boardId)
            .andThen(downloadBoard(boardId))

    private fun downloadBoard(boardId: String): Completable =
        apiRepository.getBoard(boardId)
            .flatMapCompletable { board ->
                boardRepository.insert(board)
                    .andThen(
                        threadRepository.insert(board.threads)
                    )
                    .andThen(
                        postRepository.insert(
                            board.threads.mapNotNull {
                                it.posts.getOrNull(0)
                            }
                        )
                    )
            }

    private fun observeBoard(boardId: String): Observable<Board> =
        Observable.combineLatest(
            boardRepository.observe(boardId),
            threadRepository.observeList(boardId),
            postRepository.observeOp(boardId)
        ) { board, threads, posts ->
            board.copy(
                threads = threads.map { thread ->
                    thread.copy(
                        posts = posts.filter { it.threadNum == thread.num }
                    )
                }
            )
        }
}
