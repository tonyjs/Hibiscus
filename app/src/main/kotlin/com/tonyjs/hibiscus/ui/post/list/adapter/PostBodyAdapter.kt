package com.tonyjs.hibiscus.ui.post.list.adapter

import android.graphics.Color
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.data.model.TextBody
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.DividerInfo
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.DividerViewBinder
import com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder.HeaderViewBinder
import com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder.ImageBodyBinder
import com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder.TextBodyBinder
import java.lang.UnsupportedOperationException
import java.util.*

open class PostBodyAdapter(val post: Post) : MultiViewTypeAdapter() {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
        const val VIEW_TYPE_DIVIDER = 3
    }

    init {
        addItem(Pair(post.title, post.createdTime), VIEW_TYPE_TITLE)
        addBodyRows()
    }

    override fun registerBinders() {
        registerBinder(VIEW_TYPE_TITLE, HeaderViewBinder.layoutResId, HeaderViewBinder.binder)
        registerBinder(VIEW_TYPE_TEXT, TextBodyBinder.layoutResId, TextBodyBinder.binder)
        registerBinder(VIEW_TYPE_IMAGE, ImageBodyBinder.layoutResId, ImageBodyBinder.binder)
        registerBinder(VIEW_TYPE_DIVIDER, DividerViewBinder.layoutResId, DividerViewBinder.binder)
    }

    fun addBodyRows() {
        post.bodies.sortedWith(Comparator { t1, t2 -> if (t1.seq < t2.seq) -1 else 0 })
                .forEach {
                    addDividerRow()
                    val viewType = if (it is TextBody) VIEW_TYPE_TEXT
                    else if (it is ImageBody) VIEW_TYPE_IMAGE
                    else throw UnsupportedOperationException("Should not passing unknown body type")
                    addItem(it, viewType)
                }
    }

    fun addDividerRow() {
        val dividerInfo = DividerInfo(8, Color.TRANSPARENT)
        addItem(dividerInfo, VIEW_TYPE_DIVIDER)
    }

}
