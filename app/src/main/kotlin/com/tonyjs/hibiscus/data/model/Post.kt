package com.tonyjs.hibiscus.data.model

import java.util.*

data class Post(var id: Long,
                var title: String,
                var createdTime: Date?,
                var owner: User,
                var bodies: MutableList<Body> = mutableListOf()) {
    companion object {
        const val READY_TO_SET_ID = -1L

        @JvmField val EMPTY = Post(READY_TO_SET_ID, "", null, User.EMPTY)

        fun temp(owner: User) = Post(READY_TO_SET_ID, "", null, owner)

        const val DEFAULT_DATE_FORMAT = "yyyy/MM/dd, HH:mm:ss"
    }
}

data class TextBody(var text: String,
                    override var seq: Int) : Body(seq)

data class ImageBody(var path: String,
                     var externalPath: String? = "",
                     override var seq: Int,
                     var contentType: String,
                     var width: Int,
                     var height: Int,
                     var orientation: Int) : Body(seq) {
    val bestPath: String
        get() {
            return if (!(externalPath.isNullOrBlank())) externalPath!! else path
        }
}

sealed class Body(open var seq: Int)