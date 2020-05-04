package ru.be_more.orange_forum.ui.post

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachApiRepository
import java.util.*
import javax.inject.Inject

@InjectViewState
class PostPresenter : MvpPresenter<PostView>() {

    @Inject
    lateinit var repo: DvachApiRepository
    private lateinit var boardId: String
    private lateinit var post: Post
    private lateinit var adapter: PostPicAdapter
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private val replyStack = Stack<Post>()


    fun init(post: Post,
             picListener: PicOnClickListener,
             linkListener: LinkOnClickListener) {
        App.getComponent().inject(this)

        this.post = post
        this.picListener = picListener
        this.linkListener = linkListener

        viewState.setPost(post)
    }

    fun getAdapter(): PostPicAdapter = this.adapter

    fun getPicListener(): PicOnClickListener = this.picListener

    fun getLinkListener(): LinkOnClickListener = this.linkListener

    fun putPostInStack(post: Post){
        this.replyStack.push(post)
    }

    fun getPostFromStack(): Post?{
        return if(this.replyStack.empty())
            null
        else
            replyStack.peek()
    }

    fun onBackPressed() {
        this.replyStack.pop()
        viewState.setPost(replyStack.peek())
    }
}