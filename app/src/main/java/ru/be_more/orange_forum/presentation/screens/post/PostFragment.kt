package ru.be_more.orange_forum.presentation.screens.post

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.databinding.ItemPostBinding
import ru.be_more.orange_forum.presentation.bus.VideoToBeClosed
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.bus.AppToBeClosed
import ru.be_more.orange_forum.presentation.bus.BackPressed
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment

class PostFragment : BaseFragment<ItemPostBinding>() {

    override val binding: ItemPostBinding by viewBinding()
    private var timestamp: Long = 0
    private lateinit var content: ModalContent
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private lateinit var closeModalListener: CloseModalListener
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.item_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClosingViewClickListener()
        setContent(content)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: VideoToBeClosed) {
        hideModal()
    }

    override fun onDestroyView() {
        disposable?.dispose()
        disposable = null

        super.onDestroyView()
    }

    private fun showPost(post: Post){

        binding.vPostPicFullBackground.visibility = View.VISIBLE

        binding.llPostLayout.setBackgroundColor(resources.getColor(R.color.color_background))

        binding.tvItemPostComment.text = post.comment
        binding.tvItemPostComment.setListener(linkListener)

        binding.clPostHeader.visibility = View.GONE

        binding.tvItemPostSubject.isVisible = post.subject.isNotEmpty()
        binding.tvItemPostSubject.text = post.subject

        binding.rvItemPostPics.adapter = PostPicAdapter(post.files, picListener)

        binding.tvItemPostReplies.text = ""
        var replyResult = ""

        post.replies.forEach { reply ->
            replyResult = "$replyResult <a href='$reply'>>>$reply</a>"

            binding.tvItemPostReplies.text = replyResult

            binding.tvItemPostReplies.setListener(linkListener)
        }
    }

    private fun showAttachment(pic: Attachment) {

        binding.vPostPicFullBackground.visibility = View.VISIBLE

        if(pic.duration == "") {
            var fullPicGlideUrl: GlideUrl? = null
            if(pic.uri == null) {
                fullPicGlideUrl = GlideUrl(
                    pic.url,
                    LazyHeaders.Builder()
                        .addHeader("Cookie", COOKIE)
                        .build()
                )
            }
            binding.ivPost1PicFull.resetZoom()
            binding.ivPost1PicFull.visibility = View.VISIBLE
            Glide.with(this)
                .load(pic.uri ?: fullPicGlideUrl)
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
                        binding.pbPost1PicLoading.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.ivPost1PicFull)
        }
        else{
            binding.pbPost1PicLoading.visibility = View.VISIBLE

            //TODO потом поменять на нормальные куки
            val headers = mapOf("cookie" to COOKIE)

            binding.vvPost1Video.setOnPreparedListener { binding.pbPost1PicLoading.visibility = View.GONE }
            binding.vvPost1Video.setVideoURI(Uri.parse(pic.url), headers)
            binding.vvPost1Video.visibility = View.VISIBLE
            binding.vvPost1Video.setMediaController(MediaController(this.context))
            binding.vvPost1Video.requestFocus(0)
            binding.vvPost1Video.start()
        }
    }

    private fun setContent(content: ModalContent) {
        when (content){
            is Post -> showPost(content)
            is Attachment -> showAttachment(content)
        }
    }

    private fun setClosingViewClickListener(){
        binding.vPostPicFullBackground.setOnClickListener {
            hideModal()
        }

        binding.ivPost1PicFull.setOnClickListener {
            binding.vPostPicFullBackground.visibility = View.GONE
            binding.ivPost1PicFull.visibility = View.GONE
            hideModal()
        }

        binding.vvPost1Video.setOnClickListener {
            if(System.currentTimeMillis() - timestamp < 2000) {
                binding.pbPost1PicLoading.visibility = View.GONE
                binding.vvPost1Video.visibility = View.GONE
                binding.vPostPicFullBackground.visibility = View.GONE
                hideModal()
            }
            else
                timestamp = System.currentTimeMillis()
        }
    }

    private fun hideModal(){
        binding.vPostPicFullBackground.visibility = View.GONE
        binding.ivPost1PicFull.visibility = View.GONE
        Glide.with(this).clear(binding.ivPost1PicFull)
        binding.pbPost1PicLoading.visibility = View.GONE
        binding.vvPost1Video.visibility = View.GONE
        binding.tvItemPostComment.text = ""
        binding.tvItemPostSubject.text = ""
        binding.tvItemPostReplies.text = ""

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