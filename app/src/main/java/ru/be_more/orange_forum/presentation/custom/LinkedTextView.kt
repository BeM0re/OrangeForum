package ru.be_more.orange_forum.presentation.custom

import android.content.Context
import android.text.util.Linkify
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.extentions.toChanLink
import ru.be_more.orange_forum.presentation.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.utils.ParseHtml

open class LinkedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs)  {

    private var listener: LinkOnClickListener? = null

    fun setListener(listener: LinkOnClickListener){
        this.listener = listener
    }

    init {
        //Links handling
        this.movementMethod = BetterLinkMovementMethod.newInstance()

        //TODO убрать this из аргумента
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            .setOnLinkClickListener { view, link ->

                if(view.id == R.id.tv_item_post_replies)
                    listener?.onLinkClick(link.toInt())
                else{
                    if (link[0] == '/')
                        listener?.onLinkClick(link.toChanLink)
                    else
                        listener?.onLinkClick(link)
                }



                return@setOnLinkClickListener true
            }

        Linkify.addLinks(this, Linkify.ALL)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {

        ParseHtml.parse(text.toString())

        super.setText(
            HtmlCompat.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY), type)
    }

}