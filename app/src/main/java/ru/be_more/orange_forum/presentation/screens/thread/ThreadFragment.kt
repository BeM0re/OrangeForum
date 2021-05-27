package ru.be_more.orange_forum.presentation.screens.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentCategoryBinding
import ru.be_more.orange_forum.databinding.FragmentThreadBinding
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.presentation.screens.board.BoardAdapter
import ru.be_more.orange_forum.presentation.screens.post.PostFragment
import ru.be_more.orange_forum.presentation.screens.response.ResponseFragment

class ThreadFragment :
    BaseFragment<FragmentThreadBinding>(),
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    private val viewModel: PresentationContract.ThreadViewModel by inject()

    override val binding: FragmentThreadBinding by viewBinding()
    private var disposable: Disposable? = null
    private lateinit var navController: NavController
    private var favButton: MenuItem? = null
    private var favButtonAdded: MenuItem? = null
    private var queueButton: MenuItem? = null
    private var queueButtonAdded: MenuItem? = null
    private var downButton: MenuItem? = null
    private var downButtonAdded: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        init(view)

        setUpDownButtonOnCLickListener()

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
        viewModel.onMenuReady()
    }

    override fun onDestroyView() {
        //TODO save state
        disposable?.dispose()
        disposable = null
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
    }

    private fun init(view: View) {
        App.getBus().onNext(ThreadToBeOpened)
        navController = Navigation.findNavController(view)

        binding.fabThreadRespond.setOnClickListener { showResponseForm() }

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
        }

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed ) {
                    if (binding.flThreadPost.visibility != View.GONE)
                            viewModel.onBackPressed()
                    else
                        App.getBus().onNext(AppToBeClosed)
                }
            },
            { Log.e("M_ThreadFragment","bus error = \n $it") }
        )
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

        App.getBus().onNext(VideoToBeClosed)

        childFragmentManager.findFragmentByTag(POST_IN_THREAD_TAG)
            ?.let {
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
            binding.rvPostList.scrollToPosition(binding.rvPostList.adapter?.itemCount ?: 1 - 1)
        }
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