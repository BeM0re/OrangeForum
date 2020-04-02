package ru.be_more.orange_forum.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.BoardShort
import ru.be_more.orange_forum.model.Category


class CategoryFragment private constructor(var listener: (board: BoardShort) -> Unit): MvpAppCompatFragment(), CategoryView, CategoryOnClickListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var categoryPresenter : CategoryPresenter

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_category_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    override fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories, this)

        recyclerView.adapter = adapter
    }

    override fun onBoardClick(board: BoardShort) {
        listener(board)
    }

    companion object {
        fun getCategoryFragment (listener: (board: BoardShort) -> Unit): CategoryFragment {
            return CategoryFragment(listener)
        }
    }
}