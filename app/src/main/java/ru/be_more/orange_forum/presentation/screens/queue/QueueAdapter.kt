package ru.be_more.orange_forum.presentation.screens.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.FavoriteListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread


class QueueAdapter(groups: List<ExpandableGroup<*>?>?,
                   var favoriteListener: FavoriteListener,
                   var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<QueueBoardViewHolder, QueueThreadViewHolder>(groups){

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QueueBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_downloaded_board, parent, false)
        return QueueBoardViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QueueThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_op_post_short, parent, false)
        return QueueThreadViewHolder(view, picListener)
    }

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
//            holder.setRemoveButton(group.id, thread, downloadListener) //TODO добавить удоление из избраного
            holder.setDivider()
            holder.setIntoThreadButton(View.OnClickListener {
                favoriteListener.intoThreadClick(
                    group.id,
                    thread.num,
                    thread.title
                )
            })
        }
    }

    override fun onBindGroupViewHolder(
        holder: QueueBoardViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val board = group as Board
        holder.setBoardTitle(board.name)
        holder.setIntoBoardListener(View.OnClickListener {
            favoriteListener.intoBoardClick(
                board.id,
                board.name
            )
        })
        holder.expand()
    }
}