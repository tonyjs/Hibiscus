package com.tonyjs.hibiscus.data.model

data class Token(val accessToken: String, val refreshToken: String) {
    companion object {
        @JvmField val EMPTY = Token("", "")
    }

}