package ru.be_more.orange_forum.presentation.screens.queue

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.databinding.FragmentQueueBinding
import ru.be_more.orange_forum.presentation.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.interfaces.DownFavListener
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment

class QueueFragment : BaseFragment<FragmentQueueBinding>(), DownFavListener{

    override val binding: FragmentQueueBinding by viewBinding()
    private val viewModel: QueueViewModel by inject()
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_queue, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        viewModel.init()
    }

    override fun onDestroyView() {
        binding.rvFavoriteList.adapter = null
        super.onDestroyView()
    }

    fun init(view: View){
        navController = Navigation.findNavController(view)
    }

    fun subscribe(){
        observe(viewModel.boards, ::loadQueue)
    }

    override fun onBackPressed() : Boolean{
//        if (binding.flFavoriteBoardPost.visibility == View.GONE)
        //TODO доделать закрытие открытой фото
        return true
    }

    private fun loadQueue(boards: List<Board>) {
//        (binding.rvFavoriteList.adapter as? QueueAdapter)?.let {
//            it.updateData(boards)
//            return
//        }
        /*QueueAdapter(boards, this, object : PicOnClickListener{
            override fun onThumbnailListener(
                fullPicUrl: String?,
                duration: String?,
                fullPicUri: Uri?
            ) {
                //TODO сделоть
                Toast.makeText(requireContext(), "Картинка", Toast.LENGTH_SHORT).show()
            }
        })
            .apply {
                for (i in (groups.size - 1) downTo 0) {
                    if (! isGroupExpanded(i))
                        toggleGroup(i)
                }
                binding.rvFavoriteList.adapter = this
            }*/
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