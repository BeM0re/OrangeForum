package ru.be_more.orange_forum.ui.favorire

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.DownloadListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread


class FavoriteAdapter(groups: List<ExpandableGroup<*>?>?,
                      var downloadListener: DownloadListener,
                      var linkListener: LinkOnClickListener,
                      var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<FavoriteBoardViewHolder, FavoriteThreadViewHolder>(groups){

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_downloaded_board, parent, false)
        return FavoriteBoardViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteThreadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_board_op, parent, false)
        return FavoriteThreadViewHolder(view, picListener)
    }

    override fun onBindChildViewHolder(
        holder: FavoriteThreadViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val thread: BoardThread = (group as Board).items[childIndex]

        if(thread.posts.isNotEmpty()) {
            holder.setSenderName(thread.posts[0].name)
            holder.setIsOp(thread.posts[0].op > 0)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].num)
            holder.setTitle(thread.posts[0].subject)
            holder.setComment(thread.posts[0].comment)
            holder.setTotalPosts(thread.posts[0].posts_count)
            holder.setPostsWithPic(thread.posts[0].files_count)
            if (thread.posts[0].files.isNotEmpty()) {
                holder.setPics(thread.posts[0].files)
            }
            holder.setCommentListener(linkListener)
            holder.setRemoveButton(group.id, thread, downloadListener)
            holder.setDivider()
            holder.setIntoThreadButton(View.OnClickListener {
                downloadListener.intoThreadClick(
                    group.id,
                    thread.num,
                    thread.title
                )
            })
        }
    }

    override fun onBindGroupViewHolder(
        holder: FavoriteBoardViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        holder.setBoardTitle((group as Board).name)
        holder.expand()
    }
}