package ru.be_more.orange_forum.ui.post

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.squareup.picasso.Picasso
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R


class PosPicViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var pic1: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic1)
    private var pic2: ImageView = itemView!!.findViewById(R.id.iv_op_post_pic2)

    fun setPics (url1: String, url2: String = ""){
        val url1 = "https://2ch.hk$url1"

        Log.d("M_PostPicViewHolder", "$url1")
        val glideUrl = GlideUrl(
            url1, LazyHeaders.Builder()
                .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                        "_ga=GA1.2.57010468.1498700728; " +
                        "ageallow=1; " +
                        "_gid=GA1.2.1910512907.1585793763; " +
                        "_gat=1")
                .build()
        )
        Glide.with(itemView)
            .load(glideUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(pic1)

        pic1.visibility = View.VISIBLE

        if(url2.isNotEmpty()){
            val url2 = "https://2ch.hk$url2"

            Log.d("M_PostPicViewHolder", "$url2")

            val glideUrl = GlideUrl(
                url2, LazyHeaders.Builder()
                    .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                            "_ga=GA1.2.57010468.1498700728; " +
                            "ageallow=1; " +
                            "_gid=GA1.2.1910512907.1585793763; " +
                            "_gat=1")
                    .build()
            )
            Glide.with(itemView)
                .load(glideUrl)
                .into(pic2)

            pic2.visibility = View.VISIBLE
        }
        else
            pic2.visibility = View.GONE
    }

}

