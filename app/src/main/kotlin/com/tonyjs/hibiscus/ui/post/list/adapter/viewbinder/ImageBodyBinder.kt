package com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.Request
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.ui.ImageInfo
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import kotlinx.android.synthetic.main.item_body_image.view.*

object ImageBodyBinder : ViewBinder<ImageBody>() {

    override val layoutResId: Int
        get() = R.layout.item_body_image

    override val binder: (MultiViewTypeAdapter.ViewHolder, ImageBody) -> Unit
        get() = { holder, body ->
            with(holder.itemView) {
                fun loadImage() {
                    Glide.with(context)
                            .load(body.path)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(FitCenter(context))
                            .into(ivThumb)
                }

                val imageInfo = ImageInfo(body.width, body.height, body.orientation)
                val shouldResetRatio = ivThumb.ratio != imageInfo.ratio
                if (shouldResetRatio) {
                    ivThumb.ratio = imageInfo.ratio
                    ivThumb.requestLayout()
                }

                val path = body.bestPath
                ivThumb.getTag(R.id.tag_id_for_app)?.run {
                    if (equals(path)) {
                        (ivThumb.getTag(R.id.tag_id_for_glide) as? Request)?.clear()
                        Glide.clear(ivThumb)
                    }
                }

                ivThumb.setTag(R.id.tag_id_for_app, path)

                if (shouldResetRatio) {
                    ivThumb.post { loadImage() }
                } else {
                    loadImage()
                }

            }
        }
}