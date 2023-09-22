package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class FavoriteInteractorImpl(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
): InteractorContract.FavoriteInteractor {

    override fun observe(): Observable<List<Board>> =
        Observable.combineLatest(
            boardRepository.observeList(),
            threadRepository.observeFavorite()
        ) { boards, threads ->
            boards
                .map { board ->
                    board to threads.filter { it.boardId == board.id }
                }
                .map { (board, threads) ->
                    board.copy(threads = threads)
                }
                .filter { it.threads.isNotEmpty() }
        }

/*    fun getDownFavs(): Observable<List<Board>> =
        boardRepository.getBoardsObservable()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isDownloaded || it.isFavorite })
                    }
                    .filter { it.threads.isNotEmpty() }
            }*/

    @Deprecated("Maybe delete")
    override fun getFavoritesOnly(): Single<List<Board>> =
        boardRepository.observeList()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isFavorite })
                    }
                    .filter { it.threads.isNotEmpty() }
            }
            .firstOrError()

    @Deprecated("Maybe delete")
    override fun updateNewMessageCount(boardId: String, threadNum: Int, count: Int) =
        boardRepository.updateThreadNewMessageCounter(boardId, threadNum, count)
}