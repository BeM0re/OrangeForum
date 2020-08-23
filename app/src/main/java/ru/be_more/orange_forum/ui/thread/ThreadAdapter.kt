package ru.be_more.orange_forum.ui.thread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostViewHolder

const val RESPONSE_VIEW = 1

class ThreadAdapter(var thread: BoardThread,
                    private val picListener: PicOnClickListener,
                    private val linkListener: LinkOnClickListener
) : RecyclerView.Adapter<ThreadViewHolder>(){

    private var isFooterShown = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == RESPONSE_VIEW)
            ResponseViewHolder(inflater.inflate(R.layout.item_thread_response_form, parent, false))
        else
            PostViewHolder(
                inflater.inflate(
                    R.layout.item_post,
                    parent,
                    false
                ), picListener
            )
    }

    override fun getItemCount(): Int = thread.posts.size

    override fun getItemViewType(position: Int): Int {
        return if (position == thread.posts.size)
            RESPONSE_VIEW
        else
            super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        when(holder){
            is PostViewHolder -> {
                holder.setNumber(thread.posts[position].number)
                holder.setSenderName(thread.posts[position].name)
                holder.setIsOp(thread.posts[position].op > 0)
                holder.setDate(thread.posts[position].date)
                holder.setThreadNum(thread.posts[position].num)
                holder.setTitle(thread.posts[position].subject)
                holder.setComment(thread.posts[position].comment)
                holder.setPics(thread.posts[position].files)
                holder.setCommentListener(linkListener)
                holder.setReplies(thread.posts[position].replies, linkListener)
            }
            is ResponseViewHolder -> {

            }
        }
    }

    fun setIsFooterShown(isShow:Boolean){
        if (!isFooterShown == isShow) {
            isFooterShown = isShow
            this.notifyDataSetChanged()
        }
    }

    fun updateData(data: BoardThread){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                thread.num == data.num

            override fun getOldListSize(): Int = thread.posts.size

            override fun getNewListSize(): Int  = data.posts.size

            override fun areContentsTheSame(oldPos: Int, newPos: Int) : Boolean =
                thread.posts[oldPos].hashCode() == data.posts[newPos].hashCode()
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        thread = data
        diffResult.dispatchUpdatesTo(this)
    }
}


