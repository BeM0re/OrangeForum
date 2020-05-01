package ru.be_more.orange_forum.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postPresenter.init(boardId, postNum)
    }


    override fun setPost(post: Post) {
        tv_item_post_comment.text = post.comment


    }


    companion object {
        fun getThreadFragment ( boardId: String, postNum: Int): PostFragment {
            val post = PostFragment()
            post.boardId = boardId
            post.postNum = postNum

            return post
        }
    }
}