package ru.be_more.orange_forum.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import ru.be_more.orange_forum.R


class ExpandableTextView  @JvmOverloads constructor(
    context: Context,attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs), View.OnClickListener {

    private val defaultTrimLength = 100
    private val readMore = " <html><body>...</body></html>"

    private var originalText :String = ""
    private var trimmedText = ""
    private var bufferType : BufferType? = null
    private var trim = true
    private var trimLength = 10

    init {
        val typedArray : TypedArray  = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, defaultTrimLength)
        typedArray.recycle()
        setOnClickListener(this)
    }

    private fun setText() {
        super.setText(HtmlCompat.fromHtml(getDisplayableText(), HtmlCompat.FROM_HTML_MODE_LEGACY),
            bufferType)
    }

    private fun getDisplayableText(): String {
        return if (trim)
            trimmedText
        else
            originalText
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        originalText = text.toString()
        trimmedText = getTrimmedText()
        bufferType = type
        trim = true
        setText()
    }

    private fun getTrimmedText() : String {
        return if (originalText.isNotEmpty() && originalText.length > trimLength) {
            SpannableStringBuilder(originalText, 0, trimLength + 1).append(readMore).toString()
        } else {
            originalText
        }
    }

    override fun onClick(v: View?) {
        trim = !trim
        setText()
        requestFocusFromTouch()
    }

    fun setTrimLength(trimLength : Int) {
        this.trimLength = getHeight(trimLength)
        trimmedText = getTrimmedText()
        setText()
    }

    private fun getHeight(lineCount : Int) : Int {
        return if (lineCount > 0 )
            ((lineCount * getLineCount() + (lineCount - 1) * paint.fontSpacing) / 1.5f).toInt()
        else
            ((lineCount * getLineCount()) / 1.5f).toInt()
    }
}