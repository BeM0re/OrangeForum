package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.model.model.Board
import ru.be_more.model.model.ImageboardType
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.time.Duration

class FavoriteInteractorImpl @Inject constructor(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>
): InteractorContract.FavoriteInteractor {

    override fun getBoardListFlow(): Flow<List<Board>> =
        combine(
            boardRepository.getListFlow(),
            threadRepository.getFavoriteFlow()
        ) { boards, threads ->
            boards
                .map { board ->
                    board to threads.filter { it.boardId == board.id }
                }
                .map { (board, threads) ->
                    board.copy(threads = threads)
                }
                .filter { it.threads.isNotEmpty() || it.isFavorite }
        }

    override fun getBoardFlow(): Flow<Boolean> =
        getBoardListFlow().map { boards ->
            boards.any { board ->
                board.threads.any { thread ->
                    thread.hasNewMessages
                }
            }
        }

    override suspend fun updatingFavoritesSubscription() =
        flow {
            while (true) {
                delay(Duration.parse("1m"))
                emit(Unit)
            }
        }.collect { updateFavoriteThreadInfo() }

    override suspend fun updateFavoriteThreadInfo() =
        threadRepository.getFavorites()
            .forEach { thread ->
                apiRepositoryMap[thread.imageboardType]?.let { repo ->
                    repo.getThreadInfo(thread.boardId, thread.num)
                        .also {
                                info ->
                            if (!info.isAlive)
                                threadRepository.setIsDrown(info.boardId, info.threadNum, isDrown = true)
                            else
                                repo.getEmptyThread(info.boardId, info.threadNum,)
                                    .also { updatedThread ->
                                        if (updatedThread.lasthit > thread.lasthit)
                                            threadRepository.setLasthit(info.boardId, info.threadNum, info.timestamp)
                                                .also {
                                                    threadRepository.setHasNewPost(info.boardId, info.threadNum, hasNewPost = true)
                                                }
                                    }
                        }
                }
            }

}

