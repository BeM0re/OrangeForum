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


open class LinkedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs)  {

    init {
        //Links handling
        this.movementMethod = BetterLinkMovementMethod.newInstance()


        //TODO убрать this из аргумента
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            .setOnLinkClickListener { view, link ->

                if (link[0] == '/') {
                    val chanLinks = link.toChanLink


                    Log.d("M_LinkedTextView", "link = $link")
                    Log.d("M_LinkedTextView", "3 links = $chanLinks")
                }
                return@setOnLinkClickListener true
            }

        Linkify.addLinks(this, Linkify.ALL)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(
            HtmlCompat.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY),type)
    }

}