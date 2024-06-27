package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.database.db.dao.PostDao
import ru.be_more.database.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.dbConverters.PostFactory
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Post
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val storage: StorageContract.LocalStorage,
) : DbContract.PostRepository {

    override suspend fun insert(post: Post) =
        dao.insert(
            PostFactory.toEntity(post)
        )

    override suspend fun insert(posts: List<Post>) =
        dao.insert(
            posts.map { PostFactory.toEntity(it) }
        )

    override suspend fun insertMissing(thread: BoardThread) =
        (dao.getLatestPostId(thread.boardId, thread.num) ?: -1)
            .let { latestPostId ->
                dao.insert(
                    thread
                        .posts
                        .filter { it.id > latestPostId }
                        .map { PostFactory.toEntity(it) }
                )
            }

    override suspend fun save(posts: List<Post>) =
        posts.map { post ->
                post.copy(
                    files = post.files.map { file ->
                        file.copy(
                            localPath = storage.saveFile(file.path).toString(),
                            localThumbnail = storage.saveFile(file.thumbnail).toString()
                        )
                    }
                )
            }
            .let { insert(it) }

    override suspend fun insertOp(posts: List<Post>) =
        dao.insert(
            posts.map { PostFactory.toEntity(it) }
        )

    override fun getOpListFlow(boardId: String): Flow<List<Post>> =
        dao.getOpListFlow(boardId)
            .map { posts ->
                posts.map { PostFactory.fromEntity(it) }
            }

    override fun getListFlow(boardId: String, threadNum: Int): Flow<List<Post>> =
        dao.getListFlow(boardId, threadNum)
            .map { posts ->
                posts.map { PostFactory.fromEntity(it) }
            }

    override suspend fun get(boardId: String, post: Int): Post? =
        dao.get(boardId, post)
            ?.let { PostFactory.fromEntity(it) }

    override suspend fun getThreadPosts(boardId: String, threadNum: Int): List<Post> =
        dao.getThreadPosts(boardId, threadNum)
            .map { PostFactory.fromEntity(it) }

    override suspend fun delete(boardId: String, threadNum: Int) =
        dao.getThreadPosts(boardId, threadNum)
            .let { posts ->
                posts
                    .asSequence()
                    .map { it.files }
                    .flatten()
                    .map { listOf(it.localPath, it.localThumbnail) }
                    .flatten()
                    .filterNotNull()
                    .toList()
                    .forEach { storage.delete(it) }
            }
            .also { dao.delete(boardId, threadNum) }
}