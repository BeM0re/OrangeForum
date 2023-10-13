package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.BoardThread
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

    override fun insertMissing(thread: BoardThread): Completable =
        dao.getMaxNumber(thread.boardId, thread.num)
            .defaultIfEmpty(-1)
            .flatMapCompletable { maxNumber ->
                dao.insert(
                    thread
                        .posts
                        .filter { it.number > maxNumber }
                        .map { StoredPost(it) }
                )
            }

    override fun save(posts: List<Post>): Completable =
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
            .flatMapCompletable { insert(it) }

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

    override fun get(boardId: String, post: Int): Maybe<Post> =
        dao.get(boardId, post)
            .map { it.toModel() }

    override fun getThreadPosts(boardId: String, threadNum: Int): Single<List<Post>> =
        dao.getThreadPosts(boardId, threadNum)
            .map { posts ->
                posts.map { it.toModel() }
            }

    override fun delete(boardId: String, threadNum: Int): Completable =
        dao.getThreadPosts(boardId, threadNum)
            .flatMapCompletable { posts ->
                Completable.fromCallable {
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
            }
            .andThen(
                dao.delete(boardId, threadNum)
            )
}