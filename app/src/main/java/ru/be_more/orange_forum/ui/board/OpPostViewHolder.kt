package ru.be_more.orange_forum.ui.board

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.custom.ExpandableTextView
import ru.be_more.orange_forum.ui.post.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostPicAdapter


class OpPostViewHolder(itemView: View?, private var listener: PicOnClickListener) :
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

    fun setSenderName (param: String){
        senderName.text = param
    }
    fun setIsOp (isOp: Boolean, isHidden: Boolean){
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

    fun setPics (urls: List<AttachFile>, isHidden: Boolean){
        if (urls.isNotEmpty() && !isHidden){
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

    fun setComment (param: String, isHidden: Boolean){
        if (param != "" && !isHidden) {
            comment.text = param
            comment.visibility = View.VISIBLE
        }
        else {
            comment.visibility = View.GONE
            comment.text = ""
        }
    }

    fun setTotalPosts (param: Int, isHidden: Boolean){
        if(isHidden)
            totalPosts.visibility = View.GONE
        else
            totalPosts.text ="Пропущено $param постов"
    }

    fun setPostsWithPic (param: Int, isHidden: Boolean){
        if(isHidden)
            postsWithPic.visibility = View.GONE
        else
            postsWithPic.text = "$param c картинками"
    }

    fun setIntoThreadButton(listener: View.OnClickListener, isHidden: Boolean) {
        if (isHidden)
            pickThreadButton.visibility = View.GONE
        else
            pickThreadButton.setOnClickListener(listener)
    }

    fun setCommentListener(listener: LinkOnClickListener){
        comment.setListener(listener)
    }

    fun setHideButton(thread: BoardThread) {
        if(thread.isHidden)
            itemView.setOnClickListener {
                thread.isHidden = false
                /*isOp.visibility = View.VISIBLE
                pics.visibility = View.VISIBLE
                comment.visibility = View.VISIBLE
                totalPosts.visibility = View.VISIBLE
                postsWithPic.visibility = View.VISIBLE
                hideButton.visibility = View.VISIBLE
                pickThreadButton.visibility = View.VISIBLE
                itemView.setOnClickListener {  }
                itemView.setBackgroundColor(Color.parseColor("#000000FF"))*/
            }

        hideButton.setOnClickListener {
            thread.isHidden = true
           /* isOp.visibility = View.GONE
            pics.visibility = View.GONE
            comment.visibility = View.GONE
            totalPosts.visibility = View.GONE
            postsWithPic.visibility = View.GONE
            hideButton.visibility = View.GONE
            pickThreadButton.visibility = View.GONE
            itemView.setBackgroundColor(Color.parseColor("#00000004"))*/

        }
    }

}

