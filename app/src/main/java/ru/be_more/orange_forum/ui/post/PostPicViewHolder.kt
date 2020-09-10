package ru.be_more.orange_forum.ui.post

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile


class PosPicViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var pic1: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic1)
    private var pic2: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic2)
    private var pics: ConstraintLayout = itemView!!.findViewById(R.id.ll_op_post_pics)
    private var videoIndicator1: ImageView = itemView!!.findViewById(R.id.iv_play_1)
    private var videoIndicator2: ImageView = itemView!!.findViewById(R.id.iv_play_2)
    private var videoIndicatorBg1: ImageView = itemView!!.findViewById(R.id.iv_play_background_1)
    private var videoIndicatorBg2: ImageView = itemView!!.findViewById(R.id.iv_play_background_2)


    fun setParentContainerGone(){
        pics.visibility = View.GONE
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
                .into(pic1)

            pic1.visibility = View.VISIBLE
            pic1.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file1.duration, null) }

            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                videoIndicator1.visibility = View.VISIBLE
                videoIndicatorBg1.visibility = View.VISIBLE
            }
            else {
                videoIndicator1.visibility = View.GONE
                videoIndicatorBg1.visibility = View.GONE
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
                    .into(pic2)

                pic2.visibility = View.VISIBLE
                pic2.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file2.duration, null) }


                if (!file2.duration.isNullOrEmpty()) {
                    videoIndicator2.visibility = View.VISIBLE
                    videoIndicatorBg2.visibility = View.VISIBLE
                }
                else {
                    videoIndicator2.visibility = View.GONE
                    videoIndicatorBg2.visibility = View.GONE
                }
            }
            else
                pic2.visibility = View.GONE

        }
        else{
//            Log.d("M_PostPicViewHolder", "file1 = $file1")

            Glide.with(itemView)
                .load(Uri.parse(file1.localThumbnail))
                .into(pic1)

            pic1.visibility = View.VISIBLE
            pic1.setOnClickListener {
                listener.onThumbnailListener(
                    null , file1.duration, Uri.parse(file1.localPath))
            }

            //нужно именно .isNullOrEmpty
            if (!file1.duration.isNullOrEmpty()){
                videoIndicator1.visibility = View.VISIBLE
                videoIndicatorBg1.visibility = View.VISIBLE
            }
            else {
                videoIndicator1.visibility = View.GONE
                videoIndicatorBg1.visibility = View.GONE
            }

            if(file2 != null){
                Glide.with(itemView)
                    .load(Uri.parse(file2.localThumbnail))
                    .into(pic2)

                pic2.visibility = View.VISIBLE
                pic2.setOnClickListener {
                    listener.onThumbnailListener(
                        null, file2.duration, Uri.parse(file2.localPath))
                }


                if (!file2.duration.isNullOrEmpty()) {
                    videoIndicator2.visibility = View.VISIBLE
                    videoIndicatorBg2.visibility = View.VISIBLE
                }
                else {
                    videoIndicator2.visibility = View.GONE
                    videoIndicatorBg2.visibility = View.GONE
                }
            }

        }


    }

}

