package ru.be_more.orange_forum.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.model.*

interface PresentationContract {

    interface BaseViewModel{
        fun onDestroy()
    }

    interface ViewModelWithPosts{
        val post: LiveData<Post>
        val attachment: LiveData<Attachment>
        val emptyStack: LiveData<Boolean>
        fun clearStack()
        fun putContentInStack(modal: ModalContent)
        fun onBackPressed()
        fun getSinglePost(postNum: Int)
        fun getSinglePost(boardId: String, postNum: Int)
    }

    interface CategoryViewModel: BaseViewModel{
        val dataset: LiveData<List<Category>>
        val expand: LiveData<Boolean>
        val savedQuery: LiveData<String>
        fun initViewModel()
        fun saveQuery(query: String)
        fun saveExpanded(list: List<Int>)
        fun search(query: String)
    }

    interface BoardViewModel: BaseViewModel, ViewModelWithPosts{
        val board: LiveData<Board>
        val isFavorite: LiveData<Boolean>
        val savedPosition: LiveData<Int>
        fun init(boardId: String?, boardName: String?)
        fun hideThread(threadNum: Int, toHide: Boolean)
        fun setBoardMarks()
        fun getBoardId(): String?
        fun savePosition(pos: Int)
        fun setFavorite(isFavorite: Boolean)
    }

    interface ThreadViewModel: BaseViewModel, ViewModelWithPosts{
        val thread: LiveData<BoardThread>
        val savedPosition: LiveData<Int>
        fun init(boardId: String?, threadNum: Int)
        fun getPost(chanLink: Triple<String, Int, Int>)
        fun getPost(postNum: Int)
    }

    interface ResponseViewModel: BaseViewModel{
        val result: LiveData<String>
        fun postResponse(boardId: String, threadNum: Int, comment: String, token:String)
    }

    interface FavoriteViewModel: BaseViewModel{
        val boards: LiveData<List<Board>>
        fun init()
        fun refreshData()
    }

    interface DownloadViewModel: BaseViewModel{
        val boards: LiveData<List<Board>>
        fun init()
        fun removeThread(boardId: String, threadNum: Int)
    }

}