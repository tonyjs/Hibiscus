package com.tonyjs.hibiscus.data.mapper

interface ModelMapper<FROM, MODEL> {

    fun transform(from: FROM): MODEL

    fun reverse(from: MODEL): FROM

}