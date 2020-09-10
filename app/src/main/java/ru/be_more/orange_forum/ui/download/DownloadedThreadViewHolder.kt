package ru.be_more.orange_forum.ui.download

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.DownloadListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.ui.custom.ExpandableTextView
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class DownloadedThreadViewHolder(itemView: View?, private var listener: PicOnClickListener) :
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
    private var removeButton: Button = itemView!!.findViewById(R.id.btn_board_op_hide)
    private var dividerView: View = itemView!!.findViewById(R.id.v_post1_pic_divider)

    fun setSenderName (param: String){
        senderName.text = param
    }
    fun setIsOp (isOp: Boolean){
        if(isOp)
            this.isOp.visibility=View.VISIBLE
        else
            this.isOp.visibility=View.GONE
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

    fun setPics (urls: List<AttachFile>){
        if (urls.isNotEmpty()){
            val adapter = PostPicAdapter(urls, listener = listener)

            pics.layoutManager = LinearLayoutManager(App.getInstance())
            pics.adapter = adapter
            pics.visibility = View.VISIBLE
        }
        else{
            pics.visibility = View.GONE
            pics.adapter = null
        }
    }

    fun setComment (param: String){
        if (param != "" ) {
            comment.text = param
            comment.visibility = View.VISIBLE
        }
        else {
            comment.visibility = View.GONE
            comment.text = ""
        }
    }

    fun setTotalPosts (param: Int){
//        totalPosts.text ="Пропущено $param постов"
        totalPosts.visibility = View.GONE
    }

    fun setPostsWithPic (param: Int){
//        postsWithPic.text = "$param c картинками"
        postsWithPic.visibility = View.GONE
    }

    fun setIntoThreadButton(listener: View.OnClickListener) {
        pickThreadButton.setOnClickListener(listener)
        pickThreadButton.visibility = View.VISIBLE
    }

    fun setCommentListener(listener: LinkOnClickListener){
        comment.setListener(listener)
    }

    fun setRemoveButton(boardId: String, thread: BoardThread, listener: DownloadListener) {
//        removeButton.visibility = View.GONE
        removeButton.text = "Удалить"
        removeButton.setOnClickListener {
            listener.onRemoveClick(boardId, thread.num)
        }
    }

    fun setDivider(){
        dividerView.visibility = View.VISIBLE
    }

}

