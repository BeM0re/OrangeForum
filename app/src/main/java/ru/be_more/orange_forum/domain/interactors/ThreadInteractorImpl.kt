package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.consts.ThreadUpdateInterval
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.time.Duration

class ThreadInteractorImpl @Inject constructor(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val postRepository: DbContract.PostRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>
): InteractorContract.ThreadInteractor {

    override fun getThreadFlow(
        boardId: String,
        threadNum: Int,
    ): Flow<BoardThread> =
        combine(
            threadRepository.getFlow(boardId, threadNum),
            postRepository.getListFlow(boardId, threadNum)
        ) { thread, posts ->
            thread.copy(posts = posts)
        }

    override suspend fun save(imageboardType: ImageboardType, boardId: String, threadNum: Int) =
        apiRepositoryMap[imageboardType]?.getThread(boardId, threadNum)
            ?.let { thread ->
                threadRepository.insertKeepingState(
                    listOf(thread.copy(isDownloaded = true))
                )
                postRepository.save(thread.posts)
            } ?: throw IllegalStateException("No proper repository found")

    override suspend fun markFavorite(boardId: String, threadNum: Int) =
        threadRepository
            .get(boardId, threadNum)
            .let {
                threadRepository.markFavorite(boardId, threadNum, it?.isFavorite == false)
            }

    override suspend fun markQueued(boardId: String, threadNum: Int) =
        threadRepository
            .get(boardId, threadNum)
            .let {
                threadRepository.markQueued(boardId, threadNum, it?.isQueued == false)
            }

    override suspend fun markHidden(boardId: String, threadNum: Int) =
        threadRepository
            .get(boardId, threadNum)
            .let {
                threadRepository.markHidden(boardId, threadNum, it?.isHidden == false)
            }

    override suspend fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int) =
        threadRepository.updateLastPostViewed(boardId, threadNum, postNum)

    //todo для докачивания постов есть отдельный метод апи
    override suspend fun subToUpdate(boardId: String, threadNum: Int) =
        flow {
            while (true) {
                emit(Unit)
                delay(Duration.parse(ThreadUpdateInterval))
            }
        }
            .map { threadRepository.getFavorites() }
            .collect { threadList ->
                threadList.forEach { thread ->
                    getThread(thread.imageboard, boardId, threadNum, savePics = false)
                }
            }

    override suspend fun delete(boardId: String, threadNum: Int) {
        threadRepository.delete(boardId, threadNum)
        postRepository.delete(boardId, threadNum)
    }

    override suspend fun refresh(boardId: String, threadNum: Int) =
        threadRepository.get(boardId, threadNum)
            .let { apiRepositoryMap[it?.imageboard?.type] }
            ?.getThread(boardId, threadNum)
            ?.let { thread ->
                threadRepository.insertKeepingState(listOf(thread))
                postRepository.insertMissing(thread)
            } ?: throw IllegalStateException("No proper repository found")

    @Deprecated("")
    private suspend fun getThread(
        imageboard: Imageboard,
        boardId: String,
        threadNum: Int,
        savePics: Boolean,
    ) =
        apiRepositoryMap[imageboard.type]
            ?.getThread(boardId, threadNum)
            ?.let { thread ->
                //todo save states
                if (savePics) {
                    threadRepository.insert(thread.copy(isDownloaded = true))
                    postRepository.save(thread.posts)
                } else {
                    threadRepository.insert(thread)
                    postRepository.insert(thread.posts)
                }
            }
}