package ru.be_more.orange_forum.ui.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
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
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.android.synthetic.main.item_thread_response_form.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.post.PostOnClickListener


//TODO сделать динамическое количество картинок через ресайклер
class ThreadFragment : MvpAppCompatFragment(),
    PostOnClickListener,
    ThreadView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var threadPresenter : ThreadPresenter

    private var timestamp: Long = 0
    private lateinit var boardId: String
    private var threadId: Int = 0
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : ThreadAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        threadPresenter.init(boardId, threadId)
        recyclerView = rv_post_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        srl_thread.setColorSchemeColors(ContextCompat.getColor(App.applicationContext(), R.color.color_accent))
        srl_thread.setOnRefreshListener {
            srl_thread.isRefreshing = false
            threadPresenter.updateThreadData()
        }

        rv_post_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab_thread_respond.visibility == View.VISIBLE) {
                    fab_thread_respond.hide()
                } else if (dy < 0 && fab_thread_respond.visibility != View.VISIBLE ) {
                    fab_thread_respond.show()
                }
            }
        })

//        fab_thread_respond.setOnClickListener { threadPresenter.openResponseForm() }
        fab_thread_respond.setOnClickListener { threadPresenter.post() }

//        btn_response_submit.setOnClickListener()
    }

    override fun loadThread(thread: BoardThread) {
        adapter = ThreadAdapter(thread, this)

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, HORIZONTAL)
        )
    }

    override fun hideResponseFab() {
        fab_thread_respond.visibility = View.GONE
    }

    override fun onThumbnailListener(fullPicUrl: String, duration: String?) {

        v_post_pic_full_background.visibility = View.VISIBLE
        pb_post_pic_loading.visibility = View.VISIBLE

        if(duration == "") {
            val fullPicGlideUrl = GlideUrl(
                fullPicUrl,
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
            iv_post_pic_full.visibility = View.VISIBLE
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
                        pb_post_pic_loading.visibility = View.GONE
                        return false
                    }
                })
                .into(iv_post_pic_full)

            iv_post_pic_full.setOnClickListener {
                v_post_pic_full_background.visibility = View.GONE
                iv_post_pic_full.visibility = View.GONE
            }
        }
        else{
            vv_post_video.setVideoURI(Uri.parse(fullPicUrl))
            vv_post_video.visibility = View.VISIBLE
            vv_post_video.setMediaController(MediaController(this.context))
            vv_post_video.requestFocus(0)
            vv_post_video.start()
            pb_post_pic_loading.visibility = View.GONE

            vv_post_video.setOnClickListener {
                if(System.currentTimeMillis() - timestamp < 3000) {
                    pb_post_pic_loading.visibility = View.GONE
                    vv_post_video.visibility = View.GONE
                    v_post_pic_full_background.visibility = View.GONE
                }
                else
                    timestamp = System.currentTimeMillis()
            }
        }

        v_post_pic_full_background.setOnClickListener {
            v_post_pic_full_background.visibility = View.GONE
            iv_post_pic_full.visibility = View.GONE
            Glide.with(this).clear(iv_post_pic_full)
            pb_post_pic_loading.visibility = View.GONE
            vv_post_video.visibility = View.GONE
        }
    }

    companion object {
        fun getThreadFragment ( boardId: String, threadId: Int): ThreadFragment {
            val thread = ThreadFragment()
            thread.boardId = boardId
            thread.threadId = threadId

            return thread
        }
    }
}