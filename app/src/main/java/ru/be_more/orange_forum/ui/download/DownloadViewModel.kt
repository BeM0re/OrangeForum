package ru.be_more.orange_forum.ui.download

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.PresentationContract
import java.util.*

class DownloadViewModelImpl (
    private val downloadInteractor : InteractorContract.DownloadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor
): PresentationContract.DownloadViewModel {

    override val boards = MutableLiveData<List<Board>>()

    @SuppressLint("CheckResult")
    override fun init(){
        downloadInteractor.getDownloads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownloadPresenter", "Presenter on first view attach error = $it") }
            )
    }

    override fun onDestroy() {
        downloadInteractor.release()
        postInteractor.release()
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor.deleteThread(boardId, threadNum)
    }
}