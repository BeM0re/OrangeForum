package ru.be_more.orange_forum.presentation.screens.post

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.presentation.custom.LinkedTextView
import java.util.*


class PostViewHolder(itemView: View, private val listener: PicOnClickListener) :
    RecyclerView.ViewHolder(itemView){


    //FIXME убрать
    private var senderNumber: TextView = itemView.findViewById(R.id.tv_item_post_number)
    private var senderName: TextView = itemView.findViewById(R.id.tv_item_post_name)
    private var isOp: TextView = itemView.findViewById(R.id.tv_item_post_op_check)
    private var date: TextView = itemView.findViewById(R.id.tv_item_post_datetime)
    private var num: TextView = itemView.findViewById(R.id.tv_item_post_num)
    private var title: TextView = itemView.findViewById(R.id.tv_item_post_subject)
    private var pics: RecyclerView = itemView.findViewById(R.id.rv_item_post_pics)
    private var comment: LinkedTextView = itemView.findViewById(R.id.tv_item_post_comment)
    private var replyPosts: LinkedTextView = itemView.findViewById(R.id.tv_item_post_replies)

    fun setNumber(param: Int){
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

            pics.layoutManager = GridLayoutManager(itemView.context, 2)
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
            comment.text = param
            comment.visibility = View.VISIBLE
        }
        else {
            comment.visibility = View.GONE
            comment.text = ""
        }
    }

    fun setCommentListener(listener: LinkOnClickListener){
        comment.setListener(listener)
    }

    fun setReplies(replies: Stack<Int>, listener: LinkOnClickListener) {
        replyPosts.text = ""
        var replyResult = ""

        replies.forEach { reply ->

            replyResult = if (replyResult == "")
                "<a href='$reply'>>>$reply</a>"
            else
                "$replyResult <a href='$reply'>>>$reply</a>"

            replyPosts.text = replyResult

            replyPosts.setListener(listener)
        }

    }


}

