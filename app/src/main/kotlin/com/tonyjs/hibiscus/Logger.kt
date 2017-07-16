package com.tonyjs.hibiscus

import android.util.Log

object LOG : Logger {

    var logger: Logger = AndroidLogger()

    override fun w(tag: String, throwable: Throwable) {
        logger.w(tag, throwable)
    }

    override fun w(tag: String, message: String) {
        logger.w(tag, message)
    }

    override fun e(tag: String, throwable: Throwable) {
        logger.e(tag, throwable)
    }

    override fun e(tag: String, message: String) {
        logger.e(tag, message)
    }

    override fun d(tag: String, message: String) {
        logger.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        logger.i(tag, message)
    }

}

interface Logger {

    fun w(tag: String, throwable: Throwable)

    fun w(tag: String, message: String)

    fun e(tag: String, throwable: Throwable)

    fun e(tag: String, message: String)

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

}

class AndroidLogger : Logger {
    override fun w(tag: String, throwable: Throwable) {
        w(tag, Log.getStackTraceString(throwable))
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun e(tag: String, throwable: Throwable) {
        e(tag, Log.getStackTraceString(throwable))
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

}

class DefaultLogger : Logger {

    override fun w(tag: String, throwable: Throwable) {
        println("$tag - ${throwable.message}, ${throwable.cause?.message}")
    }

    override fun w(tag: String, message: String) {
        println("$tag - $message")
    }

    override fun e(tag: String, throwable: Throwable) {
        println("$tag - ${throwable.message}, ${throwable.cause?.message}")
    }

    override fun e(tag: String, message: String) {
        println("$tag - $message")
    }

    override fun d(tag: String, message: String) {
        println("$tag - $message")
    }

    override fun i(tag: String, message: String) {
        println("$tag - $message")
    }

}