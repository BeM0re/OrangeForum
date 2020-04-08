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

    fun getThread(board: Board, threadNum: Int): LiveData<BoardThread> {
        return Transformations.map(loadThread(board, threadNum)){ entity ->
            toThread(entity, threadNum)
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
                    Log.d("M_DvachApiRepository ",response.errorBody().toString())

            }catch (e: Exception){
                Log.d("M_DvachApiRepository", "$e")
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
                    allThreads = response.body() ?: DvachBoard()
                else
                    Log.d("M_DvachApiRepository ",response.errorBody().toString())

            }catch (e: Exception){
                Log.d("M_DvachApiRepository", "$e")
            }
            finally {
                isLoading.postValue(false)
            }

            liveData.postValue(allThreads)
        }
        return liveData
    }

    private fun loadThread(board: Board, threadNum: Int): LiveData<DvachThread> {
        var allPosts = DvachThread()
        val liveData : MutableLiveData<DvachThread> = MutableLiveData()

        GlobalScope.launch(Dispatchers.Default) {

            isLoading.postValue(true)

            try {
                val cookie = "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; _ga=GA1.2.57010468.1498700728; ageallow=1; _gid=GA1.2.1910512907.1585793763; _gat=1"
                val response = dvachApi.getDvachPostsAsync(board.id, threadNum, cookie)

                if (response.isSuccessful)
                    allPosts = response.body() ?: DvachThread()
                else
                    Log.d("M_DvachApiRepository ",response.errorBody().toString())

            }catch (e: Exception){
                Log.d("M_DvachApiRepository", "Get thread error = $e")
            }
            finally {
                isLoading.postValue(false)
            }

            liveData.postValue(allPosts)
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

    private fun toThread(dvachOpPost: DvachPost) = BoardThread(
        num = dvachOpPost.num,
        posts = listOf(toPost(dvachOpPost))
    )
    private fun toThread(dvachThread: DvachThread, threadNum: Int) = BoardThread(
        num = threadNum,
        posts = dvachThread.threads[0].posts.map { toPost(it) },
        title = dvachThread.title
    )

    private fun toPost(post: DvachPost) = Post(
        num = post.num,
        name = post.name,
        comment = post.comment,
        date = post.date,
        email = post.email,
        files = post.files.map { toFiles(it) },
        files_count = post.files_count,
        op = post.op,
        posts_count = post.posts_count,
        subject = post.subject,
        timestamp = post.timestamp,
        number = post.number
    )

    private fun toFiles (file: DvachFile) = AttachFile(
        path = file.path,
        thumbnail = file.thumbnail,
        duration = if(file.duration.isNullOrEmpty()) "" else file.duration
    )


}