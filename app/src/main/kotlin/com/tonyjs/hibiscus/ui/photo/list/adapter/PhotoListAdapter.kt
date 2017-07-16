package com.tonyjs.hibiscus.ui.photo.list.adapter

import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Photo
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.photo.list.adapter.binder.PhotoViewBinder

class PhotoListAdapter : MultiViewTypeAdapter() {

    companion object {
        const val VIEW_TYPE_IMAGE = 0
        const val VIEW_TYPE_PROGRESS = 1

        const val PRE_LOAD_POSITION_OFFSET = 10
    }

    var preLoader: PreLoader? = null

    var preLoadRequired = true

    override fun registerBinders() {
        val verticalBinder = PhotoViewBinder()
        registerBinder(VIEW_TYPE_IMAGE, verticalBinder.layoutResId, verticalBinder.binder)

        registerBinder<Any>(VIEW_TYPE_PROGRESS, R.layout.item_photo_progress, { _, _ -> })
    }

    override fun onBindViewHolder(holder: MultiViewTypeAdapter.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_PROGRESS) {
            return
        }

        super.onBindViewHolder(holder, position)
        (getItem(position) as? Photo)?.let {
            if (position == getPreLoadPosition()) {
                items.map { it.first }.lastOrNull { it is Photo }?.run {
                    preLoader?.preLoad((this as Photo).id)
                }
            }
        }
    }

    fun addPhotos(list: List<Photo>) {
        list.filter {
            val isGif = it.path.contains("gif") || it.mimeType?.contains("gif") ?: false
            !isGif
        }.forEach {
            addItem(it to VIEW_TYPE_IMAGE)
        }
    }

    private fun getPreLoadPosition(): Int {
        return if (itemCount < PRE_LOAD_POSITION_OFFSET) itemCount - 1
        else itemCount - PRE_LOAD_POSITION_OFFSET
    }

    override fun getItemViewType(position: Int): Int {
        if (preLoadRequired) {
            return if (position == itemCount - 1) VIEW_TYPE_PROGRESS
            else super.getItemViewType(position)
        }
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        return if (preLoadRequired) itemCount.plus(1) else itemCount
    }

    interface PreLoader {
        fun preLoad(offset: Long)
    }
}