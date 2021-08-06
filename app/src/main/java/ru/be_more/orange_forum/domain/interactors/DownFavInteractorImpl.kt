package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class DownFavInteractorImpl(
    private val boardRepository: DbContract.BoardRepository
): InteractorContract.DownFavInteractor {
    override fun getDownFavs(): Single<List<Board>> =
        boardRepository.getBoards()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isDownloaded || it.isFavorite })
                    }
                    .filter { it.threads.isNotEmpty() }
            }

    override fun getDownFavsObservable(): Observable<List<Board>> =
        boardRepository.getBoardsObservable()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isDownloaded || it.isFavorite })
                    }
                    .filter { it.threads.isNotEmpty() }
            }

    override fun getFavoritesOnly(): Single<List<Board>> =
        boardRepository.getBoards()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isFavorite })
                    }
                    .filter { it.threads.isNotEmpty() }
            }

    override fun updateNewMessageCount(boardId: String, threadNum: Int, count: Int) =
        boardRepository.updateThreadNewMessageCounter(boardId, threadNum, count)
}