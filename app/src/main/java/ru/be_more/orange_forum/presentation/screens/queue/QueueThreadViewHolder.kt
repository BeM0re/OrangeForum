package ru.be_more.orange_forum.presentation.screens.queue

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_op_post_short.*
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener

class QueueThreadViewHolder(itemView: View?, private var listener: PicOnClickListener) :
    ChildViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun setSenderName (param: String){
        tv_favorite_op_name.text = param
    }
    fun setIsOp (isOp: Boolean){
        if(isOp)
            tv_favorite_op_check.visibility=View.VISIBLE
        else
            tv_favorite_op_check.visibility=View.GONE
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

    fun setIntoThreadButton(listener: View.OnClickListener) {
        tv_favorite_op_subject.setOnClickListener(listener)
    }

/*    fun setRemoveButton(boardId: String, thread: BoardThread, listener: DownloadListener) {
        removeButton.text = "Удалить"
        removeButton.setOnClickListener {
            listener.onRemoveClick(boardId, thread.num)
        }
    }*/

    fun setDivider(){
        v_favorite_post1_pic_divider.visibility = View.VISIBLE
    }

}

