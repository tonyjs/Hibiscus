package com.tonyjs.hibiscus.ui.message

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MessageViewModel : ViewModel() {

    val messageEvent = MutableLiveData<Pair<EventType, Message>>()

    fun write(message: String, from: Message.From, tag: Any? = null) {
        messageEvent.value = EventType.WRITE to object : Message {
            override fun text(): String {
                return message
            }

            override fun from(): Message.From {
                return from
            }

            override fun tag(): Any? {
                return tag
            }
        }
    }

    fun remove(tag: Any, from: Message.From = Message.From.BOT) {
        messageEvent.value = EventType.REMOVE to object : Message {
            override fun text(): String {
                return ""
            }

            override fun from(): Message.From {
                return from
            }

            override fun tag(): Any? {
                return tag
            }
        }
    }

}

enum class EventType {
    WRITE, REMOVE
}

interface Message {
    enum class From {
        BOT, HUMAN
    }

    fun text(): String

    fun from(): From

    fun tag(): Any?

}