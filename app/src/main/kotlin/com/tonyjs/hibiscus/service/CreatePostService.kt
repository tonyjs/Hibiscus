package com.tonyjs.hibiscus.service

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.ui.ViewModelFactory
import com.tonyjs.hibiscus.ui.post.PostViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

//TODO
class CreatePostService : IntentService(TAG) {

    companion object {
        const val TAG = "CreatePostService"

        const val KEY_POST_ID = "postId"

        private const val EMPTY_POST_ID = -1L
    }

    override fun onHandleIntent(intent: Intent?) {
        val postId = intent?.getLongExtra(KEY_POST_ID, -1L)
        if (EMPTY_POST_ID == postId) {
            return
        }

        with(ViewModelFactory.from(application).create(PostViewModel::class.java)) {

        }
    }


}