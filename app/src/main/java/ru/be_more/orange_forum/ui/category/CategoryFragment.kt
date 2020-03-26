package ru.be_more.orange_forum.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board

class CategoryFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onStart() {

        super.onStart()

        var boards : List<Board> = listOf(Board("asd"),Board("zxc"))
        var cat1 = Category("name1", boards)
        var cat2 = Category("name2", boards)

        val categories : List<Category>  = listOf(cat1, cat2)
        val recyclerView : RecyclerView = rv_category_list
        val layoutManager : LinearLayoutManager  = LinearLayoutManager(this.context)

        //instantiate your adapter with the list of genres
        val adapter : CategoryAdapter  = CategoryAdapter (categories)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}