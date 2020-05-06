package ru.be_more.orange_forum.ui.post

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.android.synthetic.main.item_post.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.ModalContent
import ru.be_more.orange_forum.model.Post

class PostFragment : MvpAppCompatFragment(), PostView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var postPresenter : PostPresenter

    private var timestamp: Long = 0

    private lateinit var content: ModalContent

    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private lateinit var closeModalListener: CloseModalListener

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        postPresenter.init(boardId, postNum, picListener, linkListener)
        postPresenter.init(content, picListener, linkListener)

        setOnBackgroundViewClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun showPost(post: Post){

        v_post1_pic_full_background.visibility = View.VISIBLE

        ll_post_layout.setBackgroundColor(resources.getColor(R.color.color_background))

        tv_item_post_comment.text = post.comment
        tv_item_post_comment.setListener(postPresenter.getLinkListener())

        cl_post_header.visibility = View.GONE

        if (post.subject.isNullOrEmpty())
            tv_item_post_subject.visibility = View.GONE
        else
            tv_item_post_subject.text = post.subject

        val recyclerView = rv_item_post_pics
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.adapter = PostPicAdapter(post.files, postPresenter.getPicListener())

        tv_item_post_replies.text = ""
        var replyResult = ""

        post.replies.forEach { reply ->

            replyResult = if (replyResult == "")
                "<a href='$reply'>>>$reply</a>"
            else
                "$replyResult <a href='$reply'>>>$reply</a>"

            tv_item_post_replies.text = replyResult

            tv_item_post_replies.setListener(postPresenter.getLinkListener())
        }
    }

    private fun showAttachment(pic: Attachment) {
        v_post1_pic_full_background.visibility = View.VISIBLE
        pb_post1_pic_loading.visibility = View.VISIBLE

        if(pic.duration == "") {
            val fullPicGlideUrl = GlideUrl(
                pic.url,
                LazyHeaders.Builder()
                    .addHeader(
                        "Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                                "_ga=GA1.2.57010468.1498700728; " +
                                "ageallow=1; " +
                                "_gid=GA1.2.1910512907.1585793763; " +
                                "_gat=1"
                    )
                    .build()
            )
            iv_post1_pic_full.resetZoom()
            iv_post1_pic_full.visibility = View.VISIBLE
            Glide.with(this)
                .load(fullPicGlideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("M_BoardFragment", "$e")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pb_post1_pic_loading.visibility = View.GONE
                        return false
                    }
                })
                .into(iv_post1_pic_full)

            iv_post1_pic_full.setOnClickListener {
                v_post1_pic_full_background.visibility = View.GONE
                iv_post1_pic_full.visibility = View.GONE
            }
        }
        else{
            pb_post1_pic_loading.visibility = View.VISIBLE

            //TODO потом поменять на нормальные куки
            val headers = mapOf("cookie" to
                    "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                    "_ga=GA1.2.57010468.1498700728; " +
                    "ageallow=1; " +
                    "_gid=GA1.2.1910512907.1585793763; " +
                    "_gat=1")

            vv_post1_video.setOnPreparedListener { pb_post1_pic_loading.visibility = View.GONE }

            vv_post1_video.setVideoURI(Uri.parse(pic.url), headers)
            vv_post1_video.visibility = View.VISIBLE
            vv_post1_video.setMediaController(MediaController(this.context))
            vv_post1_video.requestFocus(0)
            vv_post1_video.start()

            vv_post1_video.setOnClickListener {
                if(System.currentTimeMillis() - timestamp < 2000) {
                    pb_post_pic_loading.visibility = View.GONE
                    vv_post_video.visibility = View.GONE
                    v_post_pic_full_background.visibility = View.GONE
                }
                else
                    timestamp = System.currentTimeMillis()
            }
        }
    }

    override fun setContent(content: ModalContent) {
        when (content){
            is Post -> showPost(content)
            is Attachment -> showAttachment(content)
        }
    }

    private fun setOnBackgroundViewClickListener(){
        v_post1_pic_full_background.setOnClickListener {
            hideModal()
        }
    }

    private fun hideModal(){
        v_post1_pic_full_background.visibility = View.GONE
        iv_post1_pic_full.visibility = View.GONE
        Glide.with(this).clear(iv_post1_pic_full)
        pb_post1_pic_loading.visibility = View.GONE
        vv_post1_video.visibility = View.GONE
        tv_item_post_comment.text = ""
        tv_item_post_subject.text = ""
        tv_item_post_replies.text = ""

        closeModalListener.onCloseModalListener()

    }


    companion object {

        fun getPostFragment (content: ModalContent,
                             picListener: PicOnClickListener,
                             linkListener: LinkOnClickListener,
                             closeModalListener: CloseModalListener): PostFragment {

            val postFragment = PostFragment()
            postFragment.content = content
            postFragment.picListener = picListener
            postFragment.linkListener = linkListener
            postFragment.closeModalListener = closeModalListener

            return postFragment
        }
    }
}