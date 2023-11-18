package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import java.util.concurrent.TimeUnit

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val postRepository: DbContract.PostRepository,
): InteractorContract.BoardInteractor {

    private val searchQuery = BehaviorSubject
        .createDefault("")

    override fun observe(boardId: String): Observable<Board> =
        refresh(boardId)
            .andThen(observeBoard(boardId))

    override fun getSingle(boardId: String): Single<Board> =
        boardRepository.get(boardId)

    override fun markFavorite(boardId: String): Completable =
        boardRepository
            .get(boardId)
            .flatMapCompletable {
                boardRepository.markFavorite(boardId, !it.isFavorite)
            }

    override fun refresh(boardId: String): Completable =
        threadRepository
            .deleteKeepingState(boardId)
            .andThen(downloadBoard(boardId))

    private fun downloadBoard(boardId: String): Completable =
        apiRepository.getBoard(boardId)
            .flatMapCompletable { board ->
                boardRepository
                    .insertKeepingState(board)
                    .andThen(
                        threadRepository.insertKeepingState(board.threads)
                    )
                    .andThen(
                        postRepository.insert(
                            board.threads.mapNotNull {
                                it.posts.getOrNull(0)
                            }
                        )
                    )
                    .andThen(
                        threadRepository.deleteExceptGiven(
                            boardId = board.id,
                            liveThreadNumList = board.threads.map { it.num }
                        )
                    )
            }

    private fun observeBoard(boardId: String): Observable<Board> =
        Observable.combineLatest(
            boardRepository.observe(boardId),
            threadRepository.observeList(boardId),
            postRepository.observeOp(boardId),
            searchQuery
        ) { board, threads, posts, searchQuery ->
            board.copy(
                threads = threads
                    .map { thread ->
                        thread.copy(
                            posts = posts.filter { post ->
                                post.threadNum == thread.num
                                    && (post.subject.contains(searchQuery) || post.comment.contains(searchQuery))
                            }
                        )
                    }
                    .filter { it.posts.isNotEmpty() }
                    .sortedWith(
                        compareByDescending<BoardThread> { it.isPinned }
                            .thenByDescending { it.lasthit }
                    )
            )
        }

    override fun search(query: String) =
        searchQuery.onNext(query)
}
