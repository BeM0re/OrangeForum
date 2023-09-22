package ru.be_more.orange_forum.presentation.screens.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.databinding.ItemBoardOpBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpPostViewHolder =
        OpPostViewHolder(
            ItemBoardOpBinding.inflate(
                LayoutInflater.from(parent.context), parent, false),
            picListener
        )

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: OpPostViewHolder, position: Int) {
        val thread = threads[position]
        val opPost = thread.posts[0]
        holder.setSenderName(opPost.name)
        holder.setIsOp(opPost.isAuthorOp)
        holder.setDate(opPost.date)
        holder.setThreadNum(opPost.id)
        holder.setTitle(opPost.subject)
        holder.setComment(opPost.comment, thread.isHidden)
        holder.setTotalPosts(opPost.postsCount, thread.isHidden)
        holder.setPostsWithPic(opPost.filesCount, thread.isHidden)
        if(opPost.files.isNotEmpty()){
            holder.setPics(opPost.files, thread.isHidden)
        }
        holder.setCommentListener(linkListener)
        holder.setHideButton(thread, boardListener)
        holder.setIntoThreadButton( { boardListener.onIntoThreadClick(thread.num, thread.title) }, thread.isHidden)
        holder.setAddToQueueButton(queueListener, thread.num, thread.isHidden)
    }

    fun updateData(data:List<BoardThread>){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                threads[oldPos].num == data[newPos].num

            override fun getOldListSize(): Int = threads.size

            override fun getNewListSize(): Int  = data.size

            override fun areContentsTheSame(oldPos: Int, newPos: Int) : Boolean {
                return threads[oldPos] == data[newPos]
            }

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        threads = data
        diffResult.dispatchUpdatesTo(this)
    }
}


