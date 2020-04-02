package ru.be_more.orange_forum.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.repositories.DvachCategoryRepository


class CategoryFragment : MvpAppCompatFragment(), CategoryView {

    @InjectPresenter
    lateinit var categoryPresenter : CategoryPresenter

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onStart() {
        super.onStart()

        recyclerView = rv_category_list

        val layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager
    }

    override fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter
    }
}