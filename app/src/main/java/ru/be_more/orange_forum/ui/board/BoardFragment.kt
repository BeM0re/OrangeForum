package ru.be_more.orange_forum.ui.board

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_board.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.BOARD_TAG
import ru.be_more.orange_forum.consts.POST_IN_BOARD_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostFragment

//TODO сделать динамическое количество картинок через ресайклер
class BoardFragment: Fragment(),
    BoardView,
    BoardOnClickListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val boardPresenter: BoardPresenter by inject(parameters = { parametersOf(this) })

//    private var listener: ((Int, String) -> Unit)? = null
    private var id: String = ""
    private var recyclerView : RecyclerView? = null
    private var adapter : BoardAdapter? = null
    private var postFragment: PostFragment? = null
    private lateinit var navController: NavController

    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? =
        inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNav(view)
        subscribe()
    }

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        postFragment = null
        adapter = null
        recyclerView = null
        super.onDestroy()
    }

    override fun loadBoard(board: Board) {
        adapter = BoardAdapter(
            board.threads, this, this, this
        )

        recyclerView?.adapter = adapter
        recyclerView?.addItemDecoration(
            DividerItemDecoration(recyclerView?.context, HORIZONTAL)
        )
    }

    override fun setBoardMarks(isFavorite: Boolean) {
        if (isFavorite)
            App.getBus().onNext(Pair(FavoriteBoardEntered, ""))
        else
            App.getBus().onNext(Pair(UnfavoriteBoardEntered, ""))
    }

    override fun onIntoThreadClick(threadNum: Int, threadTitle: String) {
        val bundle = Bundle()
        bundle.putString("boardId", boardPresenter.getBoardId())
        bundle.putInt("threadNum", threadNum)
        bundle.putString("title", threadTitle)
        navController.navigate(R.id.action_boardFragment_to_threadFragment, bundle)

//        if (boardPresenter.listener != null)
//            boardPresenter.listener!!(threadNum, threadTitle)
    }

    override fun onHideClick(threadNum: Int, toHide: Boolean) {
        boardPresenter.hideThread(threadNum, toHide)
        adapter?.notifyDataSetChanged()
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            boardPresenter.putContentInStack(attachment)
            showPic(attachment)
            fl_board_post.visibility = View.VISIBLE
        }

    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            showToast("Пост не найден")
        else
            boardPresenter.getSinglePost(chanLink.first, chanLink.third)
    }

    override fun onLinkClick(postNum: Int) {
        boardPresenter.getSinglePost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun showPic(attachment: Attachment){
        postFragment = PostFragment.getPostFragment(
            attachment, this, this, this
        )

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, postFragment!!, POST_IN_BOARD_TAG)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post, this, this, this
        )

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, postFragment!!, POST_IN_BOARD_TAG)
            ?.commit()
    }

    override fun hideModal() {
        fl_board_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_BOARD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_BOARD_TAG)!!)

        boardPresenter.clearStack()
    }

    override fun onCloseModalListener(){
        postFragment = null
        hideModal()
    }

    override fun showToast(message: String) {
        Toast.makeText(App.applicationContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun subscribe(){
        disposable = App.getBus().subscribe({
            if (it.first is BackPressed && it.second == BOARD_TAG) {
                if (fl_board_post.visibility != View.GONE)
                    boardPresenter.onBackPressed()
                else
                    App.getBus().onNext(Pair(AppToBeClosed, ""))
            }
            if (it.first is BoardEntered && it.second == BOARD_TAG)
                boardPresenter.setBoardMarks()
        },
            {
                Log.e("M_BoardFragment", "bus error = \n $it")
            })
    }

    private fun initNav(view: View){
        navController = Navigation.findNavController(view)

        val boardId = requireArguments().getString("boardId")

        boardPresenter.init(boardId)
        recyclerView = rv_thread_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)

        App.getBus().onNext(Pair(BoardToBeOpened, ""))
        App.getBus().onNext(Pair(ThreadToBeClosed, ""))
    }

//    companion object {
//        fun getBoardFragment(
//            listener: (threadNum: Int, threadTitle: String) -> Unit,
//            id: String
//        ): BoardFragment {
//            val board = BoardFragment()
//            board.listener = listener
//            board.id = id
//
//            return board
//        }
//    }

}