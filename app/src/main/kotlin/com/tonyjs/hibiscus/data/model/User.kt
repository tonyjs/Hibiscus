package com.tonyjs.hibiscus.data.model

data class User(var id: Long,
                var nickname: String,
                var token: String) {
    companion object {
        @JvmField val EMPTY = User(-1L, "", "")

        const val READY_TO_SET_ID = -1L
    }
}
