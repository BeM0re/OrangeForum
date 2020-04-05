package ru.be_more.orange_forum.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.*
import ru.be_more.orange_forum.services.ApiFactory


object DvachApiRepository {

    private val dvachApi = ApiFactory.dvachApi
    private var isLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getIsLoading() : LiveData<Boolean> = isLoading

    fun getCategories(): LiveData<List<Category>> {
        return Transformations.map(loadCategories()){ entity ->
            toCategories(entity)
        }
    }

    fun getBoard(board: Board): LiveData<List<BoardThread>> {
        return Transformations.map(loadBoard(board)){ entity ->
            toBoard(entity)
        }
    }

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

    private fun loadBoard(board: Board): LiveData<DvachBoard> {
        var allThreads = DvachBoard()
        val liveData : MutableLiveData<DvachBoard> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            isLoading.postValue(true)

            try {
                val response = dvachApi.getDvachThreadsAsync(board.id)

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


    private fun toCategories (allCategories: DvachCategories?) : List<Category> {

        if(allCategories == null)
            return listOf()

        val adult = Category(
            title = "Взрослым",
            items = getBoardNames(allCategories.adult)
        )
        val games = Category(
            title = "Игры",
            items = getBoardNames(allCategories.games)
        )
        val politics = Category(
            title = "Политика",
            items = getBoardNames(allCategories.politics)
        )
        val custom = Category(
            title = "Пользовательские",
            items = getBoardNames(allCategories.custom)
        )
        val other = Category(
            title = "Разное",
            items = getBoardNames(allCategories.other)
        )
        val art = Category(
            title = "Творчество",
            items = getBoardNames(allCategories.art)
        )
        val thematics = Category(
            title = "Тематика",
            items = getBoardNames(allCategories.thematics)
        )
        val tech = Category(
            title = "Техника и софт",
            items = getBoardNames(allCategories.tech)
        )
        val japan = Category(
            title = "Японская культура",
            items = getBoardNames(allCategories.japan)
        )

        return listOf(adult, games, politics, custom, other, art, thematics, tech, japan)
    }

    private fun getBoardNames(dvachBoards : List<DvachBoardName>) = dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoardName) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )

    private fun toBoard(dvachBoard: DvachBoard)
            = dvachBoard.threads.map { toThread(it) }


        //TODO перенести в toPost
    private fun toThread(dvachThread: DvachThread) = BoardThread(
        num = dvachThread.num,
        posts = listOf(
            Post(
                num = dvachThread.num,
                name = dvachThread.name,
                comment = dvachThread.comment,
                date = dvachThread.date,
                email = dvachThread.email,
                files = dvachThread.files.map { toFiles(it) },
                files_count = dvachThread.files_count,
                op = dvachThread.op,
                posts_count = dvachThread.posts_count,
                subject = dvachThread.subject,
                timestamp = dvachThread.timestamp
            )
        )
    )

    private fun toFiles (file: DvachFile) = AttachFile(
        path = file.path,
        thumbnail = file.thumbnail,
        duration = if(file.duration.isNullOrEmpty()) "" else file.duration
    )


}