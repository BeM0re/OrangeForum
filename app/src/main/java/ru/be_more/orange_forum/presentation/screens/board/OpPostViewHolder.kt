package ru.be_more.orange_forum.presentation.screens.board

import android.view.View
import androidx.core.view.isVisible
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.databinding.ItemBoardOpBinding
import ru.be_more.orange_forum.presentation.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.presentation.screens.post.PostPicAdapter

class OpPostViewHolder(
    private val binding: ItemBoardOpBinding,
    private var listener: PicOnClickListener
    ) : ChildViewHolder(binding.root) {

    fun setSenderName (param: String){
        binding.tvBoardOpName.text = param
    }

    fun setIsOp (isOp: Boolean){
        binding.tvBoardOpCheck.isVisible = isOp
    }

    fun setDate (param: String){
        binding.tvBoardOpDatetime.text = param
    }

    fun setThreadNum (param: Int){
        binding.tvBoardOpNum.text = param.toString()
    }

    fun setTitle (param: String){
        binding.tvBoardOpSubject.text = param
    }

    fun setPics (urls: List<AttachFile>, isHidden: Boolean){
        binding.rvOpPostPics.isVisible = !isHidden && urls.isNotEmpty()

//            binding.rvOpPostPics.layoutManager = GridLayoutManager(itemView.context, 2)
        if (urls.isNotEmpty())
            binding.rvOpPostPics.adapter = PostPicAdapter(urls, listener = listener)
        else
            binding.rvOpPostPics.adapter = null
    }

    fun setComment (param: String, isHidden: Boolean){
        binding.tvBoardOpComment.isVisible = !isHidden
        if (param.isNotEmpty())
            binding.tvBoardOpComment.text = param
        else
            binding.tvBoardOpComment.text = ""
    }

    fun setTotalPosts (param: Int, isHidden: Boolean){
        binding.tvBoardOpTotal.isVisible = !isHidden
        binding.tvBoardOpTotal.text = itemView.context.getString(R.string.missed_posts_title, param)
    }

    fun setPostsWithPic (param: Int, isHidden: Boolean){
        binding.tvBoardOpWithPic.isVisible = !isHidden
        binding.tvBoardOpWithPic.text = itemView.context.getString(R.string.posts_with_image, param)
    }

    fun setIntoThreadButton(listener: View.OnClickListener, isHidden: Boolean) {
        binding.btnBoardOpInto.isVisible = !isHidden
        binding.btnBoardOpInto.setOnClickListener(listener)
    }

    fun setCommentListener(listener: LinkOnClickListener){
        binding.tvBoardOpComment.setListener(listener)
    }

    fun setHideButton(thread: BoardThread, listener: BoardOnClickListener) {
        if(thread.isHidden) {
            binding.btnBoardOpHide.visibility = View.GONE
            itemView.setOnClickListener {
                listener.onHideClick(thread.num, false)
            }
        }
        else{
            binding.btnBoardOpHide.visibility = View.VISIBLE
            binding.btnBoardOpHide.setOnClickListener {
                listener.onHideClick(thread.num, true)
            }
        }
    }

    fun setAddToQueueButton(queueListener: (Int) -> Unit, threadNum: Int, isHidden: Boolean) {
        binding.ivBoardOpAddQueue.isVisible = !isHidden
        binding.ivBoardOpAddQueue.setOnClickListener{ queueListener(threadNum) }
    }

}

