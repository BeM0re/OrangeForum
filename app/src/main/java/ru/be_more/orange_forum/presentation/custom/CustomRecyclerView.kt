package ru.be_more.orange_forum.presentation.custom

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.presentation.interfaces.CustomOnScrollListener

class CustomRecyclerView(context: Context, val listener: CustomOnScrollListener) : RecyclerView(context) {


    override fun onScrollStateChanged(state: Int){
        if (state == SCROLL_STATE_IDLE)
            listener.onScrollStop()
        else
            listener.onScrolling()
    }
}