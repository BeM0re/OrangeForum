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
    private var postNum: Int = 0
    private var disposables: LinkedList<Disposable?> = LinkedList()
    private lateinit var adapter: PostPicAdapter
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener


    fun init(post: Post,
             picListener: PicOnClickListener,
             linkListener: LinkOnClickListener) {
        App.getComponent().inject(this)

//        this.boardId = boardId
//        this.postNum = postNum
        this.post = post
        this.picListener = picListener
        this.linkListener = linkListener

        viewState.setPost(post)

/*        try {
            disposables.add(
                repo.getPost(boardId, postNum)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {throwable -> Log.d("M_ThreadPresenter", "error = $throwable") }
                    .subscribe(
                        {viewState.setPost(it)
                        Log.d("M_PostPresenter", "post = $it")
                        }, //TODO брать из фрагмента пост, не передавать напрямую
                        { Log.d("M_ThreadPresenter", "error = $it")}
                    )
            )
        }
        catch (e: NumberFormatException){
            Log.d("M_ThreadPresenter", "$e")
        }*/
    }

    fun getAdapter(): PostPicAdapter = this.adapter

    fun getPicListener(): PicOnClickListener = this.picListener

    fun getLinkListener(): LinkOnClickListener = this.linkListener
}