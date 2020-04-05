package ru.be_more.orange_forum.ui.post

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.AttachFile
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.board.BoardOnClickListener


class PostPicAdapter( var files: List<AttachFile> = listOf(), var listener: BoardOnClickListener? = null) :
    RecyclerView.Adapter<PosPicViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosPicViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PosPicViewHolder(inflater.inflate(R.layout.item_post_pics, parent, false))
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: PosPicViewHolder, position: Int) {

        when {
            files.size > position*2+1 ->
                holder.setPics(files[position*2].thumbnail, files[position*2+1].thumbnail)
            files.size == position*2+1 ->
                holder.setPics(files[position*2].thumbnail)
            else -> ""
//                holder.itemView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { Log.d("M_PostPicAdapter", "Click on ${holder.itemView}")}
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


