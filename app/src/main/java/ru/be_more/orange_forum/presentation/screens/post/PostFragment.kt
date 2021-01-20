package ru.be_more.orange_forum.presentation.screens.post

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.item_post.*
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.VideoToBeClosed
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post

class PostFragment : Fragment(R.layout.item_post) {

    private var timestamp: Long = 0
    private lateinit var content: ModalContent
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private lateinit var closeModalListener: CloseModalListener
    private var disposable: Disposable? = null
    private var recyclerView: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClosingViewClickListener()
        subscribe()
        setContent(content)
    }

    private fun subscribe(){
        disposable = App.getBus().subscribe(
            {
                if(it is VideoToBeClosed)
                    hideModal()
            },
            {
                Log.e("M_PostFragment","bus error = \n $it")
            }
        )
    }

    override fun onDestroyView() {
        disposable?.dispose()
        disposable = null
        recyclerView?.adapter = null

        super.onDestroyView()
    }

    private fun showPost(post: Post){

        v_post1_pic_full_background.visibility = View.VISIBLE

        ll_post_layout.setBackgroundColor(resources.getColor(R.color.color_background))

        tv_item_post_comment.text = post.comment
        tv_item_post_comment.setListener(linkListener)

        cl_post_header.visibility = View.GONE

        if (post.subject.isEmpty())
            tv_item_post_subject.visibility = View.GONE
        else
            tv_item_post_subject.text = post.subject

        recyclerView = rv_item_post_pics
        recyclerView?.layoutManager = LinearLayoutManager(this.context)

        recyclerView?.adapter = PostPicAdapter(post.files, picListener)

        tv_item_post_replies.text = ""
        var replyResult = ""

        post.replies.forEach { reply ->

            replyResult = if (replyResult == "")
                "<a href='$reply'>>>$reply</a>"
            else
                "$replyResult <a href='$reply'>>>$reply</a>"

            tv_item_post_replies.text = replyResult

            tv_item_post_replies.setListener(linkListener)
        }
    }

    private fun showAttachment(pic: Attachment) {

        v_post1_pic_full_background.visibility = View.VISIBLE
        pb_post1_pic_loading.visibility = View.VISIBLE

        if(pic.duration == "") {
            var fullPicGlideUrl: GlideUrl? = null
            if(pic.uri == null) {
                fullPicGlideUrl = GlideUrl(
                    pic.url,
                    LazyHeaders.Builder()
                        .addHeader(
                            "Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                                    "_ga=GA1.2.57010468.1498700728; " +
                                    "ageallow=1; " +
                                    "_gid=GA1.2.1910512907.1585793763; " +
                                    "_gat=1"
                        ).build()
                )
            }
            iv_post1_pic_full.resetZoom()
            iv_post1_pic_full.visibility = View.VISIBLE
            Glide.with(this)
                .load(if (pic.uri != null) pic.uri else fullPicGlideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("M_PostFragment", "$e")
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
        }
    }

    private fun setContent(content: ModalContent) {
        when (content){
            is Post -> showPost(content)
            is Attachment -> showAttachment(content)
        }
    }

    private fun setClosingViewClickListener(){
        v_post1_pic_full_background.setOnClickListener {
            hideModal()
        }

        iv_post1_pic_full.setOnClickListener {
            v_post1_pic_full_background.visibility = View.GONE
            iv_post1_pic_full.visibility = View.GONE
            hideModal()
        }

        vv_post1_video.setOnClickListener {
            if(System.currentTimeMillis() - timestamp < 2000) {
                pb_post1_pic_loading.visibility = View.GONE
                vv_post1_video.visibility = View.GONE
                v_post1_pic_full_background.visibility = View.GONE
                hideModal()
            }
            else
                timestamp = System.currentTimeMillis()
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

        disposable?.dispose()

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