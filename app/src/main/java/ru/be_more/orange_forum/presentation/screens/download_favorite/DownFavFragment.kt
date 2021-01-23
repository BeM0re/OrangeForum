package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_download.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.presentation.interfaces.*
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class DownFavFragment:
    Fragment(R.layout.fragment_download),
    DownloadListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val viewModel: PresentationContract.DownloadViewModel by inject()
    private var recyclerView : RecyclerView? = null
    private var postFragment: PostFragment? = null
    private lateinit var navController: NavController
    private var disposable: Disposable? = null
    var adapter : DownFavAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        viewModel.init()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        navController.currentDestination?.label = "Favorites"

        recyclerView = rv_downloaded_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    private fun subscribe(){
        with(viewModel){
            observe(boards, ::loadDownloads)
        }

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed && fl_downloaded_board_post.visibility != View.GONE) {
                    App.getBus().onNext(AppToBeClosed)
                }
            },
            { Log.e("M_DownloadFragment","bus error = \n $it") })
    }

    override fun onDestroyView() {
        postFragment = null
        adapter = null
        disposable?.dispose()
        disposable = null
        recyclerView = null
        recyclerView?.adapter = null
        super.onDestroyView()
    }

    fun loadDownloads(boards: List<Board>) {
        adapter = DownFavAdapter(boards, this, this, this)
        recyclerView?.adapter = adapter
    }

    override fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String) {
//        intoThreadClickListener(boardId, threadNum, threadTitle)
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, threadTitle)
        navController.navigate(R.id.action_downloadFragment_to_threadFragment, bundle)
    }

    override fun onRemoveClick(boardId: String, threadNum: Int) {
//        onRemoveClickListener(boardId, threadNum)
        viewModel.removeThread(boardId, threadNum)
    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            App.showToast("Пост не найден")
//        else
//            downloadPresenter.getSinglePost(chanLink.first, chanLink.third)
    }

    override fun onLinkClick(postNum: Int) {
//        downloadPresenter.getSinglePost(postNum)
        App.showToast("Сделать обработку")
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

     /*   var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            downloadPresenter.putContentInStack(attachment)
            showPic(attachment)
            fl_downloaded_board_post.visibility = View.VISIBLE
        }*/

    }

    fun showPic(attachment: Attachment){
       /* postFragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, postFragment!!, POST_IN_DOWNLOAD_TAG)
            ?.commit()*/
    }

    fun showPost(post: Post){
       /* fl_downloaded_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_downloaded_board_post, postFragment!!, POST_IN_DOWNLOAD_TAG)
            ?.commit()*/
    }

    override fun onCloseModalListener() {
     /*   postFragment = null
        hideModal()*/
    }
}