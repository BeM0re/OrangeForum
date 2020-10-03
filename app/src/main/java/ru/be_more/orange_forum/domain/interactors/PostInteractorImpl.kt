package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.processSingle

//import javax.inject.Inject


class PostInteractorImpl /*@Inject constructor*/(
    private val dbPostRepository: DbContract.PostRepository,
    private val dbFileRepository: DbContract.FileRepository
): InteractorContract.PostInteractor, BaseInteractorImpl() {

    override fun getPosts(boardId: String, threadNum: Int): Single<List<Post>> =
        Single.zip(dbPostRepository.getPosts(boardId, threadNum),
            dbFileRepository.getThreadFiles(boardId, threadNum),
            BiFunction <List<Post>, List<Pair<AttachFile, Int>>, List<Post>> { posts, files ->
                posts.map { post ->
                    post.copy( files = files.filter { it.second == post.num }
                        .map { it.first }
                    )
                }
            }
        )
            .processSingle()


    override fun getPost(boardId: String, postNum: Int): Single<Post> =
        Single.zip(dbPostRepository.getPost(boardId, postNum),
            dbFileRepository.getPostFiles(boardId, postNum),
            BiFunction <Post, List<AttachFile>, Post> {post, files ->
                post.copy(files = files)
            }
        )
            .processSingle()



}