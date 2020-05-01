package ru.be_more.orange_forum.ui.thread

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.ui.custom.LinkedTextView
import ru.be_more.orange_forum.ui.post.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class PostViewHolder(itemView: View, private val listener: PicOnClickListener) :
    ThreadViewHolder(itemView) {

    private var senderNumber: TextView = itemView.findViewById(R.id.tv_item_post_number)
    private var senderName: TextView = itemView.findViewById(R.id.tv_item_post_name)
    private var isOp: TextView = itemView.findViewById(R.id.tv_item_post_op_check)
    private var date: TextView = itemView.findViewById(R.id.tv_item_post_datetime)
    private var num: TextView = itemView.findViewById(R.id.tv_item_post_num)
    private var title: TextView = itemView.findViewById(R.id.tv_item_post_subject)
    private var pics: RecyclerView = itemView.findViewById(R.id.rv_item_post_pics)
    private var comment: LinkedTextView = itemView.findViewById(R.id.tv_item_post_comment)

    fun setNumber (param: Int){
        senderNumber.text = param.toString()
    }
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
        num.text = param.toString()
    }
    fun setTitle (param: String){
        if (param != ""){
            title.text = param
            title.visibility = View.VISIBLE
        }
        else {
            title.text = ""
            title.visibility = View.GONE
        }
    }

    fun setPics (urls: List<AttachFile>){
        if(urls.isNotEmpty()){
            val adapter = PostPicAdapter(urls, listener = listener)

            pics.layoutManager = LinearLayoutManager(App.getInstance())
            pics.adapter = adapter
            pics.visibility = View.VISIBLE
        }
        else {
            pics.visibility = View.GONE
            pics.adapter = null
        }

    }
    fun setComment (param: String){
        if (param != "") {
//            comment.text = HtmlCompat.fromHtml(param, HtmlCompat.FROM_HTML_MODE_LEGACY)
            comment.text = param
            comment.visibility = View.VISIBLE
        }
        else {
            comment.visibility = View.GONE
            comment.text = ""
        }
    }

    fun setListener(listener: LinkOnClickListener){
        comment.setListener(listener)
    }

}

