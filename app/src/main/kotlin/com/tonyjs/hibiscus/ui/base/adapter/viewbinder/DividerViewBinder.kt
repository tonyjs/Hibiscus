package com.tonyjs.hibiscus.ui.base.adapter.viewbinder

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.R

object DividerViewBinder : ViewBinder<DividerInfo>() {

    override val layoutResId: Int
        get() = R.layout.item_divider

    override val binder: (MultiViewTypeAdapter.ViewHolder, DividerInfo) -> Unit
        get() = { holder, (heightDp, requireColor) ->
            val itemView = holder.itemView

            val newHeight = pixelFromDp(itemView.context, heightDp.toFloat()).toInt()
            if (itemView.layoutParams.height != newHeight) {
                itemView.layoutParams.height = newHeight
            }

            if (itemView.background == null || itemView.background !is ColorDrawable) {
                val colorDrawable = ColorDrawable(requireColor)
                itemView.background = colorDrawable
            } else {
                val colorDrawable = itemView.background as ColorDrawable
                if (colorDrawable.color != requireColor) {
                    colorDrawable.color = requireColor
                    itemView.background = colorDrawable
                }
            }
        }

    fun pixelFromDp(context: Context, dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }

}

data class DividerInfo(val heightDp: Int, val color: Int)
