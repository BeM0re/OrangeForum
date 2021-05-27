package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentCategoryBinding
import ru.be_more.orange_forum.databinding.FragmentDownloadBinding
import ru.be_more.orange_forum.presentation.interfaces.*
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class DownFavFragment: BaseFragment<FragmentDownloadBinding>(),
    DownFavListener,
    LinkOnClickListener{

    override val binding: FragmentDownloadBinding by viewBinding()
    private val viewModel: PresentationContract.DownFavViewModel by inject()
    private lateinit var navController: NavController
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_download, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        viewModel.init()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
    }

    private fun subscribe(){
        with(viewModel){
            observe(boards, ::loadFavs)
        }

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed && binding.flDownloadedBoardPost.visibility != View.GONE)
                    App.getBus().onNext(AppToBeClosed)
            },
            { Log.e("M_DownloadFragment","bus error = \n $it") }
        )
    }

    override fun onDestroyView() {
        disposable?.dispose()
        disposable = null
        binding.rvDownloadedList.adapter = null
        super.onDestroyView()
    }

    private fun loadFavs(boards: List<Board>) {
        (binding.rvDownloadedList.adapter as? DownFavAdapter)?.let {
            it.updateData(boards)
            return
        }
        DownFavAdapter(boards, this, object : PicOnClickListener{
            override fun onThumbnailListener(
                fullPicUrl: String?,
                duration: String?,
                fullPicUri: Uri?
            ) {
                //TODO сделоть
                App.showToast("Картинка")
            }
        })
            .apply {
                for (i in (groups.size - 1) downTo 0) {
                    if (! isGroupExpanded(i))
                        toggleGroup(i)
                }
                binding.rvDownloadedList.adapter = this
            }
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

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) { }

    override fun onLinkClick(postNum: Int) {
//        downloadPresenter.getSinglePost(postNum)
        App.showToast("Сделать обработку")
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }
}