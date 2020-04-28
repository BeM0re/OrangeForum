package ru.be_more.orange_forum.ui.custom

import android.content.Context
import android.text.util.Linkify
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import ru.be_more.orange_forum.App

open class LinkedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs)  {

    init {
        //Links handling
        this.movementMethod = BetterLinkMovementMethod.newInstance()

        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            .setOnLinkClickListener { _, link ->
                Toast.makeText(App.applicationContext(), link, Toast.LENGTH_SHORT).show()
                return@setOnLinkClickListener true
            }

        Linkify.addLinks(this, Linkify.ALL)
    }



}