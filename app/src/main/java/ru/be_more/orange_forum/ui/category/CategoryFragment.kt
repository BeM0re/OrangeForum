package ru.be_more.orange_forum.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    lateinit var adapter : CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onDestroy() {
        categoryPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_category_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        categoryPresenter.initPresenter()
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
}