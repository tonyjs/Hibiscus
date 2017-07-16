package com.tonyjs.hibiscus.ui.post.list

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.*
import android.view.*
import com.tonyjs.hibiscus.LOG
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.ui.navigation.NavigationTo
import com.tonyjs.hibiscus.ui.navigation.NavigationViewModel
import com.tonyjs.hibiscus.ui.post.PostViewModel
import com.tonyjs.hibiscus.ui.ViewModelFactory
import com.tonyjs.hibiscus.ui.base.BaseFragment
import com.tonyjs.hibiscus.ui.post.list.adapter.PostListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_post_list.*
import kotlinx.android.synthetic.main.fragment_post_list.view.*
import org.jetbrains.anko.childrenSequence
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class PostListFragment : BaseFragment() {

    companion object {
        const val FETCH_LIMIT_OF_ITEMS = 50

        const val TAG = "PostListFragment"
    }

    private lateinit var postViewModel: PostViewModel

    private lateinit var navigationViewModel: NavigationViewModel

    private var adapter: PostListAdapter? = null

    override val pageLayoutResId: Int
        get() = R.layout.fragment_post_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupPostViewModel()
        setupNavigationViewModel()

        adapter = PostListAdapter().apply {
            preLoader = { offset ->
                loadPosts(offset)
            }
        }

        with(view.list) {
            layoutManager =
                    LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false).apply {
                        stackFromEnd = true
                    }
            PagerSnapHelper().attachToRecyclerView(this)

            adapter = this@PostListFragment.adapter

            addOnScrollListener(ScrollListenerForScaleAnimation())
        }

        onClick(btnCreatePost) {
            navigationViewModel.moveTo(NavigationTo.CREATE_POST, duplicatable = true)
        }

        loadPosts()
    }

    private fun setupNavigationViewModel() {
        navigationViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(NavigationViewModel::class.java)
    }

    private fun setupPostViewModel() {
        postViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(PostViewModel::class.java)
        postViewModel.createEvent.observe(this, Observer {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                it?.let { post -> addPost(post) }
            }
        })
    }

    private fun loadPosts(offset: Long = Long.MAX_VALUE) {
        postViewModel.getPostsOrderByDesc(offset = offset, limit = FETCH_LIMIT_OF_ITEMS)
                .map { it.reversed() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showPosts(it)
                }, { error ->
                    LOG.e(TAG, error)
                    if (offset == Long.MAX_VALUE) {
                        alert(getString(R.string.error_review_please),
                                getString(R.string.error_on_post_list), init = {
                            okButton { }
                        })
                    }
                })
    }

    private fun showPosts(posts: List<Post>) {
        if (posts.isEmpty()) {
            return
        }
        adapter?.run {
            this.posts.addAll(0, posts)
            notifyItemRangeInserted(0, posts.size)
        }
    }

    private fun addPost(post: Post) {
        adapter?.run {
            posts.add(post)
            notifyItemInserted(itemCount - 1)
            view?.list?.run {
                smoothScrollToPosition(itemCount - 1)
            }
        }
    }

    override fun onDestroyView() {
        adapter?.preLoader = null
        adapter?.viewCaches?.clear()
        adapter = null
        super.onDestroyView()
    }

    class ScrollListenerForScaleAnimation : RecyclerView.OnScrollListener() {

        companion object {
            const val MAX_SCALE = 1.05f
            const val DEFAULT_ELEVATION = 1f
        }

        private var isFirstEvent = true

        private fun setupToDefault(recyclerView: RecyclerView,
                                   x: Float, y: Float,
                                   scale: Float,
                                   elevation: Float) {
            recyclerView.findChildViewUnder(x, y)?.run {
                ViewCompat.setElevation(this, elevation)
                scaleX = scale
                scaleY = scale
                this
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val x = (recyclerView.right - recyclerView.left).div(2).toFloat()
                val y = (recyclerView.bottom - recyclerView.top).div(2).toFloat()
                setupToDefault(recyclerView, x, y, MAX_SCALE, DEFAULT_ELEVATION)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val x = (recyclerView.right - recyclerView.left).div(2).toFloat()
            val y = (recyclerView.bottom - recyclerView.top).div(2).toFloat()

            if (isFirstEvent) {
                setupToDefault(recyclerView, x, y, MAX_SCALE, DEFAULT_ELEVATION)
                isFirstEvent = false
                return
            }

            val view = recyclerView.findChildViewUnder(x, y)?.run {
                pivotX = (right - left) / 2.toFloat()
                pivotY = (bottom - top) / 2.toFloat()

                ViewCompat.setElevation(this, DEFAULT_ELEVATION)
                scaleX = Math.min(scaleX + 0.01f, MAX_SCALE)
                scaleY = Math.min(scaleY + 0.01f, MAX_SCALE)
                this
            }

            recyclerView.childrenSequence()
                    .filter { it != view }
                    .forEach {
                        ViewCompat.setElevation(it, 0f)
                        it.scaleX = 1.0f
                        it.scaleY = 1.0f
                    }
        }
    }
}