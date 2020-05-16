package ru.be_more.orange_forum.ui.favorire

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_download.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.DownloadListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.post.PostFragment

class FavoriteFragment private constructor(
    var intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
    var onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit):
    MvpAppCompatFragment(),
    FavoriteView,
    DownloadListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var favoritePresenter : FavoritePresenter

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : FavoriteAdapter

    private var bus = BusProvider.getInstance()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_download, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_downloaded_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        bus.register(this)
    }

    override fun onDestroy() {
        bus.unregister(this)
        super.onDestroy()
    }

    override fun loadFavorites(boards: List<Board>) {
        FavoriteAdapter(boards, this, this, this)
        recyclerView.adapter = adapter
    }

    override fun loadFavorites() {
        adapter = FavoriteAdapter(
            favoritePresenter.getBoards(), this, this, this)
        recyclerView.adapter = adapter

    }

    override fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String) {
        intoThreadClickListener(boardId, threadNum, threadTitle)
    }

    override fun onRemoveClick(boardId: String, threadNum: Int) {
        onRemoveClickListener(boardId, threadNum)
    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            App.showToast("Пост не найден")
        else
            favoritePresenter.getSinglePost(chanLink.first, chanLink.third)
    }

    override fun onLinkClick(postNum: Int) {
//        downloadPresenter.getSinglePost(postNum)
        App.showToast("Сделать обработку")
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun onThumbnailListener(fullPicUrl: String, duration: String?) {
        val attachment = Attachment(fullPicUrl, duration)

        favoritePresenter.putContentInStack(attachment)

        showPic(attachment)
    }

    override fun onThumbnailListener(fullPicUri: Uri, duration: String?) {
        val attachment = Attachment("", duration, fullPicUri)

        favoritePresenter.putContentInStack(attachment)

        showPic(attachment)
    }

    override fun showPic(attachment: Attachment){

        fl_downloaded_board_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_board_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun onCloseModalListener() {
        hideModal()
    }

    override fun hideModal() {
        fl_downloaded_board_post.visibility = View.GONE
        favoritePresenter.clearStack()
    }

    override fun showToast(message: String) {
        App.showToast(message )
    }

    @Subscribe
    public fun onBackPressed(event: BackPressed) {

        Log.d("M_ThreadFragment", "back")
        if (fl_downloaded_board_post.visibility != View.GONE)
            favoritePresenter.onBackPressed()
        else
            bus.post(AppToBeClosed)

    }

    companion object {
        fun getFavoriteFragment (
            intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
            onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit
        ): FavoriteFragment = FavoriteFragment(intoThreadClickListener, onRemoveClickListener)

    }
}