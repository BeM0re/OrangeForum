package ru.be_more.orange_forum.ui.post

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.ModalContent
import ru.be_more.orange_forum.model.Post
import javax.inject.Inject

@InjectViewState
class PostPresenter : MvpPresenter<PostView>() {

    @Inject
    lateinit var repo: DvachApiRepository
    private lateinit var boardId: String
    private lateinit var post: Post
    private lateinit var adapter: PostPicAdapter
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private lateinit var pic: Attachment


    fun init(content: ModalContent,
             picListener: PicOnClickListener,
             linkListener: LinkOnClickListener) {

        App.getComponent().inject(this)

        this.picListener = picListener
        this.linkListener = linkListener

        when(content) {
            is Post ->{
                this.post = content
                viewState.setContent(post)
            }
            is Attachment -> {
                this.pic = content
                viewState.setContent(pic)
            }
        }
    }


    fun getAdapter(): PostPicAdapter = this.adapter

    fun getPicListener(): PicOnClickListener = this.picListener

    fun getLinkListener(): LinkOnClickListener = this.linkListener
}