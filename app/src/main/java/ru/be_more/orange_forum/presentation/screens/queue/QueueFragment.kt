package ru.be_more.orange_forum.presentation.screens.queue

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
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.presentation.bus.AppToBeClosed
import ru.be_more.orange_forum.presentation.bus.BackPressed
import ru.be_more.orange_forum.presentation.interfaces.QueueListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class QueueFragment :
    Fragment(R.layout.fragment_favorite),
    QueueListener{

    private val viewModel: PresentationContract.QueueViewModel by inject()
    private var recyclerView : RecyclerView? = null
    private var postFragment: PostFragment? = null
    private var disposable: Disposable? = null
    private lateinit var navController: NavController
    var adapter : QueueAdapter? = null

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
        recyclerView?.adapter = null
        recyclerView = null
        super.onDestroyView()
    }

    fun init(view: View){
        navController = Navigation.findNavController(view)
        recyclerView = rv_favorite_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    fun subscribe(){
        with(viewModel){
            observe(boards, ::loadFavorites)
        }

        disposable = App.getBus().subscribe(
            {
                if (it is BackPressed && fl_favorite_board_post.visibility == View.GONE)
                    App.getBus().onNext(AppToBeClosed)
            },
            { Log.e("M_FavoriteFragment","bus error = \n $it") }
        )
    }

    private fun loadFavorites(boards: List<Board>) {
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
        recyclerView?.adapter = adapter
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
}