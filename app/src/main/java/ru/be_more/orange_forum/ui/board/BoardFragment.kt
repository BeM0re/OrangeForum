package ru.be_more.orange_forum.ui.board

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_thread.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.post.PicOnClickListener
import ru.be_more.orange_forum.ui.post.PostFragment


//TODO сделать динамическое количество картинок через ресайклер
class BoardFragment: MvpAppCompatFragment(),
    BoardView,
    BoardOnClickListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var boardPresenter : BoardPresenter

    private var timestamp: Long = 0
    private var listener: ((Int, String) -> Unit)? = null
    private var id: String = ""
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : BoardAdapter

    private var bus = BusProvider.getInstance()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardPresenter.init(id, listener)
        recyclerView = rv_thread_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        bus.register(this)
    }

    override fun onDestroy() {
        bus.unregister(this)
        super.onDestroy()
    }

    override fun loadBoard(board: Board) {
        adapter = BoardAdapter(
            board.threads, this, this, this)

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, HORIZONTAL)
        )
    }

    override fun onThreadClick(threadNum: Int, threadTitle: String) {
        if (boardPresenter.listener != null)
            boardPresenter.listener!!(threadNum, threadTitle)
    }

    override fun onThumbnailListener(fullPicUrl: String, duration: String?) {

        fl_board_post.visibility = View.VISIBLE

        val attachment = Attachment(fullPicUrl, duration)

        boardPresenter.putContentInStack(attachment)

        showPic(attachment)
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
        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_board_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_board_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun hideModal() {
        fl_board_post.visibility = View.GONE
        boardPresenter.clearStack()
    }

    override fun onCloseModalListener(){
        hideModal()
    }

    override fun showToast(message: String) {
        Toast.makeText(App.applicationContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    public fun onBackPressed(event: BackPressed) {

        Log.d("M_ThreadFragment", "back")
        if (fl_board_post.visibility != View.GONE)
            boardPresenter.onBackPressed()
        else
            bus.post(AppToBeClosed)

    }

    companion object {
        fun getBoardFragment (listener: (threadNum: Int, threadTitle: String) -> Unit,
                              id: String): BoardFragment {
            val board = BoardFragment()
            board.listener = listener
            board.id = id

            return board
        }
    }

}