package ru.be_more.orange_forum.ui.category

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.CategoryOnClickListener
import ru.be_more.orange_forum.domain.model.Category

class CategoryFragment private constructor(var onBoardClickListener: (boardId: String,
                                                                      boardTitle: String) -> Unit):
    Fragment(),
    CategoryView,
    CategoryOnClickListener {

    private val categoryPresenter: CategoryPresenter by inject(parameters = { parametersOf(this) })

    private lateinit var recyclerView : RecyclerView
    var adapter : CategoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onDestroy() {
        categoryPresenter.onDestroy()
        adapter = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_category_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        categoryPresenter.initPresenter()

        setSearchListener()
    }

    private fun setSearchListener(){
        tiet_board_search.addTextChangedListener(SearchTextWatcher { query ->
            categoryPresenter.search(query)
        })

        ib_board_search_clear.setOnClickListener {
            tiet_board_search.setText("")
            tiet_board_search.clearFocus()
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view?.windowToken, 0)
        }

    }

    override fun expandCategories() {
        for (i in (adapter?.groups?.size?:0 - 1) downTo 0) {
            if (! adapter!!.isGroupExpanded(i))
                adapter!!.toggleGroup(i)
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