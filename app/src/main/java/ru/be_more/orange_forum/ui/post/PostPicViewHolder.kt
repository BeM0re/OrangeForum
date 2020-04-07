package ru.be_more.orange_forum.ui.post

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.ui.board.BoardOnClickListener


class PosPicViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var pic1: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic1)
    private var pic2: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic2)
    private var pics: LinearLayout = itemView!!.findViewById(R.id.ll_op_post_pics)

    fun setParentContainerGone(){
        pics.visibility = View.GONE
    }

    fun setPics (file1: AttachFile, file2: AttachFile? = null, listener: PostOnClickListener){
        //TODO перенести в константы
        var thumbnailUrl = "https://2ch.hk${file1.thumbnail}"
        var fullPicUrl = "https://2ch.hk${file1.path}"

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
        pic1.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file1.duration) }

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
            pic2.setOnClickListener { listener.onThumbnailListener(fullPicUrl, file2.duration) }
        }
        else
            pic2.visibility = View.GONE
    }

}

