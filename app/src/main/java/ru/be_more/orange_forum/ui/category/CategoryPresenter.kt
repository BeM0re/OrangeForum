package ru.be_more.orange_forum.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.BoardShort
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.repositories.DvachCategoryRepository

@InjectViewState
class CategoryPresenter : MvpPresenter<CategoryView>() {

    private var repo = DvachCategoryRepository

    override fun onFirstViewAttach(){
        getParseData().observeForever( Observer {
            viewState.loadCategories(it)
        })
    }


    private fun getParseData() : LiveData<List<Category>> = runBlocking {
        return@runBlocking  loadDataAsync().await()
    }

    private fun loadDataAsync() : Deferred<LiveData<List<Category>>> = GlobalScope.async{
        repo.getCategories()
    }

    fun setSelectedBoard(board: BoardShort) {
//        Log.d("M_CategoryPresenter", "${board.id}")
    }

}