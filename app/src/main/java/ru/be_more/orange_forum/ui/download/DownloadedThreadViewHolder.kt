package ru.be_more.orange_forum.ui.download

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_board_op.*
import kotlinx.android.synthetic.main.item_thread_post.rv_op_post_pics
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_check
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_comment
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_datetime
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_name
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_num
import kotlinx.android.synthetic.main.item_thread_post.tv_board_op_subject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interfaces.DownloadListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.ui.custom.ExpandableTextView
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostPicAdapter

class DownloadedThreadViewHolder(itemView: View?, private var listener: PicOnClickListener) :
    ChildViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun setSenderName (param: String){
        tv_board_op_name.text = param
    }
    fun setIsOp (isOp: Boolean){
        if(isOp)
            this.tv_board_op_check.visibility=View.VISIBLE
        else
            this.tv_board_op_check.visibility=View.GONE
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

    fun setPics (urls: List<AttachFile>){
        if (urls.isNotEmpty()){
            val adapter = PostPicAdapter(urls, listener = listener)

            rv_op_post_pics.layoutManager = LinearLayoutManager(App.getInstance())
            rv_op_post_pics.adapter = adapter
            rv_op_post_pics.visibility = View.VISIBLE
        }
        else{
            rv_op_post_pics.visibility = View.GONE
            rv_op_post_pics.adapter = null
        }
    }

    fun setComment (param: String){
        if (param != "" ) {
            tv_board_op_comment.text = param
            tv_board_op_comment.visibility = View.VISIBLE
        }
        else {
            tv_board_op_comment.visibility = View.GONE
            tv_board_op_comment.text = ""
        }
    }

    fun setTotalPosts (param: Int){
        tv_board_op_total.visibility = View.GONE
    }

    fun setPostsWithPic (param: Int){
        tv_board_op_with_pic.visibility = View.GONE
    }

    fun setIntoThreadButton(listener: View.OnClickListener) {
        btn_board_op_into.setOnClickListener(listener)
        btn_board_op_into.visibility = View.VISIBLE
    }

    fun setCommentListener(listener: LinkOnClickListener){
        (tv_board_op_comment as ExpandableTextView).setListener(listener)
    }

    fun setRemoveButton(boardId: String, thread: BoardThread, listener: DownloadListener) {
//        removeButton.visibility = View.GONE
        btn_board_op_hide.text = "Удалить"
        btn_board_op_hide.setOnClickListener {
            listener.onRemoveClick(boardId, thread.num)
        }
    }

    fun setDivider(){
        v_post1_pic_divider.visibility = View.VISIBLE
    }

}

