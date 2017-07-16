package com.tonyjs.hibiscus.ui.photo.list

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import com.tonyjs.hibiscus.LOG
import com.tonyjs.hibiscus.R
import com.tonyjs.hibiscus.data.model.Photo
import com.tonyjs.hibiscus.ui.ViewModelFactory
import com.tonyjs.hibiscus.ui.photo.list.adapter.PhotoListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class PhotoListFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "PhotoListFragment"
    }

    private var adapter: PhotoListAdapter? = null

    private lateinit var photoViewModel: PhotoViewModel

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val root = LayoutInflater.from(context).inflate(R.layout.fragment_photo_list, null)
        dialog.setContentView(root)

        root.list.layoutManager =
                StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL).apply {
                    isAutoMeasureEnabled = true
                }
        adapter = PhotoListAdapter().apply {
            preLoader = ListPreLoader()
        }
        root.list.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        photoViewModel = ViewModelProviders.of(activity, ViewModelFactory.from(activity.application))
                .get(PhotoViewModel::class.java)

        photoViewModel.findAll(limit = 60)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showPhotos(it)
                }, { error ->
                    // TODO
                    LOG.e(TAG, error)
                    alert(getString(R.string.error_review_please),
                            getString(R.string.error_on_photo_list), init = {
                        okButton {
                            this@PhotoListFragment.dismiss()
                        }
                    })
                })
    }

    private fun showPhotos(photos: List<Photo>) {
        adapter?.run {
            addPhotos(photos)
            notifyItemRangeInserted(itemCount, itemCount.plus(photos.size))
        }
    }

    inner class ListPreLoader : PhotoListAdapter.PreLoader {

        var targetingOffset = 0L

        override fun preLoad(offset: Long) {
            val preLoadRequired = adapter?.preLoadRequired ?: false
            if (!preLoadRequired || offset < 0 || targetingOffset == offset) {
                return
            }

            targetingOffset = offset
            photoViewModel.findAll(offset = targetingOffset, limit = 60)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LOG.d(TAG, it.joinToString())

                        if (it.isEmpty()) {
                            disablePreLoad()
                            return@subscribe
                        }

                        showPhotos(it)
                    }, { error ->
                        // TODO
                        disablePreLoad()
                        LOG.e(TAG, Log.getStackTraceString(error))
                    })
        }
    }

    private fun disablePreLoad() {
        adapter?.preLoadRequired = false
        adapter?.notifyDataSetChanged()
    }

}