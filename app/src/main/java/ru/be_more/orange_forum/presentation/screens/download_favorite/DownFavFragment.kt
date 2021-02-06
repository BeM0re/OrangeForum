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
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class DownFavFragment: Fragment(R.layout.fragment_download), DownFavListener, LinkOnClickListener{

    private val viewModel: PresentationContract.DownFavViewModel by inject()
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
        recyclerView = rv_downloaded_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    private fun subscribe(){
        with(viewModel){
            observe(boards, ::loadFavs)
        }

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed && fl_downloaded_board_post.visibility != View.GONE)
                    App.getBus().onNext(AppToBeClosed)
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

    private fun loadFavs(boards: List<Board>) {
        adapter = DownFavAdapter(boards, this, object : PicOnClickListener{
            override fun onThumbnailListener(
                fullPicUrl: String?,
                duration: String?,
                fullPicUri: Uri?
            ) {
                //TODO сделоть
                App.showToast("Картинка")
            }
        })

        // Iterate and toggle groups
        for (i in (adapter!!.groups.size - 1) downTo 0) {
            if (! adapter!!.isGroupExpanded(i))
                adapter!!.toggleGroup(i)
        }
        recyclerView?.adapter = adapter
    }

    override fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, threadTitle)
        navController.navigate(R.id.action_downFavFragment_to_threadFragment, bundle)
    }

    override fun intoBoardClick(boardId: String, boardName: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putString(NAVIGATION_TITLE, boardName)
        bundle.putString(NAVIGATION_BOARD_NAME, boardName)
        navController.navigate(R.id.action_downFavFragment_to_boardFragment, bundle)
    }

    override fun onRemoveClick(boardId: String, threadNum: Int) {
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
}