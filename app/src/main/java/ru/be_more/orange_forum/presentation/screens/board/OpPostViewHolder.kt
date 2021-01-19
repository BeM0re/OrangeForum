package ru.be_more.orange_forum.presentation.screens.board

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_board_op.*
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.presentation.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.presentation.screens.post.PostPicAdapter

class OpPostViewHolder(itemView: View?, private var listener: PicOnClickListener ) :
    ChildViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun setSenderName (param: String){
        tv_board_op_name.text = param
    }
    fun setIsOp (isOp: Boolean, isHidden: Boolean){
        if(isOp)
            tv_board_op_check.visibility=View.VISIBLE
        else
            tv_board_op_check.visibility=View.GONE
    }
    fun setDate (param: String){
        tv_board_op_datetime.text = param
    }
    fun setThreadNum (param: Int){
        tv_board_op_num.text = param.toString()
    }
    fun setTitle (param: String){
        tv_board_op_subject.text = param
    }

    fun setPics (urls: List<AttachFile>, isHidden: Boolean){
        if (urls.isNotEmpty() && !isHidden){
            val adapter = PostPicAdapter(urls, listener = listener)

            rv_op_post_pics.layoutManager = GridLayoutManager(itemView.context, 2)
            rv_op_post_pics.adapter = adapter
            rv_op_post_pics.visibility = View.VISIBLE
        }
        else{
            rv_op_post_pics.visibility = View.GONE
            rv_op_post_pics.adapter = null
        }
    }

    fun setComment (param: String, isHidden: Boolean){
        if (param != "" && !isHidden) {
            tv_board_op_comment.text = param
            tv_board_op_comment.visibility = View.VISIBLE
        }
        else {
            tv_board_op_comment.visibility = View.GONE
            tv_board_op_comment.text = ""
        }
    }

    fun setTotalPosts (param: Int, isHidden: Boolean){
        if(isHidden)
            tv_board_op_total.visibility = View.GONE
        else
            tv_board_op_total.text ="Пропущено $param постов"
    }

    fun setPostsWithPic (param: Int, isHidden: Boolean){
        if(isHidden)
            tv_board_op_with_pic.visibility = View.GONE
        else
            tv_board_op_with_pic.text = "$param c картинками"
    }

    fun setIntoThreadButton(listener: View.OnClickListener, isHidden: Boolean) {
        if (isHidden)
            btn_board_op_into.visibility = View.GONE
        else {
            btn_board_op_into.setOnClickListener(listener)
            btn_board_op_into.visibility = View.VISIBLE
        }
    }

    fun setCommentListener(listener: LinkOnClickListener){
        tv_board_op_comment.setListener(listener)
    }

    fun setHideButton(thread: BoardThread, listener: BoardOnClickListener) {
        if(thread.isHidden) {
            btn_board_op_hide.visibility = View.GONE
            itemView.setOnClickListener {
                thread.isHidden = false
                listener.onHideClick(thread.num, thread.isHidden)
            }
        }
        else{
            btn_board_op_hide.visibility = View.VISIBLE
            btn_board_op_hide.setOnClickListener {
                thread.isHidden = true
                listener.onHideClick(thread.num, thread.isHidden)
            }
        }
    }

}

