package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_op_post_short.*
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener

class DownFavThreadViewHolder(itemView: View?, private var listener: PicOnClickListener) :
    ChildViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    fun setSenderName (param: String){
        tv_favorite_op_name.text = param
    }
    fun setDate (param: String){
        tv_favorite_op_datetime.text = param
    }
    fun setThreadNum (param: Int){
        tv_favorite_op_num.text = param.toString()
    }
    fun setTitle (param: String){
        tv_favorite_op_subject.text = param
    }

    fun setPics (url: AttachFile?){
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
                .into(iv_favorite_op_pic)

            iv_favorite_op_pic.visibility = View.VISIBLE
            iv_favorite_op_pic.setOnClickListener {
                listener.onThumbnailListener(fullPicUrl, url.duration, null) }

            //нужно именно .isNullOrEmpty
            if (!url.duration.isNullOrEmpty()){
                iv_favorite_play_background.visibility = View.VISIBLE
                iv_favorite_play.visibility = View.VISIBLE
            }
            else {
                iv_favorite_play_background.visibility = View.GONE
                iv_favorite_play.visibility = View.GONE
            }

            iv_favorite_op_pic.visibility = View.VISIBLE
        }
        else{
            iv_favorite_op_pic.visibility = View.GONE
        }
    }

    fun setCommentListener(listener: View.OnClickListener){
        cl_favorite_op_post.setOnClickListener(listener)
    }

    fun setRemoveButton(boardId: String, thread: BoardThread, listener: DownFavListener) {
        iv_favorite_close_button.setOnClickListener {
            listener.onRemoveClick(boardId, thread.num)
        }
    }

    fun setDivider(){
        v_favorite_post1_pic_divider.visibility = View.VISIBLE
    }

    fun setIcon(isFavorite: Boolean, isDownloaded: Boolean){
        ic_short_download.visibility = if (isDownloaded) View.VISIBLE else View.GONE
        ic_short_favorite.visibility = if (isFavorite) View.VISIBLE else View.GONE
    }

}

