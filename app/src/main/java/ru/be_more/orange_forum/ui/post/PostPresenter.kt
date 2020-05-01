package ru.be_more.orange_forum.ui.post

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
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
    private lateinit var listener: PicOnClickListener


    fun init(boardId: String, postNum: Int, listener: PicOnClickListener) {
        App.getComponent().inject(this)

        this.boardId = boardId
        this.postNum = postNum
        this.listener = listener

        try {
            disposables.add(
                repo.getPost(boardId, postNum)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {throwable -> Log.d("M_ThreadPresenter", "error = $throwable") }
                    .subscribe(
                        {viewState.setPost(it)}, //TODO брать из фрагмента пост, не передавать напрямую
                        { Log.d("M_ThreadPresenter", "error = $it")}
                    )
            )
        }
        catch (e: NumberFormatException){
            Log.d("M_ThreadPresenter", "$e")
        }
    }

    fun getAdapter(): PostPicAdapter = this.adapter

    fun getListener(): PicOnClickListener = this.listener
}