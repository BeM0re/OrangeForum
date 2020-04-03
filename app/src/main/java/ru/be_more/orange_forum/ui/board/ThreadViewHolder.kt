package ru.be_more.orange_forum.ui.category

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R

class ThreadViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var senderName: TextView = itemView!!.findViewById(R.id.tv_board_op_name)
    private var isOp: TextView = itemView!!.findViewById(R.id.tv_board_op_check)
    private var date: TextView = itemView!!.findViewById(R.id.tv_board_op_datetime)
    private var threadNum: TextView = itemView!!.findViewById(R.id.tv_board_op_num)
    private var title: TextView = itemView!!.findViewById(R.id.tv_board_op_subject)
    private var pic1: ImageView = itemView!!.findViewById(R.id.iv_board_op_pic1)
    private var pic2: ImageView = itemView!!.findViewById(R.id.iv_board_op_pic2)
    private var comment: TextView = itemView!!.findViewById(R.id.tv_board_op_comment)
    private var totalPosts: TextView = itemView!!.findViewById(R.id.tv_board_op_total)
    private var postsWithPic: TextView = itemView!!.findViewById(R.id.tv_board_op_with_pic)

    fun setSenderName (param: String){
        senderName.text = param
    }
    fun setIsOp (param: String){
        isOp.text = param
    }
    fun setDate (param: String){
        date.text = param
    }
    fun setThreadNum (param: String){
        threadNum.text = param
    }
    fun setTitle (param: String){
        title.text = param
    }
    //TODO сделать картинки
    fun setPic1 (param: String){
        pic1.setImageURI(null)
    }
    fun setPic2 (param: String){
        pic2.setImageURI(null)
    }
    fun setComment (param: String){
        comment.text = param
    }
    fun setTotalPosts (param: String){
        totalPosts.text = param
    }
    fun setPostsWithPic (param: String){
        postsWithPic.text = param
    }

}