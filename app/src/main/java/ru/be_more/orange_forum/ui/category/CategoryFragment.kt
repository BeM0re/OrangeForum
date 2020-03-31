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
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.repositories.DvachCategoryRepository
import ru.be_more.orange_forum.ui.category.rv.CategoryAdapter


class CategoryFragment : Fragment() {

    private var repo = DvachCategoryRepository

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onStart() {
        super.onStart()


        var adapter : CategoryAdapter

        val recyclerView : RecyclerView = rv_category_list
        val layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager

        getParseData().observe(this, Observer {
            adapter = CategoryAdapter(it)
            Log.d("M_CategoryFragment", "${it.size}")
            recyclerView.adapter = adapter
        })

    }

    private fun loadDataAsync() : Deferred<LiveData<List<Category>>> = GlobalScope.async{
        repo.getItems()
    }

    private fun getParseData() : LiveData<List<Category>> = runBlocking {
        return@runBlocking  loadDataAsync().await()
    }
}