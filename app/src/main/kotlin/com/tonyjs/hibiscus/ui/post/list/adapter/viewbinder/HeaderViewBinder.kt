package com.tonyjs.hibiscus.ui.post.list.adapter.viewbinder

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.PopupMenu
import android.view.View
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.ui.ViewModelFactory
import com.tonyjs.hibiscus.ui.base.adapter.MultiViewTypeAdapter
import com.tonyjs.hibiscus.ui.base.adapter.viewbinder.ViewBinder
import com.tonyjs.hibiscus.ui.post.PostViewModel
import kotlinx.android.synthetic.main.item_body_header.view.*
import org.jetbrains.anko.sdk25.listeners.onClick
import java.text.SimpleDateFormat

object HeaderViewBinder : ViewBinder<Post>() {

    override val layoutResId: Int
        get() = R.layout.item_body_header

    override val binder: (MultiViewTypeAdapter.ViewHolder, Post) -> Unit
        get() = Binder()

    private class Binder : (MultiViewTypeAdapter.ViewHolder, Post) -> Unit {
        override fun invoke(holder: MultiViewTypeAdapter.ViewHolder, post: Post) {
            with(holder.itemView) {
                tvTitle.text = post.title
                tvCreatedTime.text = SimpleDateFormat(Post.DEFAULT_DATE_FORMAT).format(post.createdTime)

                btnMore.onClick {
                    showMoreMenu(btnMore, post)
                }
            }
        }

        private fun showMoreMenu(button: View, post: Post) {
            val context = button.context
            PopupMenu(context, button).apply {
                menu.add(0, 1, 0, R.string.delete_post)
                setOnMenuItemClickListener {
                    (context as? FragmentActivity)?.run {
                        ViewModelProviders.of(this@run, ViewModelFactory.from(application))
                                .get(PostViewModel::class.java)
                                .callDeleteCommand(post)
                    }
                    true
                }
            }.show()
        }
    }

}