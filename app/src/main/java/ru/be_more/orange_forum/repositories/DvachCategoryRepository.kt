package ru.be_more.orange_forum.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.data.DvachBoard
import ru.be_more.orange_forum.data.DvachBoardName
import ru.be_more.orange_forum.data.DvachCategories
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.services.ApiFactory


object DvachCategoryRepository {

    private val dvachApi = ApiFactory.dvachApi
    private var isLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getCategories(): LiveData<List<Category>> {
        return Transformations.map(loadCategories()){ entity ->
            toCategories(entity)
        }
    }

    fun getBoard(boardId: String): LiveData<List<Category>> {
        return Transformations.map(loadBoard(boardId)){ entity ->
            toCategories(entity)
        }
    }

    fun getIsLoading() : LiveData<Boolean> = isLoading

    private fun loadCategories(): LiveData<DvachCategories> {
        var allCategories = DvachCategories()
        val liveData : MutableLiveData<DvachCategories> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            isLoading.postValue(true)

            try {
                val response = dvachApi.getDvachCategoriesAsync("get_boards")

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

    private fun getBoards(dvachBoards : List<DvachBoardName>) = dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoardName) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )

    private fun loadBoard(boardId: String): LiveData<DvachBoard> {
        var allThreads = DvachBoard()
        val liveData : MutableLiveData<DvachBoard> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            isLoading.postValue(true)

            try {
                val response = dvachApi.getDvachThreadsAsync(boardId)

                if(response.isSuccessful)
                    allThreads = response.body()?: DvachBoard()
                else
                    Log.d("M_ParseItemRepository ",response.errorBody().toString())

            }catch (e: Exception){
                Log.d("M_ParseItemRepository", "$e")
            }
            finally {
                isLoading.postValue(false)
            }

            liveData.postValue(allThreads)
        }
        return liveData
    }

}