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
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.BOARD_TAG
import ru.be_more.orange_forum.consts.POST_IN_BOARD_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.ui.PresentationContract
import ru.be_more.orange_forum.ui.post.PostFragment

//TODO сделать динамическое количество картинок через ресайклер
class BoardFragment: Fragment(),
    BoardOnClickListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val viewModel: PresentationContract.BoardViewModel by inject()
    private var recyclerView : RecyclerView? = null
    private var adapter : BoardAdapter? = null
    private var postFragment: PostFragment? = null
    private lateinit var navController: NavController

    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? = inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
    }

    override fun onDestroyView() {
        saveState()
        disposable?.dispose()
        disposable = null
        postFragment = null
        adapter = null
        recyclerView?.adapter = null
        recyclerView = null
        super.onDestroyView()
    }

    private fun loadBoard(board: Board) {
        adapter = BoardAdapter(
            board.threads, this, this, this
        )

        recyclerView?.adapter = adapter
        recyclerView?.addItemDecoration(
            DividerItemDecoration(recyclerView?.context, HORIZONTAL)
        )
    }

    private fun setBoardMarks(isFavorite: Boolean) {
        if (isFavorite)
            App.getBus().onNext(FavoriteBoardEntered)
        else
            App.getBus().onNext(UnfavoriteBoardEntered)
    }

    fun showPic(attachment: Attachment){
        postFragment = PostFragment.getPostFragment(
            attachment, this, this, this
        )

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, postFragment!!, POST_IN_BOARD_TAG)
            ?.commit()
    }

    fun showPost(post: Post){

        fl_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post, this, this, this
        )

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, postFragment!!, POST_IN_BOARD_TAG)
            ?.commit()
    }

    fun hideModal() {
        fl_board_post.visibility = View.GONE

        App.getBus().onNext(VideoToBeClosed)

        if (fragmentManager?.findFragmentByTag(POST_IN_BOARD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_BOARD_TAG)!!)

        viewModel.clearStack()
    }

    fun showToast(message: String) {
        Toast.makeText(App.applicationContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun subscribe(){
        //subscribe to viewModel
        with(viewModel){
            observe(board, ::loadBoard)
            observe(isFavorite) { setBoardMarks(it) }
            observe(post, ::showPost)
            observe(attachment, ::showPic)
            observe(emptyStack) { hideModal() }
            observe(savedPosition) { recyclerView?.scrollToPosition(it) }
        }

        //Subscribe to event bus
        disposable = App.getBus().subscribe({
            if (it is BackPressed ) {
                if (fl_board_post.visibility != View.GONE)
                    viewModel.onBackPressed()
                else
                    App.getBus().onNext(AppToBeClosed)
            }
//            if (it.first is BoardEntered && it.second == BOARD_TAG)
//                viewModel.setBoardMarks()
        },
            {
                Log.e("M_BoardFragment", "bus error = \n $it")
            })
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)

        val boardId = requireArguments().getString("boardId")

        viewModel.init(boardId)
        recyclerView = rv_thread_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)

        App.getBus().onNext(BoardToBeOpened)
        App.getBus().onNext(ThreadToBeClosed)
    }

    private fun saveState(){
        viewModel.savePosition(
            (recyclerView?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        )
    }

    override fun onCloseModalListener(){
        postFragment = null
        hideModal()
    }

    override fun onIntoThreadClick(threadNum: Int, threadTitle: String) {
        val bundle = Bundle()
        bundle.putString("boardId", viewModel.getBoardId())
        bundle.putInt("threadNum", threadNum)
        bundle.putString("title", threadTitle)
        navController.navigate(R.id.action_boardFragment_to_threadFragment, bundle)

//        if (boardPresenter.listener != null)
//            boardPresenter.listener!!(threadNum, threadTitle)
    }

    override fun onHideClick(threadNum: Int, toHide: Boolean) {
        viewModel.hideThread(threadNum, toHide)
        adapter?.notifyDataSetChanged()
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            viewModel.putContentInStack(attachment)
            showPic(attachment)
            fl_board_post.visibility = View.VISIBLE
        }

    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            showToast("Пост не найден")
        else
            viewModel.getSinglePost(chanLink.first, chanLink.third)
    }

    override fun onLinkClick(postNum: Int) {
        viewModel.getSinglePost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

}