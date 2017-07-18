package com.tonyjs.hibiscus.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

fun postToMainThread(runCondition: () -> Boolean, delayMillis: Long = 0L,
                     job: () -> Unit) {
    val withCondition = {
        if (runCondition.invoke()) {
            job()
        }
    }
    AndroidSchedulers.mainThread()
            .scheduleDirect(withCondition, delayMillis, TimeUnit.MILLISECONDS)
}

fun isAlive(lifecycleRegistry: LifecycleRegistry): Boolean {
    return lifecycleRegistry.currentState != Lifecycle.State.DESTROYED
}

fun showSoftInput(view: View?) {
    view?.requestFocus()

    view?.post {
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.showSoftInput(view, 0)
    }
}

fun hideSoftInput(view: View?) {
    view?.run {
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun pixelFromDP(context: Context, dp: Float): Int {
    return (context.resources.displayMetrics.density * dp).toInt()
}

class RotationTransformation(context: Context, val degree: Int) : BitmapTransformation(context) {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val matrix = Matrix()

        matrix.postRotate(degree.toFloat())

        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.width, toTransform.height, matrix, true)
    }

    override fun getId(): String {
        return "RotationTransformation" + degree
    }

}

data class ImageInfo(val width: Int, val height: Int, val orientation: Int) {

    companion object {
        const val DEFAULT_RATIO = 1.0f
    }

    val isVerticalImage: Boolean
        get() = orientedSize.first < orientedSize.second

    val orientedSize: Pair<Int, Int> by lazy {
        var imageWidth = width
        var imageHeight = height

        if (orientation == 90 || orientation == 270) {
            val temp = imageHeight
            imageHeight = imageWidth
            imageWidth = temp
        }

        imageWidth to imageHeight
    }

    val ratio: Float by lazy {
        val width = orientedSize.first
        val height = orientedSize.second
        if (width == 0 || height == 0) {
            DEFAULT_RATIO
        } else {
            height / width.toFloat()
        }
    }
}