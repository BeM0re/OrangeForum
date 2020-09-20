package ru.be_more.orange_forum.ui.category

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.interfaces.CategoryOnClickListener
import java.util.*

/*class CategoryFragment private constructor(var onBoardClickListener: (boardId: String,
                                                                      boardTitle: String) -> Unit):*/
class CategoryFragment:
    Fragment(),
    CategoryOnClickListener {

//    private val categoryPresenter: CategoryPresenter by inject(parameters = { parametersOf(this) })
    private val viewModel: CategoryViewModel by inject()

    private var recyclerView : RecyclerView? = null
    var adapter : CategoryAdapter? = null
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onDestroyView() {
        saveState()
        super.onDestroyView()
    }

    override fun onDestroy() {
        adapter = null
        recyclerView = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        recyclerView = rv_category_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)

//        categoryPresenter.initPresenter(this)
        subscribe()
        viewModel.initViewModel()
        setSearchListener()
    }

    private fun subscribe() =
        with(viewModel){
            observe(dataset, ::loadCategories)
            observe(expand) {
                if (it)
                    expandCategories()
                else
                    collapseCategories()
            }
            observe(savedQuery) {
                tiet_board_search.setText(it)
            }
        }

    private fun saveState() {
        val list = LinkedList<Int>()
//        for (i in 0 until (adapter?.groups?.size?:0)){
//            if (adapter?.isGroupExpanded(i) == true){
//                list.add(i)
//            }
//        }
//        viewModel.saveExpanded(list)
        viewModel.saveQuery(tiet_board_search.text.toString())
    }

    private fun setSearchListener(){
        tiet_board_search.addTextChangedListener(SearchTextWatcher { query ->
            viewModel.search(query)
        })

        ib_board_search_clear.setOnClickListener {
            tiet_board_search.setText("")
            tiet_board_search.clearFocus()
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    private fun expandCategories() {
        for (i in ((adapter?.groups?.size?:0) - 1) downTo 0) {
            if (! adapter!!.isGroupExpanded(i))
                adapter!!.toggleGroup(i)
        }
    }

    private fun collapseCategories() {
        for (i in ((adapter?.groups?.size?:0) - 1) downTo 0) {
            if (adapter!!.isGroupExpanded(i))
                adapter!!.toggleGroup(i)
        }
    }

    fun restoreState(expandedItems: List<Int>, savedQuery: String) {
        for (i in expandedItems) {
            adapter!!.toggleGroup(i)
        }
        tiet_board_search.setText(savedQuery)
    }

    private fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories, this)

        recyclerView?.adapter = adapter

    }

    override fun onBoardClick(boardId: String, boardTitle: String) {
        val bundle = Bundle()
        bundle.putString("boardId", boardId)
        bundle.putString("title", boardTitle)
        navController.navigate(R.id.action_categoryFragment_to_boardFragment, bundle)
    }

    class SearchTextWatcher(val listener: ((String) -> Unit)): TextWatcher{

        override fun afterTextChanged(query: Editable) {
            listener(query.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int){}
    }
}