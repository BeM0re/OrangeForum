package ru.be_more.orange_forum.presentation.screens.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_thread.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.CustomOnScrollListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.custom.CustomScrollListener
import ru.be_more.orange_forum.presentation.screens.post.PostFragment
import ru.be_more.orange_forum.presentation.screens.response.ResponseFragment

class ThreadFragment : Fragment(R.layout.fragment_thread),
    PicOnClickListener,
    LinkOnClickListener,
    CustomOnScrollListener,
    CloseModalListener {

    private val viewModel: PresentationContract.ThreadViewModel by inject()

    private var boardId: String = ""
    private var boardName: String = ""
    private var threadNum: Int = 0
    private var recyclerView : RecyclerView? = null //TODO Убрать, пихать адаптер сразу в верстку
    private var disposable: Disposable? = null
    private var responseFragment: ResponseFragment? = null
    private var postFragment: PostFragment? = null
    private var adapter : ThreadAdapter? = null
    private lateinit var navController: NavController
    private var favButton: MenuItem? = null
    private var favButtonAdded: MenuItem? = null
    private var queueButton: MenuItem? = null
    private var queueButtonAdded: MenuItem? = null
    private var downButton: MenuItem? = null
    private var downButtonAdded: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()

        setUpDownButtonOnCLickListener()

//        setOnScrollListener()

        fab_thread_respond.setOnClickListener { showResponseForm() }

        //Swipe to refresh. maybe return later
        /*srl_thread.setColorSchemeColors(ContextCompat.getColor(App.applicationContext(), R.color.color_accent))
        srl_thread.setOnRefreshListener {
            srl_thread.isRefreshing = false
            threadPresenter.updateThreadData()
        }*/

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        favButton = menu.findItem(R.id.navigation_favorite)
        favButtonAdded = menu.findItem(R.id.navigation_favorite_added)
        queueButton = menu.findItem(R.id.navigation_queue)
        queueButtonAdded = menu.findItem(R.id.navigation_queue_added)
        downButton = menu.findItem(R.id.navigation_download)
        downButtonAdded = menu.findItem(R.id.navigation_download_done)
        setToolbarListeners()
    }

    private fun setToolbarListeners() {
        favButton?.setOnMenuItemClickListener {
            viewModel.setFavorite(true)
            true
        }
        favButtonAdded?.setOnMenuItemClickListener {
            viewModel.setFavorite(false)
            true
        }
        downButton?.setOnMenuItemClickListener {
            viewModel.download(true)
            true
        }
        downButtonAdded?.setOnMenuItemClickListener {
            viewModel.download(false)
            true
        }
        queueButton?.setOnMenuItemClickListener {
            viewModel.setQueue(false)
            true
        }
        queueButtonAdded?.setOnMenuItemClickListener {
            viewModel.setQueue(true)
            true
        }
    }

    override fun onDestroyView() {
        //TODO save state
        disposable?.dispose()
        disposable = null
        adapter = null
        recyclerView?.adapter = null
        postFragment = null
        responseFragment = null
        super.onDestroyView()
    }

    private fun init(view: View) {
        App.getBus().onNext(ThreadToBeOpened)
        navController = Navigation.findNavController(view)

        boardId = requireArguments().getString(NAVIGATION_BOARD_ID)?:""
        boardName = requireArguments().getString(NAVIGATION_BOARD_NAME)?:""
        threadNum = requireArguments().getInt(NAVIGATION_THREAD_NUM)

        viewModel.init(boardId, threadNum, boardName)
        recyclerView = rv_post_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
    }

    private fun subscribe() {
        with(viewModel){
            observe(thread, ::loadThread)
            observe(post, ::showPost)
            observe(attachment, ::showPic)
            observe(emptyStack) { hideModal() }
            observe(savedPosition, ::setPosition)
            observe(isFavorite, ::setFavoriteMark)
            observe(isQueued, ::setQueueMark)
            observe(isDownload, ::setDownloadMark)
        }

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed ) {
                    if (fl_thread_post.visibility != View.GONE)
                            viewModel.onBackPressed()
                    else
                        App.getBus().onNext(AppToBeClosed)
                }
            },
            { Log.e("M_ThreadFragment","bus error = \n $it") }
        )
    }

    //TODO переделать на нормальную капчу, когда (если) макака сделает API
    private fun showResponseForm() {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, "Reply")
        navController.navigate(R.id.action_threadFragment_to_responseFragment, bundle)
    }

    private fun loadThread(thread: BoardThread) {
        adapter = ThreadAdapter(thread, this, this)

        recyclerView?.adapter = adapter
        recyclerView?.addItemDecoration(
            DividerItemDecoration(recyclerView?.context, HORIZONTAL)
        )
    }

    private fun setDownloadMark(isDownloaded: Boolean){
        downButton?.isVisible = !isDownloaded
        downButtonAdded?.isVisible = isDownloaded
    }

    private fun setFavoriteMark(isFavorite: Boolean){
        favButton?.isVisible = !isFavorite
        favButtonAdded?.isVisible = isFavorite
    }

    private fun setQueueMark(isQueued: Boolean){
        queueButton?.isVisible = !isQueued
        queueButtonAdded?.isVisible = isQueued
    }

    private fun showPic(attachment: Attachment){
        postFragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, postFragment!!, POST_IN_THREAD_TAG)
            ?.commit()
    }

    //TODO переделать как-нибудь нормально
    private fun showPost(post: Post){
        fl_thread_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, postFragment!!, POST_IN_THREAD_TAG)
            ?.commit()
    }

    private fun hideModal() {
        fl_thread_post.visibility = View.GONE

        App.getBus().onNext(VideoToBeClosed)

        if (fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG)!!)

        viewModel.clearStack()
    }

    private fun setPosition(pos: Int){
        (recyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(pos)
    }

    private fun setUpDownButtonOnCLickListener(){
        fab_thread_up.setOnClickListener {
            recyclerView?.scrollToPosition(0)
        }
        fab_thread_down.setOnClickListener {
            recyclerView?.scrollToPosition(adapter?.itemCount?:1 - 1)
        }
    }

    private fun setOnScrollListener(){
        val scrollListener = CustomScrollListener(this)

        recyclerView?.setOnScrollChangeListener(scrollListener)
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            viewModel.putContentInStack(attachment)
            showPic(attachment)
            fl_thread_post.visibility = View.VISIBLE
        }

    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink != null) {
            viewModel.getPost(chanLink)
        }
    }

    override fun onLinkClick(postNum: Int) {
        viewModel.getPost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun onScrolling(){
        Log.d("M_ThreadFragment", "scroll")
        fab_thread_up.visibility = View.VISIBLE
        fab_thread_down.visibility = View.VISIBLE
    }

    override fun onScrollStop(){
        Log.d("M_ThreadFragment", "stop scroll")
        fab_thread_up.visibility = View.GONE
        fab_thread_down.visibility = View.GONE
    }

    override fun onCloseModalListener(){
        postFragment = null
        hideModal()
    }
}