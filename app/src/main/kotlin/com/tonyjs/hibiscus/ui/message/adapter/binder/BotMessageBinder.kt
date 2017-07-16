package com.tonyjs.hibiscus.ui.message.adapter.binder

import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import com.tonyjs.hibiscus.ui.message.Message
import kotlinx.android.synthetic.main.item_message_bot.view.*

class BotMessageBinder : ViewBinder<Message>() {

    override val layoutResId: Int
        get() = R.layout.item_message_bot

    override val binder: (MultiViewTypeAdapter.ViewHolder, Message) -> Unit
        get() = { holder, message ->
            with(holder.itemView) {
                tvText.text = message.text()
            }
        }

}