package ru.be_more.orange_forum.presentation.screens.post

import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.databinding.ItemPostPicsBinding
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.AttachedFile

class PostPicViewHolder(private val binding: ItemPostPicsBinding) : ChildViewHolder(binding.root) {

    fun setParentContainerGone(){
        binding.llOpPostPics.visibility = View.GONE
    }

    fun setPic (file1: AttachedFile, listener: PicOnClickListener){
        if (file1.localThumbnail.isNullOrEmpty()){

            //TODO перенести в константы
            val thumbnailUrl = "https://2ch.hk${file1.thumbnail}"
            val fullPicUrl = "https://2ch.hk${file1.path}"

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
                .into(binding.ivOpPostPic)

            binding.ivOpPostPic.visibility = View.VISIBLE
            binding.ivOpPostPic.setOnClickListener {
                listener.onThumbnailListener(fullPicUrl, file1.duration, null)
            }

            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                binding.ivPlay1.visibility = View.VISIBLE
                binding.ivPlayBackground1.visibility = View.VISIBLE
            }
            else {
                binding.ivPlay1.visibility = View.GONE
                binding.ivPlayBackground1.visibility = View.GONE
            }
        }
        else{
            Glide.with(itemView)
                .load(Uri.parse(file1.localThumbnail))
                .into(binding.ivOpPostPic)

            binding.ivOpPostPic.visibility = View.VISIBLE
            binding.ivOpPostPic.setOnClickListener {
                listener.onThumbnailListener(null , file1.duration, Uri.parse(file1.localPath))
            }
            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                binding.ivPlay1.visibility = View.VISIBLE
                binding.ivPlayBackground1.visibility = View.VISIBLE
            }
            else {
                binding.ivPlay1.visibility = View.GONE
                binding.ivPlayBackground1.visibility = View.GONE
            }
        }
    }
}

