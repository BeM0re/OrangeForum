package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredPost
import ru.be_more.orange_forum.domain.model.Post

class PostRepositoryImpl(
    private val dao: DvachDao
) : DbContract.PostRepository{

    override fun savePost(post: Post, threadNum: Int, boardId: String) {
        dao.insertPost(toStoredPost(post, threadNum, boardId))
    }

    override fun savePosts(posts: List<Post>, threadNum: Int, boardId: String) {
        dao.insertPosts(
            posts.map { post -> toStoredPost(post, threadNum, boardId) }
        )
    }

    override fun getPost(boardId: String, postNum: Int): Single<Post> =
        Single.zip(
            dao.getPost(boardId, postNum),
            dao.getPostFiles(postNum, boardId),
            {post, files ->
                toModelPost(post, files)
            })

    override fun getPosts(boardId: String, threadNum: Int): Single<List<Post>> =
        Single.zip(
            dao.getPosts(boardId, threadNum),
            dao.getThreadFiles(boardId, threadNum),
            {posts, files ->
                posts.map { post -> toModelPost(post, files.filter { it.postNum == post.num }) }
            })
}