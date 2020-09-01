package ru.be_more.orange_forum.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.CategoryOnClickListener
import ru.be_more.orange_forum.model.Category


class CategoryFragment private constructor(var onBoardClickListener: (boardId: String,
                                                                      boardTitle: String) -> Unit):
    MvpAppCompatFragment(),
    CategoryView,
    CategoryOnClickListener {

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

        setSearchListener()
    }

    private fun setSearchListener(){
        tiet_board_search.addTextChangedListener(SearchTextWatcher { query ->
            categoryPresenter.search(query)
        })
    }

    override fun expandCategories() {
        for (i in (adapter.groups.size - 1) downTo 0) {
            if (! adapter.isGroupExpanded(i))
                adapter.toggleGroup(i)
        }
    }

    override fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories, this)

        recyclerView.adapter = adapter
    }

    override fun onBoardClick(boardId: String, boardTitle: String) {
        onBoardClickListener(boardId, boardTitle)
    }

    companion object {
        fun getCategoryFragment (onBoardClickListener: (boardId: String, boardTitle: String) -> Unit): CategoryFragment {
            return CategoryFragment(onBoardClickListener)
        }
    }

    class SearchTextWatcher(val listener: ((String) -> Unit)): TextWatcher{

        override fun afterTextChanged(query: Editable ) {
            listener(query.toString())
        }

        override fun beforeTextChanged(s: CharSequence , start: Int , count: Int , after: Int) {}

        override fun onTextChanged(s: CharSequence , start: Int , count: Int , after: Int){}
    }
}