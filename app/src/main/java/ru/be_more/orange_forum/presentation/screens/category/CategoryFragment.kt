package ru.be_more.orange_forum.presentation.screens.category

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.work.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.NAVIGATION_BOARD_ID
import ru.be_more.orange_forum.consts.NAVIGATION_BOARD_NAME
import ru.be_more.orange_forum.consts.NAVIGATION_TITLE
import ru.be_more.orange_forum.databinding.FragmentCategoryBinding
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.interfaces.CategoryOnClickListener
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.worker.CheckFavoriteUpdateWorker
import java.util.concurrent.TimeUnit

class CategoryFragment:
    BaseFragment<FragmentCategoryBinding>(),
    CategoryOnClickListener {

    override val binding: FragmentCategoryBinding by viewBinding()
    private val viewModel: CategoryViewModel by inject()
    private lateinit var navController: NavController

    override fun onDestroyView() {
        binding.rvCategoryList.adapter = null
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        setSearchListener()
//        initRefreshFavWorker()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
    }

    private fun subscribe() =
        with(viewModel){
        }

    private fun setSearchListener(){

        binding.etBoardSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(query: Editable?) { viewModel.search(query.toString()) }

        })

        binding.ibBoardSearchClear.setOnClickListener {
            binding.etBoardSearch.setText("")
            binding.etBoardSearch.clearFocus()
//            viewModel.search("")
            hideKeyboard()
        }
    }

    private fun expandCategories() {
        (binding.rvCategoryList.adapter as? CategoryAdapter)
            ?.groups
            ?.forEachIndexed { index, _ ->
                if (!(binding.rvCategoryList.adapter as CategoryAdapter).isGroupExpanded(index))
                    (binding.rvCategoryList.adapter as CategoryAdapter).toggleGroup(index)
            }
    }

    private fun collapseCategories() {
        (binding.rvCategoryList.adapter as? CategoryAdapter)
            ?.groups
            ?.forEachIndexed { index, _ ->
                if ((binding.rvCategoryList.adapter as CategoryAdapter).isGroupExpanded(index))
                    (binding.rvCategoryList.adapter as CategoryAdapter).toggleGroup(index)
            }
    }

    private fun expandCategories(indices: List<Int>) {
        indices.forEach {
            (binding.rvCategoryList.adapter as? CategoryAdapter)?.toggleGroup(it)
        }
    }

    private fun loadCategories(categories: List<Category>) {
//        binding.rvCategoryList.adapter = CategoryAdapter(categories, this)
//            .apply {
//                setOnGroupClickListener {
//                    viewModel.categoryClicked(it)
//                    true
//                }
//            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRefreshFavWorker(){
        val refreshWorkRequest =
            PeriodicWorkRequestBuilder<CheckFavoriteUpdateWorker> (15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(requireContext())
            .enqueue(refreshWorkRequest)
    }

    override fun onBoardClick(boardId: String, boardTitle: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putString(NAVIGATION_BOARD_NAME, boardTitle)
        bundle.putString(NAVIGATION_TITLE, boardTitle)

        navController.navigate(R.id.action_categoryFragment_to_boardFragment, bundle)
    }
}