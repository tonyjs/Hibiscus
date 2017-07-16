package com.tonyjs.hibiscus.ui.message.adapter

import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.message.Message
import com.tonyjs.hibiscus.ui.message.adapter.binder.BotMessageBinder
import com.tonyjs.hibiscus.ui.message.adapter.binder.HumanMessageBinder
import java.lang.UnsupportedOperationException

class MessageAdapter : MultiViewTypeAdapter() {

    companion object {
        private const val VIEW_TYPE_BOT = 0
        private const val VIEW_TYPE_HUMAN = 1
    }

    override fun registerBinders() {
        val botMessageBinder = BotMessageBinder()
        registerBinder(VIEW_TYPE_BOT, botMessageBinder.layoutResId, botMessageBinder.binder)
        val humanMessageBinder = HumanMessageBinder()
        registerBinder(VIEW_TYPE_HUMAN, humanMessageBinder.layoutResId, humanMessageBinder.binder)
    }

    fun addMessage(message: Message) {
        addItem(message, if (message.from() == Message.From.BOT) {
            VIEW_TYPE_BOT
        } else if (message.from() == Message.From.HUMAN) {
            VIEW_TYPE_HUMAN
        } else throw UnsupportedOperationException("able to handle BOT and HUMAN"))
    }

    fun findMessagePositionByTag(message: Message): Int? {
        return items.filter {
            val tag = (it.first as? Message)?.tag()
            tag != null && tag == message.tag()
        }.withIndex().firstOrNull()?.index
    }

}