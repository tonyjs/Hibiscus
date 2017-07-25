package com.tonyjs.hibiscus.ui.post.create

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tonyjs.hibiscus.LOG
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.data.model.Photo
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.data.model.TextBody
import com.tonyjs.hibiscus.ui.*
import com.tonyjs.hibiscus.ui.base.BaseFragment
import com.tonyjs.hibiscus.ui.navigation.NavigationTo
import com.tonyjs.hibiscus.ui.navigation.NavigationViewModel
import com.tonyjs.hibiscus.ui.photo.list.PhotoListFragment
import com.tonyjs.hibiscus.ui.photo.list.PhotoViewModel
import com.tonyjs.hibiscus.ui.post.PostViewModel
import com.tonyjs.hibiscus.ui.user.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.item_create_post_image.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.textChangedListener
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

class CreatePostFragment : BaseFragment() {

    companion object {
        const val TAG = "CreatePostFragment"
    }

    private lateinit var post: Post

    private lateinit var navigationViewModel: NavigationViewModel

    private lateinit var userViewModel: UserViewModel

    private lateinit var postViewModel: PostViewModel

    private lateinit var photoViewModel: PhotoViewModel

    override val pageLayoutResId: Int
        get() = R.layout.fragment_create_post

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup user
        userViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(UserViewModel::class.java)

        userViewModel.loadUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    post = Post.temp(it)
                    setupViews()
                }, { error ->
                    LOG.e(TAG, error)
                    alert(getString(R.string.error_review_please),
                            getString(R.string.error_on_create_post_load), init = {
                        okButton {
                            close()
                        }
                    })
                })
    }

    private fun setupViews() {
        setupNavigationViewModel()
        setupPostViewModel()
        setupPhotoViewModel()

        setupTitleEditor()
        setupTextEditor()
        setupUserActionListeners()
    }

    private fun setupPostViewModel() {
        postViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(PostViewModel::class.java)
    }

    private fun setupNavigationViewModel() {
        navigationViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(NavigationViewModel::class.java)
    }

    private fun setupPhotoViewModel() {
        photoViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(PhotoViewModel::class.java)
        photoViewModel.pickEvent
                .observe(this, Observer {
                    if (it == null) {
                        return@Observer
                    }

                    hidePhotoList()
                    setupImageEditor(it)
                    photoViewModel.pickEvent.value = null
                })
    }

    private fun setupUserActionListeners() {
        onClick(frame) {
            val lastEditText = frame.childrenSequence().lastOrNull() as? EditText
            val ableToAdd = lastEditText == null || lastEditText.text.isNotBlank()
            if (ableToAdd) {
                setupTextEditor(shouldFocus = true)
            }
        }

        onClick(btnCreatePost) {
            if (post.title.isNullOrBlank()) {
                toast(R.string.please_input_title)
                return@onClick
            }

            btnCreatePost.isEnabled = false
            createPost()
        }

        onClick(btnAddImage) {
            hideSoftInput(frame.findFocus() ?: run {
                val lastChild = frame.getChildAt(frame.childCount - 1)
                lastChild.requestFocus()
                lastChild
            })

            checkStoragePermissionFirst { showPhotoList() }
        }
    }

    private fun createPost() {
        postViewModel.createAndGet(post)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { postViewModel.createSuccessEvent.value = it }
                .subscribe({
                    close()
                    navigationViewModel.moveTo(NavigationTo.POST_LIST)
                }, { error ->
                    LOG.e(TAG, error)
                    alert(getString(R.string.error_review_please),
                            getString(R.string.error_on_create_post), init = {
                        okButton {
                            btnCreatePost.isEnabled = true
                        }
                    })
                })
    }

    private fun showPhotoList() {
        postToMainThread(runCondition = { isAlive(lifecycle) }) {
            childFragmentManager.findFragmentByTag(PhotoListFragment.TAG)?.let {
                (it as PhotoListFragment).dialog.show()
                return@postToMainThread
            }

            PhotoListFragment().show(childFragmentManager, PhotoListFragment.TAG)
        }
    }

    private fun hidePhotoList() {
        postToMainThread(runCondition = { isAlive(lifecycle) }) {
            childFragmentManager.findFragmentByTag(PhotoListFragment.TAG)?.let {
                (it as PhotoListFragment).dialog.hide()
            }
        }
    }

    @SuppressLint("NewApi")
    inline fun checkStoragePermissionFirst(crossinline job: () -> Unit) {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (hasPermission(permission)) {
            job()
        } else {
            requestPermissions(arrayOf(permission), 1)
        }
    }

    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showPhotoList()
        }
    }

    override fun onDestroyView() {
        hideSoftInput(frame.findFocus() ?: run {
            val lastChild = frame.getChildAt(frame.childCount - 1)
            lastChild.requestFocus()
            lastChild
        })
        super.onDestroyView()
    }

    private fun setupTitleEditor() {
        frame.themedEditText(R.style.EditText_Post_Form) {
            val margin = dip(8f)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams = LinearLayout.LayoutParams(width, height).apply {
                setMargins(margin, margin, margin, margin)
            }
            hint = context.getString(R.string.input_title)
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            textColor = Color.BLACK
            isSaveEnabled = false

            textChangedListener {
                onTextChanged { charSequence, _, _, _ ->
                    post.title = charSequence?.toString() ?: ""
                }
            }

            requestFocus()
            showSoftInput(this)
        }
    }

    private fun setupTextEditor(shouldFocus: Boolean = false) {
        frame.themedEditText(R.style.EditText_Post_Form) {
            val seq = post.bodies.size.plus(1)
            val textBody = TextBody("", seq)
            post.bodies.add(textBody)

            val marginHorizontal = dip(2f)
            val marginVertical = dip(8f)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams = LinearLayout.LayoutParams(width, height).apply {
                setMargins(marginVertical, marginHorizontal, marginVertical, marginHorizontal)
            }
            hint = context.getString(R.string.input_text)
            textSize = 14f
            textColor = ContextCompat.getColor(context, R.color.color_light_black)
            isSaveEnabled = false

            textChangedListener {
                onTextChanged { charSequence, _, _, _ ->
                    textBody.text = charSequence?.toString() ?: ""
                }
            }

            if (shouldFocus) {
                requestFocus()
                showSoftInput(this)
            }
        }
    }

    private fun setupImageEditor(photo: Photo) {
        if (photo.width == 0 || photo.height == 0) {
            determineImageSize(photo, {
                setupNextImageBodyEditor(photo)
            })
        } else {
            setupNextImageBodyEditor(photo)
        }
    }

    private fun determineImageSize(photo: Photo, success: () -> Unit) {
        Glide.with(this)
                .load(photo.path)
                .listener(object : RequestListener<String, GlideDrawable> {
                    override fun onResourceReady(resource: GlideDrawable?, model: String?,
                                                 target: Target<GlideDrawable>?,
                                                 isFromMemoryCache: Boolean,
                                                 isFirstResource: Boolean): Boolean {
                        photo.width = resource?.intrinsicWidth ?: 0
                        photo.height = resource?.intrinsicHeight ?: 0
                        success()
                        return true
                    }

                    override fun onException(e: Exception?, model: String?,
                                             target: Target<GlideDrawable>?,
                                             isFirstResource: Boolean): Boolean {
                        return false
                    }

                })
                .into(ivFake)
    }

    private fun setupNextImageBodyEditor(photo: Photo) {
        val imageBody = ImageBody(seq = post.bodies.size,
                path = photo.path, contentType = photo.mimeType ?: "",
                width = photo.width, height = photo.height, orientation = photo.orientation)
        post.bodies.add(imageBody)

        frame.run {
            childrenSequence()
                    .forEach {
                        it.clearFocus()
                    }
            val root = this

            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_create_post_image, root, false)
            addView(view)

            val imageInfo = ImageInfo(imageBody.width, imageBody.height, imageBody.orientation)
            val ratio = imageInfo.ratio

            view.ivImage.ratio = ratio
            view.ivImage.requestLayout()

            post {
                Glide.with(context)
                        .load(photo.path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(FitCenter(context))
                        .into(view.ivImage)

                scroll.smoothScrollBy(0, view.measuredHeight)
            }

            onClick(view.delete) {
                post.bodies.remove(imageBody)
                root.removeView(view)
            }
        }
    }

}