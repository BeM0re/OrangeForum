package ru.be_more.orange_forum.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.be_more.orange_forum.data.DvachBoard
import ru.be_more.orange_forum.data.DvachCategories
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.services.ApiFactory


object dvachCategoryRepository {

    private val dvachCategoryService = ApiFactory.dvachApi
    private var isLoading : MutableLiveData<Boolean> = MutableLiveData()

/*    fun getItems(): LiveData<List<Category>> {

        val response = MutableLiveData<List<Category>> ()
        response.postValue(toCategories(loadData().value))


        return response
    }*/
    fun getItems(): LiveData<List<Category>> {
        return Transformations.map(loadData()){ entity ->
            toCategories(entity)
        }
    }

    fun getIsLoading() : LiveData<Boolean> = isLoading

    private fun loadData(): LiveData<DvachCategories> {
        var allCategories = DvachCategories()
        val liveData : MutableLiveData<DvachCategories> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            isLoading.postValue(true)

            try {
                withContext(Dispatchers.Main) {
//                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val response = dvachCategoryService.getDvachCategoriesAsync("get_boards")
//                    val formattedResponse: DvachCategories = gson.fromJson(response.body().toString(), DvachCategories::class.java)
                    Log.d("M_ParseItemRepository ","response = $response")

                if(response.isSuccessful){
                    val dvachCategoryResponse = response.body()
                        allCategories = dvachCategoryResponse?: DvachCategories()
                    Log.d("M_ParseItemRepository ","response.body = ${response.body().toString()}")

                }else{
                    Log.d("M_ParseItemRepository ",response.errorBody().toString())
                }

                }
            }catch (e: Exception){
                Log.d("M_ParseItemRepository", "$e")
            }
            finally {
                isLoading.postValue(false)
            }

            liveData.postValue(allCategories)
        }
        return liveData
    }

    private fun toCategories (allCategories: DvachCategories?) : List<Category> {

        if(allCategories == null)
            return listOf()


        val adult = Category(
            title = "Взрослым",
            items = getBoards(allCategories.adult)
        )
        val games = Category(
            title = "Игры",
            items = getBoards(allCategories.games)
        )

        return listOf(adult, games)
    }

    private fun getBoards(dvachBoards : List<DvachBoard>) = dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoard) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )


}