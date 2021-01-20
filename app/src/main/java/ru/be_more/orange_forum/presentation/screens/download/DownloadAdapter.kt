package ru.be_more.orange_forum.presentation.screens.download

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.DownloadListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread


class DownloadAdapter(groups: List<ExpandableGroup<*>?>?,
                      var downloadListener: DownloadListener,
                      var linkListener: LinkOnClickListener,
                      var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<DownloadedBoardViewHolder, DownloadedThreadViewHolder>(groups){

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadedBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_downloaded_board, parent, false)
        return DownloadedBoardViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadedThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_op_post_short, parent, false)
        return DownloadedThreadViewHolder(view, picListener)
    }

    override fun onBindChildViewHolder(
        holder: DownloadedThreadViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val thread: BoardThread = (group as Board).items[childIndex]

        if(thread.posts.isNotEmpty()) {
            holder.setSenderName(thread.posts[0].name)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            holder.setPics(thread.posts[0].files.getOrNull(0))
            holder.setCommentListener(View.OnClickListener {
                downloadListener.intoThreadClick(
                    group.id,
                    thread.num,
                    thread.title
                )
            })
            holder.setRemoveButton(group.id, thread, downloadListener)
            holder.setDivider()
        }
    }

    override fun onBindGroupViewHolder(
        holder: DownloadedBoardViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        holder.setBoardTitle((group as Board).name)
    }
}