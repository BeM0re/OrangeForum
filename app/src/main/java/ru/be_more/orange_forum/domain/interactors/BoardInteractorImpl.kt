package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import javax.inject.Inject

class BoardInteractorImpl @Inject constructor(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val postRepository: DbContract.PostRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>,
): InteractorContract.BoardInteractor {

    private val searchQuery = MutableStateFlow("")

    override fun getBoardFlow(boardId: String): Flow<Board> =
        combine(
            boardRepository.getFlow(boardId),
            threadRepository.getListFlow(boardId),
            postRepository.getOpListFlow(boardId),
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

    override suspend fun getBoard(boardId: String): Board? =
        boardRepository.get(boardId)

    override suspend fun markFavorite(boardId: String) =
        boardRepository
            .get(boardId)
            .let {
                boardRepository.markFavorite(boardId, it?.isFavorite == true)
            }

    override suspend fun refresh(imageboardType: ImageboardType, boardId: String) =
        threadRepository
            .deleteKeepingState(boardId)
            .also { downloadBoard(imageboardType, boardId) }

    override suspend fun search(query: String) =
        searchQuery.emit(query)

    private suspend fun downloadBoard(imageboardType: ImageboardType, boardId: String) =
        withContext(Dispatchers.IO) {
            val boardName = async { boardRepository.get(boardId)?.name ?: "" }
            (apiRepositoryMap[imageboardType] ?: throw IllegalStateException("No proper repository found"))
                .getBoard(boardName = boardName.await(), boardId = boardId)
                .let { board ->
                    boardRepository
                        .insertKeepingState(board)
                        .also { threadRepository.insertKeepingState(board.threads) }
                        .also {
                            postRepository.insert(
                                board.threads.mapNotNull {
                                    it.posts.getOrNull(0)
                                }
                            )
                        }
                        .also {
                            threadRepository.deleteExceptGiven(
                                boardId = board.id,
                                liveThreadNumList = board.threads.map { it.num }
                            )
                        }
                }
        }
}
