package ru.be_more.orange_forum.ui.download

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_download.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.DOWNLOAD_TAG
import ru.be_more.orange_forum.consts.POST_IN_DOWNLOAD_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.interfaces.*
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.post.PostFragment

class DownloadFragment private constructor(
    var intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
    var onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit):
    Fragment(),
    DownloadView,
    DownloadListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val downloadPresenter: DownloadPresenter by inject(parameters = { parametersOf(this) })

    private lateinit var recyclerView : RecyclerView
    lateinit var adapter : DownloadAdapter
    private var postFragment: PostFragment? = null

    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_download, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = rv_downloaded_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        downloadPresenter.initPresenter()

        disposable = App.getBus().subscribe({
            if(it.first is BackPressed && it.second == DOWNLOAD_TAG) {
                if (fl_downloaded_board_post.visibility != View.GONE)
                    downloadPresenter.onBackPressed()
                else
                    App.getBus().onNext(Pair(AppToBeClosed, ""))
            }
            if (it.first is RefreshDownload && it.second == DOWNLOAD_TAG)
                adapter.notifyDataSetChanged()
        },
        {
            Log.e("M_DownloadFragment","bus error = \n $it")
        })
    }

    override fun onDestroy() {
        postFragment = null
        disposable?.dispose()
        disposable = null
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
        postFragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, postFragment!!, POST_IN_DOWNLOAD_TAG)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_downloaded_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, postFragment!!, POST_IN_DOWNLOAD_TAG)
            ?.commit()
    }

    override fun onCloseModalListener() {
        postFragment = null
        hideModal()
    }

    override fun hideModal() {
        fl_downloaded_board_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_DOWNLOAD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_DOWNLOAD_TAG)!!)

        downloadPresenter.clearStack()
    }

    override fun showToast(message: String) {
        App.showToast(message )
    }

    companion object {
        fun getDownloadFragment (
            intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
            onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit
        ): DownloadFragment = DownloadFragment(intoThreadClickListener, onRemoveClickListener)

    }
}