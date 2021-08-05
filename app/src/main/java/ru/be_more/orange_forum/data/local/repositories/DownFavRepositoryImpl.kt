package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.domain.model.Board

class DownFavRepositoryImpl (
    private val dao: DvachDao
) : DbContract.DownFavRepository{

    override fun getDownloadsAndFavorites(): Single<List<Board>> =
        Single.zip(
            dao.getBoards(),
            dao.getDownloadedAndFavoritesThreads(),
            dao.getOpPosts(),
            dao.getOpFiles(),
            { boards, threads, posts, files ->
                boards.map { board ->
                    toModelBoard(board, toModelThreads(threads.filter { it.boardId == board.id })
                        .map { thread -> thread.copy(posts = posts
                            .filter { it.threadNum == thread.num && it.boardId == board.id}
                            .map{ post -> toModelPost(post, files
                                .filter { it.postNum == post.num && it.boardId == board.id }
                            )}
                        )}
                    )
                }
                    .filter { it.threads.isNotEmpty() || it.isFavorite }
            }
        )

    override fun getFavoritesOnly(): Single<List<Board>> {
        return Single.zip(
            dao.getBoards(),
            dao.getFavoriteThreads(),
            dao.getOpPosts(),
            dao.getOpFiles(),
            { boards, threads, posts, files ->
                boards.map { board ->
                    toModelBoard(board, toModelThreads(threads.filter { it.boardId == board.id })
                        .map { thread -> thread.copy(posts = posts
                            .filter { it.threadNum == thread.num && it.boardId == board.id}
                            .map{ post -> toModelPost(post, files
                                .filter { it.postNum == post.num && it.boardId == board.id }
                            )}
                        )}
                    )
                }
                    .filter { it.threads.isNotEmpty() || it.isFavorite }
            }
        )
    }
}