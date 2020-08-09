package ru.be_more.orange_forum.ui.download

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
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.android.synthetic.main.fragment_thread.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.bus.RefreshDownload
import ru.be_more.orange_forum.bus.RefreshFavorite
import ru.be_more.orange_forum.consts.DOWNLOAD_TAG
import ru.be_more.orange_forum.interfaces.*
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.post.PostFragment

class DownloadFragment private constructor(
    var intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
    var onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit):
    MvpAppCompatFragment(),
    DownloadView,
    DownloadListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var downloadPresenter : DownloadPresenter

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : DownloadAdapter

//    private var bus = BusProvider.getInstance()
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_download, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_downloaded_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

//        bus.register(this)

        disposable = App.getBus().subscribe({

            if(it.first is BackPressed && it.second == DOWNLOAD_TAG) {
                if (fl_downloaded_board_post.visibility != View.GONE)
                    downloadPresenter.onBackPressed()
                else
                    App.getBus().onNext(Pair(AppToBeClosed, ""))
            }
        },
            {
                Log.e("M_DownloadFragment","bus error = \n $it")
            }
        )
    }

    override fun onDestroy() {
//        bus.unregister(this)
        disposable?.dispose()
        super.onDestroy()
    }

    override fun loadDownloads(boards: List<Board>) {
        adapter = DownloadAdapter(boards, this, this, this)
        recyclerView.adapter = adapter
    }

    override fun loadDownloads() {
        adapter = DownloadAdapter(
            downloadPresenter.getBoards(), this, this, this)
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
            downloadPresenter.getSinglePost(chanLink.first, chanLink.third)
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
            downloadPresenter.putContentInStack(attachment)
            showPic(attachment)
            fl_downloaded_board_post.visibility = View.VISIBLE
        }

    }

    override fun showPic(attachment: Attachment){
        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_downloaded_board_post.visibility = View.VISIBLE

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
        downloadPresenter.clearStack()
    }

    override fun showToast(message: String) {
        App.showToast(message )
    }

//    @Subscribe
//    public fun onBackPressed(event: BackPressed) {
//
//        Log.d("M_DownloadFragment", "back")
//        if (fl_downloaded_board_post.visibility != View.GONE)
//            downloadPresenter.onBackPressed()
//        else
//            bus.post(AppToBeClosed)
//    }

    @Subscribe
    public fun refreshDownload(event: RefreshDownload) {
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun getDownloadFragment (
            intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
            onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit
        ): DownloadFragment = DownloadFragment(intoThreadClickListener, onRemoveClickListener)

    }
}