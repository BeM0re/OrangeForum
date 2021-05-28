package ru.be_more.orange_forum.presentation.screens.board

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentBoardBinding
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.interfaces.BoardOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.CloseModalListener
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.presentation.screens.post.PostFragment

class BoardFragment: BaseFragment<FragmentBoardBinding>(),
    BoardOnClickListener,
    PicOnClickListener,
    LinkOnClickListener,
    CloseModalListener {

    override val binding: FragmentBoardBinding by viewBinding()
    private val viewModel: PresentationContract.BoardViewModel by inject()
    private var postFragment: PostFragment? = null
    private lateinit var navController: NavController
    private var favButton: MenuItem? = null
    private var favButtonAdded: MenuItem? = null
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        favButton = menu.findItem(R.id.navigation_favorite)
        favButtonAdded = menu.findItem(R.id.navigation_favorite_added)
        setToolbarListeners()
        viewModel.onMenuReady()
    }

    override fun onDestroyView() {
        saveState()
        disposable?.dispose()
        disposable = null
        postFragment = null
        binding.rvThreadList.adapter = null
        super.onDestroyView()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)

        val boardId = requireArguments().getString(NAVIGATION_BOARD_ID)
        val boardName = requireArguments().getString(NAVIGATION_BOARD_NAME)

        viewModel.init(boardId, boardName)
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
    }

    private fun loadBoard(board: Board) {
        (binding.rvThreadList.adapter as? BoardAdapter)?.let {
            it.updateData(board.threads)
            return
        }
        binding.rvThreadList.adapter = BoardAdapter(
            board.threads, this, this, this) { threadNum ->
            viewModel.addToQueue(threadNum)
        }
        binding.rvThreadList.addItemDecoration(
            DividerItemDecoration(requireContext(), HORIZONTAL)
        )
    }

    private fun setBoardMarks(isFavorite: Boolean) {
        favButton?.isVisible = !isFavorite
        favButtonAdded?.isVisible = isFavorite
    }

    private fun showPost(post: ModalContent){
        binding.flBoardPost.visibility = View.VISIBLE
        PostFragment.getPostFragment(
            content = post,
            this,
            this,
            this
        )
            .also {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_board_post, it, POST_IN_BOARD_TAG)
                    .commit()
            }
    }

    private fun hideModal() {
        binding.flBoardPost.visibility = View.GONE

        childFragmentManager
            .fragments
            .filterIsInstance<PostFragment>().firstOrNull()
            ?.let {
                it.closeVideo()
                childFragmentManager
                    .beginTransaction()
                    .remove(it)
            }

        viewModel.clearStack()
    }

    private fun subscribe(){
        with(viewModel){
            observe(board, ::loadBoard)
            observe(isFavorite) { setBoardMarks(it) }
            observe(post, ::showPost)
            observe(attachment, ::showPost)
            observe(emptyStack) { hideModal() }
            observe(savedPosition) { binding.rvThreadList.scrollToPosition(it) }
        }
    }

    override fun onBackPressed() : Boolean{
        return if (binding.flBoardPost.visibility != View.GONE) {
            viewModel.onBackPressed()
            false
        }
        else
            true
    }

    private fun saveState(){
        viewModel.savePosition(
            (binding.rvThreadList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        )
    }

    override fun onCloseModalListener(){
        hideModal()
    }

    override fun onIntoThreadClick(threadNum: Int, threadTitle: String) {
        val bundle = Bundle()
        bundle.putString(NAVIGATION_BOARD_ID, viewModel.getBoardId())
        bundle.putString(NAVIGATION_BOARD_NAME, viewModel.getBoardName())
        bundle.putInt(NAVIGATION_THREAD_NUM, threadNum)
        bundle.putString(NAVIGATION_TITLE, threadTitle)
        navController.navigate(R.id.action_boardFragment_to_threadFragment, bundle)
    }

    override fun onHideClick(threadNum: Int, toHide: Boolean) {
        viewModel.hideThread(threadNum, toHide)
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        viewModel.prepareModal(fullPicUrl, duration, fullPicUri)
    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        viewModel.linkClicked(chanLink)
    }

    override fun onLinkClick(postNum: Int) {
        viewModel.getSinglePost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

}