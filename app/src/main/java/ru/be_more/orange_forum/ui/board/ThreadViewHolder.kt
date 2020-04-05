package ru.be_more.orange_forum.ui.board

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Transformations.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class ThreadViewHolder(itemView: View?, var listener: BoardOnClickListener) : ChildViewHolder(itemView) {

    private var senderName: TextView = itemView!!.findViewById(R.id.tv_board_op_name)
    private var isOp: TextView = itemView!!.findViewById(R.id.tv_board_op_check)
    private var date: TextView = itemView!!.findViewById(R.id.tv_board_op_datetime)
    private var threadNum: TextView = itemView!!.findViewById(R.id.tv_board_op_num)
    private var title: TextView = itemView!!.findViewById(R.id.tv_board_op_subject)
    private var pics: RecyclerView = itemView!!.findViewById(R.id.rv_op_post_pics)
    private var comment: TextView = itemView!!.findViewById(R.id.tv_board_op_comment)
    private var totalPosts: TextView = itemView!!.findViewById(R.id.tv_board_op_total)
    private var postsWithPic: TextView = itemView!!.findViewById(R.id.tv_board_op_with_pic)

    fun setSenderName (param: String){
        senderName.text = param
    }
    fun setIsOp (param: Boolean){
        if(param)
            isOp.visibility=View.VISIBLE
        else
            isOp.visibility=View.GONE
    }
    fun setDate (param: String){
        date.text = param
    }
    fun setThreadNum (param: Int){
        threadNum.text = param.toString()
    }
    fun setTitle (param: String){
        title.text = param
    }
    //TODO сделать картинки
    fun setPics (urls: List<AttachFile>){
        val adapter = PostPicAdapter(urls, listener = listener)

        pics.layoutManager = LinearLayoutManager(App.getInstance())
        pics.adapter = adapter
    }
    fun setComment (param: String){
        comment.text = HtmlCompat.fromHtml(param, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    fun setTotalPosts (param: Int){
        totalPosts.text ="Пропущено $param постов"
    }
    fun setPostsWithPic (param: Int){
        postsWithPic.text = "$param c картинками"
    }

}

