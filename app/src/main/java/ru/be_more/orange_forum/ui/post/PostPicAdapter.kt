package ru.be_more.orange_forum.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.AttachFile


class PostPicAdapter( var files: List<AttachFile> = listOf(), var listener: PicOnClickListener) :
    RecyclerView.Adapter<PosPicViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosPicViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PosPicViewHolder(inflater.inflate(R.layout.item_post_pics, parent, false))
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: PosPicViewHolder, position: Int) {

        when {
            //Один вью холдер содержит 2 картинки, поэтому приходится извращаться
            //и передавать по 2 или по 1 файлу за раз
            //TODO отрефакторить: изменить модель, чтобы картинки хранились парами
            files.size > position * 2 + 1 -> {
                holder.setPics(files[position * 2], files[position * 2 + 1], listener)
                holder.itemView.visibility = View.VISIBLE
            }
            files.size == position * 2 + 1 -> {
                holder.setPics(files[position * 2], listener = listener)
                holder.itemView.visibility = View.VISIBLE
            }
            else -> {
                holder.itemView.visibility = View.GONE
                holder.setParentContainerGone()
            }
        }

        //хз что это, может потом вспомню зачем это начал делать
//        holder.itemView.setOnClickListener { Log.d("M_PostPicAdapter", "Click on ${holder.itemView}")}
    }

    fun updateData(data:List<AttachFile>){

        val diffCallback = object: DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                files[oldPos].path == data[newPos].path

            override fun getOldListSize(): Int = files.size

            override fun getNewListSize(): Int  = data.size

            override fun areContentsTheSame(oldPos: Int, newPos: Int) : Boolean =
                files[oldPos].hashCode() == data[newPos].hashCode()

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        files = data
        diffResult.dispatchUpdatesTo(this)
    }
}


