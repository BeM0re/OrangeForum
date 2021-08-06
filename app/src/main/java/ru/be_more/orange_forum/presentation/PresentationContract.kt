package ru.be_more.orange_forum.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import ru.be_more.orange_forum.domain.model.*

interface PresentationContract {

    interface BaseViewModel{
        val error: LiveData<String>
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
        val expand: LiveData<List<Int>>
        val savedQuery: LiveData<String>
        fun initViewModel()
        fun search(query: String)
        fun categoryClicked(index: Int)
    }

    interface BoardViewModel: BaseViewModel, ViewModelWithPosts{
        val board: LiveData<Board>
        val isFavorite: LiveData<Boolean>
        val savedPosition: LiveData<Int>
        fun init(boardId: String?, boardName: String?)
        fun hideThread(threadNum: Int, toHide: Boolean)
        fun setBoardMarks()
        @Deprecated("") fun getBoardId(): String?
        fun savePosition(pos: Int)
        fun setFavorite(isFavorite: Boolean)
        fun getBoardName(): String
        fun addToQueue(threadNum: Int)
        fun onMenuReady()
        fun prepareModal(fullPicUrl: String?, duration: String?, fullPicUri: Uri?)
        fun linkClicked(chanLink: Triple<String, Int, Int>?)
    }

    interface ThreadViewModel: BaseViewModel, ViewModelWithPosts{
        val thread: LiveData<BoardThread>
        val savedPosition: LiveData<Int>
        val isFavorite: LiveData<Boolean>
        val isQueued: LiveData<Boolean>
        val isDownload: LiveData<Boolean>
        val isRefreshing: LiveData<Boolean>
        fun init(boardId: String?, threadNum: Int, boardName: String)
        fun getPost(chanLink: Triple<String, Int, Int>?)
        fun getPost(postNum: Int)
        fun addToQueue(isQueued: Boolean)
        fun setFavorite(isFavorite: Boolean)
        fun download(isDownload: Boolean)
        fun onMenuReady()
        fun closeModal()
        fun prepareModal(fullPicUrl: String?, duration: String?, fullPicUri: Uri?)
        fun onRefresh()
    }

    interface ResponseViewModel: BaseViewModel{
        val result: LiveData<String>
        fun postResponse(boardId: String, threadNum: Int, comment: String, token:String)
    }

    interface QueueViewModel: BaseViewModel{
        val boards: LiveData<List<Board>>
        fun init()
        fun removeThread(boardId: String, threadNum: Int)
    }

    interface DownFavViewModel: BaseViewModel{
        val boards: LiveData<List<Board>>
        fun init()
        fun removeThread(boardId: String, threadNum: Int)
    }

}