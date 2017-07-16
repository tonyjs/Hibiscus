package com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder

import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import kotlinx.android.synthetic.main.item_body_header.view.*
import java.text.SimpleDateFormat
import java.util.*

object HeaderViewBinder : ViewBinder<Pair<String, Date>>() {

    override val layoutResId: Int
        get() = R.layout.item_body_header

    override val binder: (MultiViewTypeAdapter.ViewHolder, Pair<String, Date>) -> Unit
        get() = { holder, item ->
            with(holder.itemView) {
                tvTitle.text = item.first

                tvCreatedTime.text = SimpleDateFormat(Post.DEFAULT_DATE_FORMAT).format(item.second)
            }
        }

}