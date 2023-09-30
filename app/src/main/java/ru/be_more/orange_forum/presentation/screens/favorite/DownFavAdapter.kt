package ru.be_more.orange_forum.presentation.screens.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.databinding.ItemBoardShortBinding
import ru.be_more.orange_forum.databinding.ItemOpPostShortBinding
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board

class DownFavAdapter(private var boards: List<ExpandableGroup<*>?>?,
                     private var downFavListener: DownFavListener,
                     private var picListener: PicOnClickListener) :
    ExpandableRecyclerViewAdapter<DownFavBoardViewHolder, DownFavThreadViewHolder>(boards){

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownFavBoardViewHolder =
        DownFavBoardViewHolder(
            ItemBoardShortBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
        )


    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownFavThreadViewHolder =
        DownFavThreadViewHolder(
            ItemOpPostShortBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            picListener
        )


    override fun onBindChildViewHolder(
        holder: DownFavThreadViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        /*val thread: BoardThread = (group as Board).items[childIndex]

        if(thread.posts.isNotEmpty()) {
            holder.setSenderName(thread.posts[0].name)
            holder.setDate(thread.posts[0].date)
            holder.setThreadNum(thread.posts[0].id)
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
            holder.setNewMessageBadge(thread.newMessageAmount)
        }*/
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

    fun updateData(groups: List<ExpandableGroup<*>?>?){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                (boards?.get(oldPos) as? Board)?.id == (groups?.get(newPos) as? Board)?.id

            override fun getOldListSize(): Int = boards?.size ?: 0

            override fun getNewListSize(): Int  = groups?.size ?: 0

            override fun areContentsTheSame(oldPos: Int, newPos: Int) : Boolean {
                if (boards == null || groups == null)
                    return false

                boards?.get(oldPos)?.items?.forEachIndexed { index, thread ->
                    if (thread != groups.getOrNull(newPos)?.items?.getOrNull(index) ?: true)
                        return false
                }
                return true
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        boards = groups
        diffResult.dispatchUpdatesTo(this)
    }
}