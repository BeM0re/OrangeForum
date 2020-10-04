package ru.be_more.orange_forum.presentation.screens.post

import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_post_pics.*
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile

class PosPicViewHolder(itemView: View?) : ChildViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun setParentContainerGone(){
        ll_op_post_pics.visibility = View.GONE
    }

    fun setPics (file1: AttachFile, file2: AttachFile? = null, listener: PicOnClickListener){

        if (file1.localThumbnail.isEmpty()){

            //TODO перенести в константы
            var thumbnailUrl = "https://2ch.hk${file1.thumbnail}"
            val fullPicUrl = "https://2ch.hk${file1.path}"

            var thumbnailGlideUrl = GlideUrl(
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
                .into(iv_op_post_pic1)

            iv_op_post_pic1.visibility = View.VISIBLE
            iv_op_post_pic1.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file1.duration, null) }

            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                iv_play_1.visibility = View.VISIBLE
                iv_play_background_1.visibility = View.VISIBLE
            }
            else {
                iv_play_1.visibility = View.GONE
                iv_play_background_1.visibility = View.GONE
            }

            if(file2 != null){
                thumbnailUrl = "https://2ch.hk${file2.thumbnail}"
                val fullPicUrl = "https://2ch.hk${file2.path}"

                thumbnailGlideUrl = GlideUrl(
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
                    .into(iv_op_post_pic2)

                iv_op_post_pic2.visibility = View.VISIBLE
                iv_op_post_pic2.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file2.duration, null) }


                if (!file2.duration.isNullOrEmpty()) {
                    iv_play_2.visibility = View.VISIBLE
                    iv_play_background_2.visibility = View.VISIBLE
                }
                else {
                    iv_play_2.visibility = View.GONE
                    iv_play_background_2.visibility = View.GONE
                }
            }
            else
                iv_op_post_pic2.visibility = View.GONE

        }
        else{
//            Log.d("M_PostPicViewHolder", "file1 = $file1")

            Glide.with(itemView)
                .load(Uri.parse(file1.localThumbnail))
                .into(iv_op_post_pic1)

            iv_op_post_pic1.visibility = View.VISIBLE
            iv_op_post_pic1.setOnClickListener {
                listener.onThumbnailListener(
                    null , file1.duration, Uri.parse(file1.localPath))
            }

            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                iv_play_1.visibility = View.VISIBLE
                iv_play_background_1.visibility = View.VISIBLE
            }
            else {
                iv_play_1.visibility = View.GONE
                iv_play_background_1.visibility = View.GONE
            }

            if(file2 != null){
                Glide.with(itemView)
                    .load(Uri.parse(file2.localThumbnail))
                    .into(iv_op_post_pic2)

                iv_op_post_pic2.visibility = View.VISIBLE
                iv_op_post_pic2.setOnClickListener {
                    listener.onThumbnailListener(
                        null, file2.duration, Uri.parse(file2.localPath))
                }


                if (!file2.duration.isNullOrEmpty()) {
                    iv_play_2.visibility = View.VISIBLE
                    iv_play_background_2.visibility = View.VISIBLE
                }
                else {
                    iv_play_2.visibility = View.GONE
                    iv_play_background_2.visibility = View.GONE
                }
            }

        }


    }

}

