package com.tonyjs.hibiscus.ui.post.list.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.ui.pixelFromDP
import kotlinx.android.synthetic.main.item_post.view.*

class PostListAdapter : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    companion object {
        const val PRE_LOAD_POSITION_OFFSET = 5
    }

    val posts = mutableListOf<Post>()


    var preLoader: ((Long) -> Unit)? = null

    var viewCaches = hashMapOf<Long, View>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val frame = FrameLayout(parent.context)
        val width = pixelFromDP(parent.context, 300f)
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        frame.layoutParams = ViewGroup.LayoutParams(width, height)
        frame.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        return ViewHolder(frame)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        (holder.itemView as? ViewGroup)?.run itemView@ {
            if (childCount <= 0) {

                setupBodies(this@itemView, post)

            } else if (childCount == 1) {
                val child = getChildAt(0)
                if (post != (child.tag as? Post)) {
                    this@itemView.removeView(child)

                    setupBodies(this@itemView, post)
                } else {

                }
            } else {
                this@itemView.removeAllViews()
                viewCaches[post.id]?.let {
                    this@itemView.addView(it)
                } ?: let {
                    setupBodies(this@itemView, post)
                }
            }
        }

        loadPreviousPageIfNeed(position)
    }

    private fun setupBodies(parent: ViewGroup, post: Post) {
        with(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)) body@ {
            this@body.list.layoutManager = LinearLayoutManager(context).apply {
                isAutoMeasureEnabled = true
            }
            this@body.list.adapter = PostBodyAdapter(post)
            this@body.tag = post
            parent.addView(this@body)
            viewCaches.put(post.id, this@body)
        }
    }

    var requestedOffsets = mutableListOf<Long>()

    private fun loadPreviousPageIfNeed(position: Int) {
        if (position >= PRE_LOAD_POSITION_OFFSET) {
            return
        }

        val offset = posts[0].id

        if (requestedOffsets.contains(offset)) {
            return
        }

        requestedOffsets.add(offset)
        preLoader?.invoke(offset)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
