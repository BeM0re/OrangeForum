package ru.be_more.orange_forum.data.db.repositories

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.be_more.orange_forum.data.db.DbContract
import ru.be_more.orange_forum.data.db.db.dao.DvachDao
import ru.be_more.orange_forum.data.db.db.entities.StoredBoard
import ru.be_more.orange_forum.data.db.db.entities.StoredFile
import ru.be_more.orange_forum.data.db.db.entities.StoredPost
import ru.be_more.orange_forum.data.db.db.entities.StoredThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelFile
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelPost
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : DbContract.FavoriteRepository{

    override fun getFavorites(): Single<List<Board>> =
        Single.zip(
            dao.getBoards(),
            dao.getFavoriteThreads(),
            dao.getOpPosts(),
            dao.getOpFiles(),
            Function4 <List<StoredBoard>, List<StoredThread>, List<StoredPost>, List<StoredFile>, List<Board>>
            { boards, threads, posts, files ->
                boards.map { board ->
                    toModelBoard(board, toModelThreads(threads)
                        .map { thread -> thread.copy(posts = posts
                            .map{ post -> toModelPost(post, files
                                .filter { it.postNum == post.num }
                            )}
                        )}
                    )
                }
            }
        )
            .processSingle()

}