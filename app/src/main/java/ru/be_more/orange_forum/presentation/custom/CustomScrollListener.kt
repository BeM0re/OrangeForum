package ru.be_more.orange_forum.presentation.custom

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.be_more.orange_forum.presentation.interfaces.CustomOnScrollListener


class CustomScrollListener(var listener: CustomOnScrollListener) : RecyclerView.OnScrollListener(),
    View.OnScrollChangeListener {
    override fun onScrollStateChanged(
        recyclerView: RecyclerView,
        newState: Int
    ) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> listener.onScrollStop()
            RecyclerView.SCROLL_STATE_DRAGGING -> listener.onScrolling()
            RecyclerView.SCROLL_STATE_SETTLING -> listener.onScrolling()
        }
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {

    }


}