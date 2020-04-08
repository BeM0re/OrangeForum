package ru.be_more.orange_forum.ui.thread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.board.OpPostViewHolder
import ru.be_more.orange_forum.ui.post.PostOnClickListener


class ThreadAdapter(var thread: BoardThread, private val listener: PostOnClickListener) :
    RecyclerView.Adapter<ThreadViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ThreadViewHolder(inflater.inflate(R.layout.item_post, parent, false), listener)
    }

    override fun getItemCount(): Int = thread.posts.size

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        holder.setNumber(thread.posts[position].number)
        holder.setSenderName(thread.posts[position].name)
        holder.setIsOp(thread.posts[position].op > 0)
        holder.setDate(thread.posts[position].date)
        holder.setThreadNum(thread.posts[position].num)
        holder.setTitle(thread.posts[position].subject)
        holder.setComment(thread.posts[position].comment)
        holder.setPics(thread.posts[position].files)
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


