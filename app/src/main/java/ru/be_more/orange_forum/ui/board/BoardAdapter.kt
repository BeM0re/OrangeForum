package ru.be_more.orange_forum.ui.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.BoardThread


class BoardAdapter( var threads: List<BoardThread> = listOf(), var listener: BoardOnClickListener) :
    RecyclerView.Adapter<ThreadViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ThreadViewHolder(inflater.inflate(R.layout.item_board_op, parent, false), listener)
    }

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        val thread: BoardThread? = threads[position]
        if(thread != null){
            holder.setSenderName(thread.posts[0].name)
            holder.setIsOp(thread.posts[0].op > 0)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            holder.setComment(thread.posts[0].comment)
            holder.setTotalPosts(thread.posts[0].posts_count)
            holder.setPostsWithPic(thread.posts[0].files_count)
            if(thread.posts[0].files.isNotEmpty()){
                holder.setPics(thread.posts[0].files)
            }
            holder.setHideButton()
            holder.setIntoThreadButton(View.OnClickListener { listener.onThreadClick(thread) })
        }
    }

    fun updateData(data:List<BoardThread>){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                threads[oldPos].num == data[newPos].num

            override fun getOldListSize(): Int = threads.size

            override fun getNewListSize(): Int  = data.size

            override fun areContentsTheSame(oldPos: Int, newPos: Int) : Boolean =
                threads[oldPos].hashCode() == data[newPos].hashCode()

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        threads = data
        diffResult.dispatchUpdatesTo(this)
    }
}


