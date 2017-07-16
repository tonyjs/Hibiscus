package com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder

import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.TextBody
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import kotlinx.android.synthetic.main.item_body_text.view.*

object TextBodyBinder : ViewBinder<TextBody>() {

    override val layoutResId: Int
        get() = R.layout.item_body_text

    override val binder: (MultiViewTypeAdapter.ViewHolder, TextBody) -> Unit
        get() = { holder, body ->
            holder.itemView.tvText.text = body.text
        }

}