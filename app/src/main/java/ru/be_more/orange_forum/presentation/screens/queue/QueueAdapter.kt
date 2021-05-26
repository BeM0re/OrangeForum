package ru.be_more.orange_forum.presentation.screens.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.databinding.ItemBoardShortBinding
import ru.be_more.orange_forum.databinding.ItemOpPostShortBinding
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener


class QueueAdapter(groups: List<ExpandableGroup<*>?>?,
                   private var queueListener: DownFavListener,
                   private var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<QueueBoardViewHolder, QueueThreadViewHolder>(groups){

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): QueueBoardViewHolder =
        QueueBoardViewHolder(
            ItemBoardShortBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QueueThreadViewHolder =
        QueueThreadViewHolder(
            ItemOpPostShortBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            picListener
        )


    override fun onBindChildViewHolder(
        holder: QueueThreadViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val thread: BoardThread = (group as Board).items[childIndex]

        if(thread.posts.isNotEmpty()) {
            holder.setSenderName(thread.posts[0].name)
            holder.setIsOp(thread.posts[0].op > 0)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            if (thread.posts[0].files.isNotEmpty()) {
                holder.setPics(thread.posts[0].files[0])
            }
//            holder.setRemoveButton(group.id, thread, downloadListener) //TODO добавить удаление из избраного
            holder.setDivider()
            holder.setIntoThreadButton {
                queueListener.intoThreadClick(
                    group.id,
                    thread.num,
                    thread.title
                )
            }
            holder.setRemoveButton(group.id, thread.num, queueListener)
        }
    }

    override fun onBindGroupViewHolder(
        holder: QueueBoardViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val board = group as Board
        holder.setBoardTitle(board.name)
        holder.setIntoBoardListener {
            queueListener.intoBoardClick(
                board.id,
                board.name
            )
        }
        holder.expand()
    }
}