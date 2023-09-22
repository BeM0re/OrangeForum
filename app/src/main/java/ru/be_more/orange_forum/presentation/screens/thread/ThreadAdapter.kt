package ru.be_more.orange_forum.presentation.screens.thread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.presentation.screens.post.PostViewHolder

class ThreadAdapter(var thread: BoardThread,
                    private val picListener: PicOnClickListener,
                    private val linkListener: LinkOnClickListener
) : RecyclerView.Adapter<PostViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PostViewHolder(inflater.inflate( R.layout.item_post, parent, false),
            picListener)
    }

    override fun getItemCount(): Int = thread.posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
                holder.setNumber(thread.posts[position].number)
                holder.setSenderName(thread.posts[position].name)
                holder.setIsOp(thread.posts[position].isAuthorOp)
                holder.setDate(thread.posts[position].date)
                holder.setThreadNum(thread.posts[position].id)
                holder.setTitle(thread.posts[position].subject)
                holder.setComment(thread.posts[position].comment)
                holder.setPics(thread.posts[position].files)
                holder.setCommentListener(linkListener)
                holder.setReplies(thread.posts[position].replies, linkListener)
        }

    fun updateData(data: BoardThread){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                thread.posts[oldPos] == data.posts[newPos]

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


