package ru.be_more.orange_forum.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.be_more.orange_forum.data.DvachBoard
import ru.be_more.orange_forum.data.DvachCategories
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.services.ApiFactory
import java.lang.Exception

object dvachCategoryRepository {

    private val dvachCategoryService = ApiFactory.dvachApi
    private var isLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getItems(): LiveData<List<Category>> {

        val response = MutableLiveData<List<Category>> ()
        response.postValue(toCategories(loadData().value))

        return response
    }

    fun getIsLoading() : LiveData<Boolean> = isLoading

    private fun loadData(): LiveData<DvachCategories> {
        var allCategories : DvachCategories = DvachCategories()
        val liveData : MutableLiveData<DvachCategories> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            withContext(Dispatchers.Main) {
                isLoading.value = true
            }

            val dvachCategoryRequest = dvachCategoryService.getBashQuotesAsync()

            try {

                val response = dvachCategoryRequest.await()
                if(response.isSuccessful){
                    val dvachCategoryResponse = response.body()
                    allCategories = dvachCategoryResponse?: DvachCategories()

                }else{
                    Log.d("M_ParseItemRepository ",response.errorBody().toString())
                }
            }catch (e: Exception){
                Log.d("M_ParseItemRepository", "$e")
            }
            finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }

            withContext(Dispatchers.Main) {
                liveData.value = allCategories
            }

        }
        return liveData
    }

    private fun toCategories (allCategories: DvachCategories?) : List<Category> {

        if(allCategories == null)
            return listOf()


        var adult = Category(
            title = "Взрослым",
            items = getBoards(allCategories.Взрослым)
        )
        var games = Category(
            title = "Игры",
            items = getBoards(allCategories.Игры)
        )

        return listOf()
    }

    private fun getBoards(dvachBoards : List<DvachBoard>) = dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoard) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )


}