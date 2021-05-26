package ru.be_more.orange_forum.presentation.screens.queue

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
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentQueueBinding
import ru.be_more.orange_forum.presentation.bus.AppToBeClosed
import ru.be_more.orange_forum.presentation.bus.BackPressed
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class QueueFragment : BaseFragment<FragmentQueueBinding>(), DownFavListener{

    override val binding: FragmentQueueBinding by viewBinding()
    private val viewModel: PresentationContract.QueueViewModel by inject()
    private var postFragment: PostFragment? = null
    private var disposable: Disposable? = null
    private lateinit var navController: NavController
    var adapter : QueueAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_queue, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        viewModel.init()
    }

    override fun onDestroyView() {
        disposable?.dispose()
        disposable = null
        postFragment = null
        adapter = null
        binding.rvFavoriteList.adapter = null
        super.onDestroyView()
    }

    fun init(view: View){
        navController = Navigation.findNavController(view)
        binding.rvFavoriteList.layoutManager = LinearLayoutManager(this.context)
    }

    fun subscribe(){
        with(viewModel){
            observe(boards, ::loadQueue)
        }

        disposable = App.getBus().subscribe(
            {
                if (it is BackPressed && binding.flFavoriteBoardPost.visibility == View.GONE)
                    App.getBus().onNext(AppToBeClosed)
            },
            { Log.e("M_FavoriteFragment","bus error = \n $it") }
        )
    }

    private fun loadQueue(boards: List<Board>) {
        adapter = QueueAdapter(boards, this, object : PicOnClickListener{
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
        binding.rvFavoriteList.adapter = adapter
    }

    override fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, threadTitle)
        navController.navigate(R.id.action_queueFragment_to_threadFragment3, bundle)
    }

    override fun intoBoardClick(boardId: String, boardName: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putString(NAVIGATION_TITLE, boardName)
        bundle.putString(NAVIGATION_BOARD_NAME, boardName)
        navController.navigate(R.id.action_queueFragment_to_boardFragment, bundle)
    }

    override fun onRemoveClick(boardId: String, threadNum: Int) {
        viewModel.removeThread(boardId, threadNum)
    }
}