package ru.be_more.orange_forum.presentation.screens.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.AttachFile


class PostPicAdapter( var files: List<AttachFile> = listOf(), var listener: PicOnClickListener) :
    RecyclerView.Adapter<PosPicViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosPicViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PosPicViewHolder(inflater.inflate(R.layout.item_post_pics, parent, false))
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: PosPicViewHolder, position: Int) {

        if(files.isNotEmpty()) {
            holder.setPic(files[position], listener)
            holder.itemView.visibility = View.VISIBLE
        }
        else {
            holder.itemView.visibility = View.GONE
            holder.setParentContainerGone()
        }
    }

    /*fun updateData(data:List<AttachFile>){

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
    }*/
}


