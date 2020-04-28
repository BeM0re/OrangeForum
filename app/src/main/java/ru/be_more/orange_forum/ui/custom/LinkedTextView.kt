package ru.be_more.orange_forum.ui.custom

import android.content.Context
import android.text.util.Linkify
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.extentions.toChanLink
import ru.be_more.orange_forum.interfaces.LinkOnClickListener


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
            .setOnLinkClickListener { _, link ->

                if (link[0] == '/')
                    listener?.onClick(link.toChanLink)
                else
                    listener?.onClick(link)

                return@setOnLinkClickListener true
            }

        Linkify.addLinks(this, Linkify.ALL)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(
            HtmlCompat.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY),type)
    }

}