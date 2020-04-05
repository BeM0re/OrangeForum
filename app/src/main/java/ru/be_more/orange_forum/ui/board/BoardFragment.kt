package ru.be_more.orange_forum.ui.board

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.item_post_pics.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread


//TODO сделать динамическое количество картинок через ресайклер
class BoardFragment private constructor(): MvpAppCompatFragment(),
    BoardOnClickListener,
    BoardView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var boardPresenter : BoardPresenter

    private lateinit var listener: (thread: BoardThread) -> Unit
    private lateinit var id: String
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : BoardAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardPresenter.setBoardId(id)
        recyclerView = rv_thread_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    override fun loadBoard(board: Board) {
        adapter = BoardAdapter(board.threads, this)

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, HORIZONTAL)
        )
    }

    companion object {
        fun getBoardFragment (listener: (thread: BoardThread) -> Unit, id: String): BoardFragment {
            val board = BoardFragment()
            board.listener = listener
            board.id = id

            return board
        }
    }

    override fun onThreadClick(thread: BoardThread) {
        listener(thread)
    }

    override fun onThumbnailListener(fullPicUrl: String) {
        val fullPicGlideUrl = GlideUrl(
            fullPicUrl,
            LazyHeaders.Builder()
                .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                        "_ga=GA1.2.57010468.1498700728; " +
                        "ageallow=1; " +
                        "_gid=GA1.2.1910512907.1585793763; " +
                        "_gat=1")
                .build()
        )
        v_op_post_pic_full_background.visibility = View.VISIBLE
        iv_op_post_pic_full.visibility = View.VISIBLE
        pb_op_pos_pic_loading.visibility = View.VISIBLE
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
                    pb_op_pos_pic_loading.visibility = View.GONE
                    return false
                }
            })
            .into(iv_op_post_pic_full)

        v_op_post_pic_full_background.setOnClickListener {
            v_op_post_pic_full_background.visibility = View.GONE
            iv_op_post_pic_full.visibility = View.GONE
            Glide.with(this).clear(iv_op_post_pic_full)
            pb_op_pos_pic_loading.visibility = View.GONE
        }
        iv_op_post_pic_full.setOnClickListener {
            v_op_post_pic_full_background.visibility = View.GONE
            iv_op_post_pic_full.visibility = View.GONE
        }
    }

}