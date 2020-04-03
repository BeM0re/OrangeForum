package ru.be_more.orange_forum.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_board.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.ui.category.*

//TODO сделать динамическое количество картинок через ресайклер
class BoardFragment private constructor(): MvpAppCompatFragment(),
    BoardOnClickListener,
    BoardView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var boardPresenter : BoardPresenter

    private lateinit var listener: (thread: BoardThread) -> Unit
    private lateinit var id: String
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : BoardAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardPresenter.setBoardId(id)
        recyclerView = rv_thread_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    override fun loadBoard(board: Board) {
        adapter = BoardAdapter(board.threads, this)

        recyclerView.adapter = adapter
    }

    companion object {
        fun getBoardFragment (listener: (thread: BoardThread) -> Unit, id: String): BoardFragment {
            var board = BoardFragment()
            board.listener = listener
            board.id = id

            return board
        }
    }

    override fun onThreadClick(thread: BoardThread) {
        listener(thread)
    }
}