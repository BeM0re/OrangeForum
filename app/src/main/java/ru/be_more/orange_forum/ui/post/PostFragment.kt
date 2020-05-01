package ru.be_more.orange_forum.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_post.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Post

class PostFragment : MvpAppCompatFragment(), PostView{

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var postPresenter : PostPresenter

    private lateinit var boardId: String
    private var postNum: Int = 0
    private lateinit var listener: PicOnClickListener
    private var post: MutableLiveData<Post> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postPresenter.init(boardId, postNum, listener)

        initObserver()
    }

    private fun initObserver(){
        post.observe(this, Observer {

            tv_item_post_comment.text = post.value?.comment
            cl_post_header.visibility = View.GONE

            if (post.value?.subject.isNullOrEmpty())
                tv_item_post_subject.visibility = View.GONE
            else
                tv_item_post_subject.text = post.value?.subject

            val recyclerView = rv_item_post_pics
            recyclerView.layoutManager = LinearLayoutManager(this.context)

            if(post.value?.files != null)
                recyclerView.adapter = PostPicAdapter(post.value?.files!!, postPresenter.getListener())
            else
                rv_item_post_pics.visibility = View.GONE
        })
    }

    override fun setPost(post: Post) {
        this.post.postValue(post)
    }


    companion object {
        fun getThreadFragment ( boardId: String, postNum: Int, listener: PicOnClickListener): PostFragment {
            val post = PostFragment()
            post.boardId = boardId
            post.postNum = postNum
            post.listener = listener

            return post
        }
    }
}