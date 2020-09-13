package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.disposables
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : DbContract.DownloadRepository{
    override fun getDownloads(): Single<List<Board>> =
        Single.zip(
            dao.getBoards(),
            dao.getDownloadedThreads(),
            dao.getOpPosts(),
            dao.getOpFiles(),
            Function4 { boards, threads, posts, files ->
                val boardResult = LinkedList<Board>()
                boards.forEach{ board ->
                    val thread = threads.filter { it.boardId == board.id }
                    if (thread.isNotEmpty())
                        boardResult.add(toModelBoard(
                            board,
                            toModelThreads(thread)
                        ))
                }

                posts.forEach { post ->
                    boardResult.find { it.id == post.boardId }
                        ?.threads?.find { it.num == post.threadNum }
                        ?.posts = listOf(toModelPost(
                        post,
                        files.filter { it.postNum == post.num && it.boardId == post.boardId})
                    )
                }

                boardResult
            }
        )

    override fun release() {
        disposables.forEach{ it.dispose() }
        disposables.clear()
    }
}