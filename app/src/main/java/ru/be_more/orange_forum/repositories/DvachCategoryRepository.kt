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
import java.lang.Thread.sleep
import kotlin.concurrent.thread


object DvachCategoryRepository {

    private val dvachCategoryService = ApiFactory.dvachApi
    private var isLoading : MutableLiveData<Boolean> = MutableLiveData()

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
                //TODO убрать слип на релизе
                withContext(Dispatchers.Default){
//                    sleep(1000)
                }

                val response = dvachCategoryService.getDvachCategoriesAsync("get_boards")

                if(response.isSuccessful)
                    allCategories = response.body()?: DvachCategories()
                else
                    Log.d("M_ParseItemRepository ",response.errorBody().toString())

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
        val politics = Category(
            title = "Политика",
            items = getBoards(allCategories.politics)
        )
        val custom = Category(
            title = "Пользовательские",
            items = getBoards(allCategories.custom)
        )
        val other = Category(
            title = "Разное",
            items = getBoards(allCategories.other)
        )
        val art = Category(
            title = "Творчество",
            items = getBoards(allCategories.art)
        )
        val thematics = Category(
            title = "Тематика",
            items = getBoards(allCategories.thematics)
        )
        val tech = Category(
            title = "Техника и софт",
            items = getBoards(allCategories.tech)
        )
        val japan = Category(
            title = "Японская культура",
            items = getBoards(allCategories.japan)
        )

        return listOf(adult, games, politics, custom, other, art, thematics, tech, japan)
    }

    private fun getBoards(dvachBoards : List<DvachBoard>) = dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoard) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )

}