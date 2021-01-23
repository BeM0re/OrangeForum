package ru.be_more.orange_forum.presentation.screens.favorire

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
import ru.be_more.orange_forum.presentation.bus.AppToBeClosed
import ru.be_more.orange_forum.presentation.bus.BackPressed
import ru.be_more.orange_forum.consts.NAVIGATION_BOARD_ID
import ru.be_more.orange_forum.consts.NAVIGATION_THREAD_TITLE
import ru.be_more.orange_forum.consts.NAVIGATION_TITLE
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.FavoriteListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class QueueFragment :
    Fragment(R.layout.fragment_favorite),
    FavoriteListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val viewModel: PresentationContract.FavoriteViewModel by inject()
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
        navController.currentDestination?.label = "Favorite"

        recyclerView = rv_favorite_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    fun subscribe(){
        with(viewModel){
            observe(boards, ::loadFavorites)
        }

        disposable = App.getBus().subscribe({
            if(it is BackPressed) {
                if (fl_favorite_board_post.visibility != View.GONE)
//                    viewModel.onBackPressed()
                else
                    App.getBus().onNext(AppToBeClosed)
            }
//            if (it.first is RefreshFavorite && it.second == FAVORITE_TAG)
//                viewModel.refreshData()
        },
            {
                Log.e("M_FavoriteFragment","bus error = \n $it")
            }
        )
    }


    private fun loadFavorites(boards: List<Board>) {
        adapter = QueueAdapter(boards, this, this)

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
        bundle.putInt(NAVIGATION_THREAD_TITLE, threadNum)
        bundle.putString(NAVIGATION_TITLE, threadTitle)
        navController.navigate(R.id.action_favoriteFragment_to_threadFragment3, bundle)
    }

    override fun intoBoardClick(boardId: String, boardName: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putString(NAVIGATION_TITLE, boardName)
        navController.navigate(R.id.action_favoriteFragment_to_boardFragment, bundle)
    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            App.showToast("Пост не найден")
        else
            ""
//            viewModel.getSinglePost(chanLink.first, chanLink.third)
    }

    override fun onLinkClick(postNum: Int) {
//        downloadPresenter.getSinglePost(postNum)
        App.showToast("Сделать обработку")
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

       /* var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            favoritePresenter.putContentInStack(attachment)
            showPic(attachment)
//            .visibility = View.VISIBLE
        }*/

    }

    fun showPic(attachment: Attachment){

       /* fl_favorite_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_favorite_board_post, postFragment!!, POST_IN_FAVORITE_TAG)
            ?.commit()*/
    }

    fun showPost(post: Post){

     /*   fl_board_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_favorite_board_post, postFragment!!, POST_IN_FAVORITE_TAG)
            ?.commit()*/
    }

    override fun onCloseModalListener() {
       /* postFragment = null
        hideModal()*/
    }

    fun hideModal() {
       /* fl_favorite_board_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_FAVORITE_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_FAVORITE_TAG)!!)

        favoritePresenter.clearStack()*/
    }

    fun showToast(message: String) {
        App.showToast(message )
    }

//    companion object {
//        fun getFavoriteFragment (
//            intoThreadClickListener: (boardId: String, threadNum: Int, threadTitle: String) -> Unit,
//            intoBoardClickListener: (boardId: String, boardName: String) -> Unit,
//            onRemoveClickListener: (boardId: String, threadNum: Int) -> Unit
//        ): FavoriteFragment = FavoriteFragment(intoThreadClickListener, intoBoardClickListener, onRemoveClickListener)
//
//    }
}