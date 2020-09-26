package ru.be_more.orange_forum.ui.post

import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post


class PostViewModel (private var viewState: PostView?) {

    private lateinit var post: Post
    private lateinit var picListener: PicOnClickListener
    private lateinit var linkListener: LinkOnClickListener
    private lateinit var pic: Attachment

    fun init(content: ModalContent,
             picListener: PicOnClickListener,
             linkListener: LinkOnClickListener) {

        this.picListener = picListener
        this.linkListener = linkListener

        when(content) {
            is Post ->{
                this.post = content
                viewState?.setContent(post)
            }
            is Attachment -> {
                this.pic = content
                viewState?.setContent(pic)
            }
        }
    }

    fun getPicListener(): PicOnClickListener = this.picListener

    fun getLinkListener(): LinkOnClickListener = this.linkListener

    fun onDestroy(){
        viewState = null
    }


}