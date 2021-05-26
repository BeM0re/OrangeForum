package ru.be_more.orange_forum.presentation.screens.category

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
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.NAVIGATION_BOARD_ID
import ru.be_more.orange_forum.consts.NAVIGATION_BOARD_NAME
import ru.be_more.orange_forum.consts.NAVIGATION_TITLE
import ru.be_more.orange_forum.databinding.FragmentBoardBinding
import ru.be_more.orange_forum.databinding.FragmentCategoryBinding
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.interfaces.CategoryOnClickListener
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment

class CategoryFragment:
    BaseFragment<FragmentCategoryBinding>(),
    CategoryOnClickListener {

    override val binding: FragmentCategoryBinding by viewBinding()
    private val viewModel: PresentationContract.CategoryViewModel by inject()
    private lateinit var navController: NavController
    var adapter : CategoryAdapter? = null

    override fun onDestroyView() {
        saveState()
        adapter = null
        binding.rvCategoryList.adapter = null
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        viewModel.initViewModel()
        setSearchListener()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
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
                binding.etBoardSearch.setText(it)
            }
        }

    private fun saveState() {
//        val list = LinkedList<Int>()
//        for (i in 0 until (adapter?.groups?.size?:0)){
//            if (adapter?.isGroupExpanded(i) == true){
//                list.add(i)
//            }
//        }
//        viewModel.saveExpanded(list)
        viewModel.saveQuery(binding.etBoardSearch.text.toString())
    }

    private fun setSearchListener(){
        binding.etBoardSearch.addTextChangedListener(SearchTextWatcher { query ->
            viewModel.search(query)
        })

        binding.ibBoardSearchClear.setOnClickListener {
            binding.etBoardSearch.setText("")
            binding.etBoardSearch.clearFocus()
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
        expandedItems.forEach {
            adapter?.toggleGroup(it)
        }
        binding.etBoardSearch.setText(savedQuery)
    }

    private fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories, this)

        binding.rvCategoryList.adapter = adapter
    }

    override fun onBoardClick(boardId: String, boardTitle: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putString(NAVIGATION_BOARD_NAME, boardTitle)
        bundle.putString(NAVIGATION_TITLE, boardTitle)

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