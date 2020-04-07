package ru.be_more.orange_forum.ui.thread

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.ui.custom.ExpandableTextView
import ru.be_more.orange_forum.ui.post.PostOnClickListener
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class ThreadViewHolder(itemView: View?, private val listener: PostOnClickListener) :
    ChildViewHolder(itemView) {

    private var senderName: TextView = itemView!!.findViewById(R.id.tv_item_post_name)
    private var isOp: TextView = itemView!!.findViewById(R.id.tv_item_post_op_check)
    private var date: TextView = itemView!!.findViewById(R.id.tv_item_post_datetime)
    private var num: TextView = itemView!!.findViewById(R.id.tv_item_post_num)
    private var title: TextView = itemView!!.findViewById(R.id.tv_item_post_subject)
    private var pics: RecyclerView = itemView!!.findViewById(R.id.rv_item_post_pics)
    private var comment: ExpandableTextView = itemView!!.findViewById(R.id.tv_item_post_comment)

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
        title.text = param
    }
    //TODO сделать картинки
    fun setPics (urls: List<AttachFile>){
        val adapter = PostPicAdapter(urls, listener = listener)

        pics.layoutManager = LinearLayoutManager(App.getInstance())
        pics.adapter = adapter
    }
    fun setComment (param: String){
        comment.text = param
    }

}

