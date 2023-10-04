package ru.be_more.orange_forum.presentation.screens.queue

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.databinding.ItemOpPostShortBinding
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener

class QueueThreadViewHolder(private val binding: ItemOpPostShortBinding,
                            private var listener: PicOnClickListener):
    ChildViewHolder(binding.root) {


    fun setSenderName (param: String){
        binding.tvFavoriteOpName.text = param
    }

    fun setIsOp (isOp: Boolean){
        binding.tvFavoriteOpCheck.isVisible = isOp
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

    fun setPics (url: AttachedFile?){
        if (url != null){

            //TODO перенести в константы
            val thumbnailUrl = "https://2ch.hk${url.thumbnail}"
            val fullPicUrl = "https://2ch.hk${url.path}"

            val thumbnailGlideUrl = GlideUrl(
                thumbnailUrl, LazyHeaders.Builder()
                    .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                            "_ga=GA1.2.57010468.1498700728; " +
                            "ageallow=1; " +
                            "_gid=GA1.2.1910512907.1585793763; " +
                            "_gat=1")
                    .build()
            )

            Glide.with(itemView)
                .load(thumbnailGlideUrl)
                .into(binding.ivFavoriteOpPic)

            binding.ivFavoriteOpPic.visibility = View.VISIBLE
            binding.ivFavoriteOpPic.setOnClickListener {
                listener.onThumbnailListener(fullPicUrl, url.duration, null)
            }

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

    fun setIntoThreadButton(listener: View.OnClickListener) {
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

}

