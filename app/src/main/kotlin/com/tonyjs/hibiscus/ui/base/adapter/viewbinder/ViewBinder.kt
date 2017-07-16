package com.tonyjs.hibiscus.ui.base.adapter.viewbinder

import android.support.annotation.LayoutRes
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter

abstract class ViewBinder<in ITEM : Any> {

    @get:LayoutRes
    abstract val layoutResId: Int

    abstract val binder: (MultiViewTypeAdapter.ViewHolder, ITEM) -> Unit

}