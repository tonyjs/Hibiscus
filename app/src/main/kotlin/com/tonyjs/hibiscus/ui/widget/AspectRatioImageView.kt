package com.tonyjs.hibiscus.ui.widget

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.tonyjs.hibiscus.R

class AspectRatioImageView : AppCompatImageView {

    var ratio = DEFAULT_RATIO
        set(ratio) {
            if (field != ratio) {
                field = ratio
                requestLayout()
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(context, attr, defStyle) {
        context.obtainStyledAttributes(attr, R.styleable.AspectRatioImageView)?.let {
            ratio = it.getFloat(R.styleable.AspectRatioImageView_ratio, DEFAULT_RATIO)
            it.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width * this.ratio).toInt()
        setMeasuredDimension(width, height)
    }

    companion object {
        val DEFAULT_RATIO = 1.0f
    }
}
