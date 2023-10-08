package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.Post

class PostRepositoryImpl(
    private val dao: PostDao,
    private val storage: StorageContract.LocalStorage,
) : DbContract.PostRepository {

    override fun insert(post: Post): Completable =
        dao.insert(StoredPost(post))

    override fun insert(posts: List<Post>): Completable =
        dao.insert(
            posts.map { StoredPost(it) }
        )

    override fun save(posts: List<Post>): Single<List<Post>> =
        Observable
            .fromIterable(posts)
            .map { post ->
                post.copy(
                    files = post.files.map { file ->
                        file.copy(
                            localPath = storage.saveFile(file.path).toString(),
                            localThumbnail = storage.saveFile(file.thumbnail).toString()
                        )
                    }
                )
            }
            .toList()

    override fun insertOp(posts: List<Post>): Completable =
        dao.insert(
            posts.map { StoredPost(it) }
        )

    override fun observeOp(boardId: String): Observable<List<Post>> =
        dao.observeOp(boardId)
            .map { posts ->
                posts.map { it.toModel() }
            }

    override fun observe(boardId: String, threadNum: Int): Observable<List<Post>> =
        dao.observe(boardId, threadNum)
            .map { posts ->
                posts.map { it.toModel() }
            }

    override fun get(boardId: String, threadNum: Int): Single<List<Post>> =
        dao.get(boardId, threadNum)
            .map { posts ->
                posts.map { it.toModel() }
            }

    override fun delete(boardId: String, threadNum: Int): Completable =
        dao.delete(boardId, threadNum)

    /** delete posts for threads that no longer exists */
    override fun delete(boardId: String): Completable =
        dao.delete(boardId)
}