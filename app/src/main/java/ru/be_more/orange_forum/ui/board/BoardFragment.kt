package ru.be_more.orange_forum.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_category.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.ui.category.*


class BoardFragment private constructor(): MvpAppCompatFragment(),
    CategoryView, CategoryOnClickListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var boardPresenter : BoardPresenter

    private lateinit var listener: (thread: BoardThread) -> Unit
    private lateinit var id: String
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardPresenter.setBoardId(id)
        recyclerView = rv_board_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    override fun loadCategories(categories: List<Category>) {
        adapter = CategoryAdapter(categories, this)

        recyclerView.adapter = adapter
    }

    override fun onBoardClick(board: Board) {
        listener(board.threads[0])
    }

    companion object {
        fun getBoardFragment (listener: (thread: BoardThread) -> Unit, id: String): BoardFragment {
            var board = BoardFragment()
            board.listener = listener
            board.id = id

            return board
        }
    }
}