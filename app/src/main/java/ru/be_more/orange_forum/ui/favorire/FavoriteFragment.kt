package ru.be_more.orange_forum.ui.favorire

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.bus.RefreshFavorite
import ru.be_more.orange_forum.bus.VideoToBeClosed
import ru.be_more.orange_forum.consts.FAVORITE_TAG
import ru.be_more.orange_forum.consts.POST_IN_FAVORITE_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.FavoriteListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.post.PostFragment

class FavoriteFragment private constructor(
    var intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
    var intoBoardClickListener: (boardId: String, boardName: String) -> Unit,
    var onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit):
    MvpAppCompatFragment(),
    FavoriteView,
    FavoriteListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var favoritePresenter : FavoritePresenter

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : FavoriteAdapter

//    private var bus = BusProvider.getInstance()
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_favorite, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_favorite_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

//        bus.register(this)

        disposable = App.getBus().subscribe({
            if(it.first is BackPressed && it.second == FAVORITE_TAG) {
                if (fl_favorite_board_post.visibility != View.GONE)
                    favoritePresenter.onBackPressed()
                else
                    App.getBus().onNext(Pair(AppToBeClosed, ""))
            }
            if (it.first is RefreshFavorite && it.second == FAVORITE_TAG)
                favoritePresenter.refreshData()
        },
            {
                Log.e("M_FavoriteFragment","bus error = \n $it")
            }
        )
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun loadFavorites(boards: List<Board>) {
        FavoriteAdapter(boards, this, this)
        recyclerView.adapter = adapter
    }

    override fun loadFavorites() {
        adapter = FavoriteAdapter(
            favoritePresenter.getBoards(), this, this)

        // Iterate and toggle groups
        for (i in (adapter.groups.size - 1) downTo 0) {
            if (! adapter.isGroupExpanded(i))
                adapter.toggleGroup(i)
        }

        recyclerView.adapter = adapter

    }

    override fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String) {
        intoThreadClickListener(boardId, threadNum, threadTitle)
    }

    override fun intoBoardClick(boardId: String, boardName: String) {
        intoBoardClickListener(boardId, boardName)
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

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            favoritePresenter.putContentInStack(attachment)
            showPic(attachment)
//            .visibility = View.VISIBLE
        }

    }

    override fun showPic(attachment: Attachment){

        fl_favorite_board_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_favorite_board_post, fragment, POST_IN_FAVORITE_TAG)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_board_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_favorite_board_post, fragment, POST_IN_FAVORITE_TAG)
            ?.commit()
    }

    override fun onCloseModalListener() {
        hideModal()
    }

    override fun hideModal() {
        fl_favorite_board_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_FAVORITE_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_FAVORITE_TAG)!!)

        favoritePresenter.clearStack()
    }

    override fun showToast(message: String) {
        App.showToast(message )
    }

    companion object {
        fun getFavoriteFragment (
            intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
            intoBoardClickListener: (boardId: String, boardName: String) -> Unit,
            onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit
        ): FavoriteFragment = FavoriteFragment(intoThreadClickListener, intoBoardClickListener, onRemoveClickListener)

    }
}