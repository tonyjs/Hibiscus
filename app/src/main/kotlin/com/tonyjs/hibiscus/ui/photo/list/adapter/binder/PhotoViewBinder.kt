package com.tonyjs.hibiscus.ui.photo.list.adapter.binder

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.Request
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import com.tonyjs.hibiscus.data.model.Photo
import com.tonyjs.hibiscus.ui.ImageInfo
import com.tonyjs.hibiscus.ui.RotationTransformation
import com.tonyjs.hibiscus.ui.ViewModelFactory
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.photo.list.PhotoViewModel
import kotlinx.android.synthetic.main.item_photo.view.*
import org.jetbrains.anko.sdk25.listeners.onClick

class PhotoViewBinder : ViewBinder<Photo>() {

    override val layoutResId: Int
        get() = R.layout.item_photo

    override val binder: (MultiViewTypeAdapter.ViewHolder, Photo) -> Unit
        get() = { holder, photo ->
            //FIXME really? clear?
            val activity = holder.itemView.context as FragmentActivity
            val photoViewModel =
                    ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                            .get(PhotoViewModel::class.java)
            holder.itemView.onClick {
                if (photoViewModel.pickEvent.value == null) {
                    photoViewModel.pickEvent.value = photo
                }
            }

            with(holder.itemView) {

                fun loadImage() {
                    val request = Glide.with(context)
                            .load(photo.path)
                            .asBitmap()
                            .dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    photo.thumbPath?.let {
                        request.thumbnail(Glide.with(context)
                                .load(it)
                                .asBitmap()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transform(RotationTransformation(context, photo.orientation)))
                    }
                    request.into(ivPhoto)
                }

                val imageInfo = ImageInfo(photo.width, photo.height, photo.orientation)
                val shouldResetRatio = ivPhoto.ratio != imageInfo.ratio
                if (shouldResetRatio) {
                    ivPhoto.ratio = imageInfo.ratio
                    ivPhoto.requestLayout()
                }

                val path = photo.path
                ivPhoto.getTag(R.id.tag_id_for_app)?.run {
                    if (equals(path)) {
                        (ivPhoto.getTag(R.id.tag_id_for_glide) as? Request)?.clear()
                        Glide.clear(ivPhoto)
                    }
                }

                ivPhoto.setTag(R.id.tag_id_for_app, path)

                if (shouldResetRatio) {
                    ivPhoto.post { loadImage() }
                } else {
                    loadImage()
                }
            }
        }

}