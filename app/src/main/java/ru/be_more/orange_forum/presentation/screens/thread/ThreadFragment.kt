package ru.be_more.orange_forum.presentation.screens.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentThreadBinding
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.presentation.screens.post.PostFragment
import java.util.concurrent.TimeUnit

class ThreadFragment :
    BaseFragment<FragmentThreadBinding>(),
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val viewModel: ThreadViewModel by inject()

    override val binding: FragmentThreadBinding by viewBinding()
    private lateinit var navController: NavController
    private var favButton: MenuItem? = null
    private var favButtonAdded: MenuItem? = null
    private var queueButton: MenuItem? = null
    private var queueButtonAdded: MenuItem? = null
    private var downButton: MenuItem? = null
    private var downButtonAdded: MenuItem? = null
    private var refreshButton: MenuItem? = null
    private var buttonUpDisposable: Disposable? = null
    private var buttonDownDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        init(view)

        setUpDownButtonOnCLickListener()
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
        refreshButton = menu.findItem(R.id.navigation_refresh)
        setToolbarListeners()
        viewModel.onMenuReady()
    }

    override fun onDestroyView() {
        //TODO save state
        binding.rvPostList.adapter = null
        super.onDestroyView()
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
            viewModel.addToQueue(false)
            true
        }
        queueButtonAdded?.setOnMenuItemClickListener {
            viewModel.addToQueue(true)
            true
        }
        refreshButton?.setOnMenuItemClickListener {
            viewModel.onRefresh()
            true
        }
        refreshButton?.isVisible = true
    }

    private fun init(view: View) {
        EventBus.getDefault().post(ThreadToBeOpened)
        navController = Navigation.findNavController(view)

        binding.fabThreadRespond.setOnClickListener { showResponseForm() }

        binding.strPostList.setColorSchemeColors(getColor(requireContext(), R.color.color_accent))

        binding.strPostList.setOnRefreshListener {
            viewModel.onRefresh()
        }

        binding.rvPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when {
                    dy < 0 -> {
                        buttonUpDisposable?.dispose()
                        buttonUpDisposable = showUpDownFab(binding.fabThreadUp)
                    }
                    dy > 0 -> {
                        buttonDownDisposable?.dispose()
                        buttonDownDisposable = showUpDownFab(binding.fabThreadDown)
                    }
                }
            }
        })

        val boardId = requireArguments().getString(NAVIGATION_BOARD_ID)?:""
        val boardName = requireArguments().getString(NAVIGATION_BOARD_NAME)?:""
        val threadNum = requireArguments().getInt(NAVIGATION_THREAD_NUM)
        viewModel.init(boardId, threadNum, boardName)
    }

    private fun subscribe() {
        with(viewModel){
            observe(thread, ::loadThread)
            observe(post, ::showPost)
            observe(attachment, ::showPost)
            observe(emptyStack) { hideModal() }
            observe(savedPosition, ::setPosition)
            observe(isFavorite, ::setFavoriteMark)
            observe(isQueued, ::setQueueMark)
            observe(isDownload, ::setDownloadMark)
            observe(isRefreshing, ::setRefresh)
        }
    }

    override fun onBackPressed(): Boolean {
        return if (binding.flThreadPost.visibility != View.GONE){
            viewModel.onBackPressed()
            false
        }
        else
            true
    }

    private fun showResponseForm() {
        val boardId = requireArguments().getString(NAVIGATION_BOARD_ID)?:""
        val threadNum = requireArguments().getInt(NAVIGATION_THREAD_NUM)
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, boardId)
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, "Reply")
        navController.navigate(R.id.action_threadFragment_to_responseFragment, bundle)
    }

    private fun loadThread(thread: BoardThread) {
        (binding.rvPostList.adapter as? ThreadAdapter)?.let {
            it.updateData(thread)
            return
        }
        binding.rvPostList.adapter = ThreadAdapter(thread, this, this)
        binding.rvPostList.addItemDecoration(
            DividerItemDecoration(requireContext(), HORIZONTAL)
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

    private fun showPost(content: ModalContent){
        binding.flThreadPost.visibility = View.VISIBLE
        PostFragment.getPostFragment(
            content = content,
            this,
            this,
            this
        )
            .also {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_thread_post, it, POST_IN_THREAD_TAG)
                    .commit()
            }
    }

    private fun hideModal() {
        binding.flThreadPost.visibility = View.GONE

        childFragmentManager
            .fragments
            .filterIsInstance<PostFragment>()
            .firstOrNull()
            ?.let {
                it.closeVideo()
                childFragmentManager
                    .beginTransaction()
                    .remove(it)
            }
    }

    private fun setPosition(pos: Int){
        binding.rvPostList.layoutManager?.scrollToPosition(pos)
    }

    private fun setUpDownButtonOnCLickListener(){
        binding.fabThreadUp.setOnClickListener {
            binding.rvPostList.scrollToPosition(0)
        }
        binding.fabThreadDown.setOnClickListener {
            val position = binding.rvPostList.adapter?.itemCount ?: 1
            setPosition(position - 3)
        }
    }

    private fun setRefresh(isRefreshing: Boolean){
        binding.strPostList.isRefreshing = isRefreshing
    }

    private fun showUpDownFab(fab: FloatingActionButton): Disposable {
        fab.isVisible = true

        return Single.timer(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { fab.isVisible = false },
                { Log.e("M_ThreadFragment","hide fab error = $it") }
            )
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        viewModel.prepareModal(fullPicUrl, duration, fullPicUri)
    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        viewModel.getPost(chanLink)
    }

    override fun onLinkClick(postNum: Int) {
        viewModel.getPost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
        Toast
            .makeText(requireContext(), "outer link = $externalLink", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onCloseModalListener(){
        viewModel.closeModal()
    }
}