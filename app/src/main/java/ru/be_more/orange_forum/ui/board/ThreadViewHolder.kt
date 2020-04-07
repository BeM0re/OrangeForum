package ru.be_more.orange_forum.ui.board

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
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
import ru.be_more.orange_forum.ui.custom.ExpandableTextView
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class ThreadViewHolder(itemView: View?, private var listener: BoardOnClickListener) :
    ChildViewHolder(itemView) {

    private var senderName: TextView = itemView!!.findViewById(R.id.tv_board_op_name)
    private var isOp: TextView = itemView!!.findViewById(R.id.tv_board_op_check)
    private var date: TextView = itemView!!.findViewById(R.id.tv_board_op_datetime)
    private var threadNum: TextView = itemView!!.findViewById(R.id.tv_board_op_num)
    private var title: TextView = itemView!!.findViewById(R.id.tv_board_op_subject)
    private var pics: RecyclerView = itemView!!.findViewById(R.id.rv_op_post_pics)
    private var comment: ExpandableTextView = itemView!!.findViewById(R.id.tv_board_op_comment)
    private var totalPosts: TextView = itemView!!.findViewById(R.id.tv_board_op_total)
    private var postsWithPic: TextView = itemView!!.findViewById(R.id.tv_board_op_with_pic)
    private var pickThreadButton: Button = itemView!!.findViewById(R.id.btn_board_op_into)
    private var hideButton: Button = itemView!!.findViewById(R.id.btn_board_op_hide)

    private lateinit var senderNameValue: String
    private lateinit var isOpValue: String
    private lateinit var dateValue: String
    private lateinit var threadNumValue: String
    private lateinit var titleValue: String
    private lateinit var picsValue: String
    private lateinit var commentValue: String
    private lateinit var totalPostsValue: String
    private lateinit var postsWithPicValue: String

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
//        comment.text = HtmlCompat.fromHtml(param, HtmlCompat.FROM_HTML_MODE_LEGACY)
        comment.text = param
    }
    fun setTotalPosts (param: Int){
        totalPosts.text ="Пропущено $param постов"
    }
    fun setPostsWithPic (param: Int){
        postsWithPic.text = "$param c картинками"
    }
    fun setIntoThreadButton(listener: View.OnClickListener) {
        pickThreadButton.setOnClickListener(listener)
    }
    fun setHideButton() {
        hideButton.setOnClickListener {
            isOp.visibility = View.GONE
            pics.visibility = View.GONE
            comment.visibility = View.GONE
            totalPosts.visibility = View.GONE
            postsWithPic.visibility = View.GONE
            hideButton.visibility = View.GONE
            pickThreadButton.visibility = View.GONE

            itemView.setBackgroundColor(Color.parseColor("#00000004"))
            itemView.setOnClickListener {
                isOp.visibility = View.VISIBLE
                pics.visibility = View.VISIBLE
                comment.visibility = View.VISIBLE
                totalPosts.visibility = View.VISIBLE
                postsWithPic.visibility = View.VISIBLE
                hideButton.visibility = View.VISIBLE
                pickThreadButton.visibility = View.VISIBLE
                itemView.setOnClickListener {  }
                itemView.setBackgroundColor(Color.parseColor("#000000FF"))
            }
        }
    }

}

