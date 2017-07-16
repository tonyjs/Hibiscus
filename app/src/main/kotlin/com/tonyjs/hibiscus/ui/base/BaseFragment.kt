package com.tonyjs.hibiscus.ui.base

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

abstract class BaseFragment : LifecycleFragment() {

    val disposables = CompositeDisposable()

    fun addDisposable(disposable: Disposable) = disposables.add(disposable)

    @get:LayoutRes
    abstract val pageLayoutResId: Int

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(pageLayoutResId, container, false)
    }

    abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    inline fun onClick(view: View, throttleTime: Long = 500, crossinline listener: () -> Unit) {
        view.clicks().throttleFirst(throttleTime, TimeUnit.MILLISECONDS)
                .subscribe { listener.invoke() }
                .apply { addDisposable(this) }
    }

    open fun close() {
        fragmentManager.beginTransaction().remove(this).commitNowAllowingStateLoss()
    }

}