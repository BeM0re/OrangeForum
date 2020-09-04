package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelPost
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredFile
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredPost
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : LocalContract.PostRepository{

    override fun savePost(post: Post,
                          threadNum: Int,
                          boardId: String,
                          localPath: String,
                          localThumbnail: String) =
        Single.fromCallable {
            dao.insertPost(toStoredPost(post, threadNum, boardId))
            post.files.forEach { file -> //TODO вынести в юзкейс
                dao.insertFile(toStoredFile(file, post.num, boardId, threadNum, localPath, localThumbnail))
            }
        }
            .processSingle()


    override fun getPost(boardId: String, postNum: Int): Single<Post> =
        Single.zip(
            dao.getPost(boardId, postNum),
            dao.getFiles(boardId, postNum),
            BiFunction <StoredPost, List<StoredFile>, Post> {post, files ->
                toModelPost(post, files)
            })
            .processSingle()


    override fun getPosts(boardId: String, threadNum: Int): Single<List<Post>> =
        Single.zip(
            dao.getPosts(boardId, threadNum),
            dao.getFiles(boardId, threadNum),
            BiFunction <List<StoredPost>, List<StoredFile>, List<Post>> {posts, files ->
                posts.map { post -> toModelPost(post, files.filter { it.postNum == post.num }) }
            })
            .processSingle()
}