package ru.be_more.orange_forum.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import kotlinx.android.synthetic.main.item_post.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.Post


class PostFragment : MvpAppCompatFragment(), PostView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var postPresenter : PostPresenter

    private lateinit var boardId: String
    private var postNum: Int = 0
    private lateinit var postFromFragment: Post
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private var post: MutableLiveData<Post> = MutableLiveData()
    var bus = BusProvider.getInstance()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        postPresenter.init(boardId, postNum, picListener, linkListener)
        postPresenter.init(postFromFragment, picListener, linkListener)

        initObserver()

        bus.register(this)
    }

    private fun initObserver(){
        post.observe(this, Observer {

            tv_item_post_comment.text = post.value?.comment
            tv_item_post_comment.setListener(postPresenter.getLinkListener())

            cl_post_header.visibility = View.GONE

            if (post.value?.subject.isNullOrEmpty())
                tv_item_post_subject.visibility = View.GONE
            else
                tv_item_post_subject.text = post.value?.subject

            val recyclerView = rv_item_post_pics
            recyclerView.layoutManager = LinearLayoutManager(this.context)

            if(post.value?.files != null)
                recyclerView.adapter = PostPicAdapter(post.value?.files!!, postPresenter.getPicListener())
            else
                rv_item_post_pics.visibility = View.GONE

            tv_item_post_replies.text = ""
            var replyResult = ""


            post.value?.replies?.forEach { reply ->

                replyResult = if (replyResult == "")
                    "<a href='$reply'>>>$reply</a>"
                else
                    "$replyResult <a href='$reply'>>>$reply</a>"

                tv_item_post_replies.text = replyResult

                tv_item_post_replies.setListener(postPresenter.getLinkListener())
            }
        })
    }

    override fun setPost(post: Post) {
        this.post.postValue(post)
    }


    @Subscribe
    public fun onBackPressed(event: BackPressed) {
        Log.d("M_PostFragment", "back pressed")
    }

   /* override fun onBackPressed(): Boolean {
        postPresenter.onBackPressed()
        return true
    }*/

    companion object {
        fun getPostFragment (boardId: String,
                             postNum: Int,
                             picListener: PicOnClickListener,
                             linkListener: LinkOnClickListener): PostFragment {
            val post = PostFragment()
            post.boardId = boardId
            post.postNum = postNum
            post.picListener = picListener
            post.linkListener = linkListener

            return post
        }

        fun getPostFragment (post: Post?,
                             picListener: PicOnClickListener,
                             linkListener: LinkOnClickListener): PostFragment? {
            if(post == null)
                return null

            val postFragment = PostFragment()
            postFragment.postFromFragment = post
            postFragment.picListener = picListener
            postFragment.linkListener = linkListener

            return postFragment
        }

    }
}