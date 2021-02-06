package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread

class DownFavAdapter(groups: List<ExpandableGroup<*>?>?,
                     private var downFavListener: DownFavListener,
                     private var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<DownFavBoardViewHolder, DownFavThreadViewHolder>(groups){

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownFavBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_board_short, parent, false)
        return DownFavBoardViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownFavThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_op_post_short, parent, false)
        return DownFavThreadViewHolder(view, picListener)
    }

    override fun onBindChildViewHolder(
        holder: DownFavThreadViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val thread: BoardThread = (group as Board).items[childIndex]

        if(thread.posts.isNotEmpty()) {
            holder.setSenderName(thread.posts[0].name)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            holder.setPics(thread.posts[0].files.getOrNull(0))
            holder.setCommentListener {
                downFavListener.intoThreadClick(
                    group.id,
                    thread.num,
                    thread.title
                )
            }
            holder.setRemoveButton(group.id, thread.num, downFavListener)
            holder.setDivider()
            holder.setIcon(thread.isFavorite, thread.isDownloaded)
        }
    }

    override fun onBindGroupViewHolder(
        holder: DownFavBoardViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val board = (group as Board)
        holder.setBoardTitle(board.name)
        holder.setIntoBoardListener {
            downFavListener.intoBoardClick(
                board.id,
                board.name
            )
        }
    }
}