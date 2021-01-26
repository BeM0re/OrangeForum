package ru.be_more.orange_forum.presentation.screens.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener


class BoardAdapter(var threads: List<BoardThread> = listOf(),
                   private var picListener: PicOnClickListener,
                   private var boardListener: BoardOnClickListener,
                   private val linkListener: LinkOnClickListener,
                   private val queueListener: (Int) -> Unit
) : RecyclerView.Adapter<OpPostViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return OpPostViewHolder(inflater.inflate(R.layout.item_board_op, parent, false), picListener )
    }

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: OpPostViewHolder, position: Int) {
        val thread: BoardThread? = threads[position]
        if(thread != null){
            holder.setSenderName(thread.posts[0].name)
            holder.setIsOp(thread.posts[0].op > 0, thread.isHidden)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            holder.setComment(thread.posts[0].comment, thread.isHidden)
            holder.setTotalPosts(thread.posts[0].posts_count, thread.isHidden)
            holder.setPostsWithPic(thread.posts[0].files_count, thread.isHidden)
            if(thread.posts[0].files.isNotEmpty()){
                holder.setPics(thread.posts[0].files, thread.isHidden)
            }
            holder.setCommentListener(linkListener)
            holder.setHideButton(thread, boardListener)
            holder.setIntoThreadButton(View.OnClickListener { boardListener.onIntoThreadClick(thread.num, thread.title) }, thread.isHidden)
            holder.setAddToQueueButton(queueListener, thread.num)
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


