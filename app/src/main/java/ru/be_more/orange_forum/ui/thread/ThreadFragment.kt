package ru.be_more.orange_forum.ui.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_thread.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.POST_IN_THREAD_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.consts.RESPONSE_TAG
import ru.be_more.orange_forum.consts.THREAD_TAG
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.CustomOnScrollListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.custom.CustomScrollListener
import ru.be_more.orange_forum.ui.post.PostFragment
import ru.be_more.orange_forum.ui.response.ResponseFragment

class ThreadFragment : Fragment(),
    PicOnClickListener,
    LinkOnClickListener,
    ThreadView,
    CustomOnScrollListener,
    CloseModalListener {

    private val threadPresenter: ThreadPresenter by inject(parameters = { parametersOf(this) })

    private lateinit var boardId: String
    private var threadNum: Int = 0
    private var recyclerView : RecyclerView? = null
    private var disposable: Disposable? = null
    private var responseFragment: ResponseFragment? = null
    private var postFragment: PostFragment? = null
    private var adapter : ThreadAdapter? = null
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val boardId = requireArguments().getString("boardId")
        val threadNum = requireArguments().getInt("threadNum")

        threadPresenter.init(boardId, threadNum)
        recyclerView = rv_post_list
        recyclerView?.layoutManager = LinearLayoutManager(this.context)

        disposable = App.getBus().subscribe(
            {
                if(it.first is BackPressed && it.second == THREAD_TAG) {
                    if (fl_thread_post.visibility != View.GONE){
                        if (fragmentManager?.findFragmentByTag(RESPONSE_TAG) != null)
                            hideResponseForm()
                        else
                            threadPresenter.onBackPressed()
                    }
                    else
                        App.getBus().onNext(Pair(AppToBeClosed, ""))
                }
                if (it.first is ThreadEntered && it.second == THREAD_TAG)
                    threadPresenter.setThreadMarks()
            },
            {
                Log.e("M_ThreadFragment","bus error = \n $it")
            }
        )

        setUpDownButtonOnCLickListener()

        setOnScrollListener()

        fab_thread_respond.setOnClickListener { showResponseForm() }
        fab_close_posting.setOnClickListener { hideResponseForm() }

        //Swipe to refresh. maybe return later
        /*srl_thread.setColorSchemeColors(ContextCompat.getColor(App.applicationContext(), R.color.color_accent))
        srl_thread.setOnRefreshListener {
            srl_thread.isRefreshing = false
            threadPresenter.updateThreadData()
        }*/

    }

    private fun hideResponseForm() {

        fab_thread_down.visibility = View.VISIBLE
        fab_thread_up.visibility = View.VISIBLE
        fab_thread_respond.visibility = View.VISIBLE
        fab_close_posting.visibility = View.GONE

        fl_thread_post.visibility = View.GONE
        fl_thread_post.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        //will be called only when fragmentManager?.findFragmentByTag(RESPONSE_TAG) not null
        fragmentManager
            ?.beginTransaction()
            ?.remove(fragmentManager?.findFragmentByTag(RESPONSE_TAG)!!)
            ?.commit()
    }

    override fun onDestroyView() {
        fl_thread_post.clearFindViewByIdCache()
        super.onDestroyView()
    }

    override fun onDestroy() {
        recyclerView = null
        disposable?.dispose()
        disposable = null
        postFragment = null
        responseFragment = null
        adapter = null
        threadPresenter.onDestroy()
        super.onDestroy()
    }

    //TODO переделать на нормальную капчу, когда (если) макака сделает API
    override fun showResponseForm() {

        if (responseFragment == null)
            responseFragment = ResponseFragment(boardId, threadNum)

        fab_thread_down.visibility = View.GONE
        fab_thread_up.visibility = View.GONE
        fab_thread_respond.visibility = View.GONE
        fab_close_posting.visibility = View.VISIBLE

        fl_thread_post.visibility = View.VISIBLE
        fl_thread_post.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, responseFragment!!, RESPONSE_TAG)
            ?.commit()
    }

    override fun loadThread(thread: BoardThread) {

//        navController.currentDestination?.label = thread.title

        adapter = ThreadAdapter(thread, this, this)

        recyclerView?.adapter = adapter
        recyclerView?.addItemDecoration(
            DividerItemDecoration(recyclerView?.context, HORIZONTAL)
        )
    }

    override fun setThreadMarks(isDownloaded: Boolean, isFavorite: Boolean){
        if (isDownloaded)
            App.getBus().onNext(Pair(DownloadedThreadEntered, ""))
        else
            App.getBus().onNext(Pair(UndownloadedThreadEntered, ""))

        if (isFavorite)
            App.getBus().onNext(Pair(FavoriteThreadEntered, ""))
        else
            App.getBus().onNext(Pair(UnfavoriteThreadEntered, ""))
    }

    override fun hideResponseFab() {
        fab_thread_respond.visibility = View.GONE
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            threadPresenter.putContentInStack(attachment)
            showPic(attachment)
            fl_thread_post.visibility = View.VISIBLE
        }

    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink != null) {
            val post = threadPresenter.thread.posts.find { it.num == chanLink.third }

            if (post != null) {
                threadPresenter.putContentInStack(post)
                showPost(post)
            }
            else
                threadPresenter.getSinglePost(chanLink.first, chanLink.third)
        }
    }

    override fun onLinkClick(postNum: Int) {
        val post = threadPresenter.thread.posts.find { it.num == postNum }

        if (post != null) {
            threadPresenter.putContentInStack(post)
            showPost(post)
        }
        else
            threadPresenter.getSinglePost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun showPic(attachment: Attachment){
        postFragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, postFragment!!, POST_IN_THREAD_TAG)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_thread_post.visibility = View.VISIBLE

        postFragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, postFragment!!, POST_IN_THREAD_TAG)
            ?.commit()
    }

    override fun hideModal() {
        fl_thread_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG)!!)

        threadPresenter.clearStack()
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

    override fun showToast(message: String) {
        Toast.makeText(App.applicationContext(), message, Toast.LENGTH_SHORT).show()
    }

//    companion object {
//        fun getThreadFragment ( boardId: String, threadId: Int): ThreadFragment {
//            val thread = ThreadFragment()
//            thread.boardId = boardId
//            thread.threadNum = threadId
//
//            return thread
//        }
//    }
}