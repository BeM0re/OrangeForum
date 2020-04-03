package ru.be_more.orange_forum.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class BoardPresenter : MvpPresenter<BoardView>() {

    private var repo = DvachApiRepository
    private lateinit var board :Board

    override fun onFirstViewAttach(){

    }

    private fun getParseData() : LiveData<List<BoardThread>> = runBlocking {
        return@runBlocking  repo.getBoard(board)
    }

    fun setBoardId(id: String){
        board.id = id

        getParseData().observeForever( Observer {
            board.threads=it
            viewState.loadBoard(board)
        })
    }
    fun setSelectedBoard(board: Board) {
//        Log.d("M_CategoryPresenter", "${board.id}")
    }

}