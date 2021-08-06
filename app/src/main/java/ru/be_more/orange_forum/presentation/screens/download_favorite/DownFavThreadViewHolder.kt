package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.databinding.ItemBoardOpBinding
import ru.be_more.orange_forum.databinding.ItemOpPostShortBinding
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener

class DownFavThreadViewHolder(
    private val binding: ItemOpPostShortBinding,
    private var listener: PicOnClickListener
    ) : ChildViewHolder(binding.root) {

    fun setSenderName (param: String){
        binding.tvFavoriteOpName.text = param
    }
    fun setDate (param: String){
        binding.tvFavoriteOpDatetime.text = param
    }
    fun setThreadNum (param: Int){
        binding.tvFavoriteOpNum.text = param.toString()
    }
    fun setTitle (param: String){
        binding.tvFavoriteOpSubject.text = param
    }

    fun setPics (url: AttachFile?){
        if (url != null){

            //TODO перенести в константы
            val thumbnailUrl = "https://2ch.hk${url.thumbnail}"
            val fullPicUrl = "https://2ch.hk${url.path}"

            val thumbnailGlideUrl = GlideUrl(
                thumbnailUrl, LazyHeaders.Builder()
                    .addHeader("Cookie", COOKIE)
                    .build()
            )

            Glide.with(itemView)
                .load(thumbnailGlideUrl)
                .into(binding.ivFavoriteOpPic)

            binding.ivFavoriteOpPic.visibility = View.VISIBLE
            binding.ivFavoriteOpPic.setOnClickListener {
                listener.onThumbnailListener(fullPicUrl, url.duration, null) }

            //нужно именно .isNullOrEmpty
            if (!url.duration.isNullOrEmpty()){
                binding.ivFavoritePlayBackground.visibility = View.VISIBLE
                binding.ivFavoritePlay.visibility = View.VISIBLE
            }
            else {
                binding.ivFavoritePlayBackground.visibility = View.GONE
                binding.ivFavoritePlay.visibility = View.GONE
            }

            binding.ivFavoriteOpPic.visibility = View.VISIBLE
        }
        else{
            binding.ivFavoriteOpPic.visibility = View.GONE
        }
    }

    fun setCommentListener(listener: View.OnClickListener){
        binding.clFavoriteOpPost.setOnClickListener(listener)
    }

    fun setRemoveButton(boardId: String, threadNum: Int, listener: DownFavListener) {
        binding.ivFavoriteCloseButton.setOnClickListener {
            listener.onRemoveClick(boardId, threadNum)
        }
    }

    fun setDivider(){
        binding.vFavoritePost1PicDivider.visibility = View.VISIBLE
    }

    fun setIcon(isFavorite: Boolean, isDownloaded: Boolean){
        binding.icShortDownload.visibility = if (isDownloaded) View.VISIBLE else View.GONE
        binding.icShortFavorite.visibility = if (isFavorite) View.VISIBLE else View.GONE
    }

    fun setNewMessageBadge(count: Int){
        binding.tvFavoriteOpNewMessages.isVisible = count > 0
        binding.tvFavoriteOpNewMessages.text = count.toString()
    }

}

