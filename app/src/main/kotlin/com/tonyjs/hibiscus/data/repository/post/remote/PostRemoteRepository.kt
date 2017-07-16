package com.tonyjs.hibiscus.data.repository.post.remote

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tonyjs.hibiscus.LOG
import com.tonyjs.hibiscus.data.model.ImageBody
import com.tonyjs.hibiscus.data.model.Post
import com.tonyjs.hibiscus.data.model.TextBody
import com.tonyjs.hibiscus.data.network.api.TelegraphApi
import com.tonyjs.hibiscus.data.network.dto.UploadImageResponse
import com.tonyjs.hibiscus.service.CreatePostService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

//TODO
class PostFromTelegraphRepository(private val telegraphApi: TelegraphApi,
                                  private val imageManager: RequestManager) : PostRemoteRepository {

    companion object {
        const val MAXIMUM_IMAGE_SIZE = 2048
    }

    override fun saveAndGet(post: Post): Single<Post> {
        return Single.fromCallable {
            Observable.fromIterable(post.bodies)
                    .filter { it is ImageBody }
                    .cast(ImageBody::class.java)
                    .flatMapSingle { body ->
                        uploadImageBody(body)
                                .onErrorReturnItem(UploadImageResponse(""))
                                .doOnSuccess { body.externalPath = it.src }
                    }
                    .subscribe()

            val array = JsonArray()
            post.bodies.forEach {
                if (it is TextBody) {
                    array.add(toTextContent(it))
                } else if (it is ImageBody) {
                    array.add(toImageContent(it))
                }
            }

            telegraphApi.createPage(post.owner.token, post.title, post.owner.nickname, Gson().toJson(array))
                    .subscribe()

            post
        }
    }

    fun toTextContent(textBody: TextBody): String {
        val json = JsonObject().apply {
            addProperty("tag", "p")
            add("children", JsonArray().apply { add(textBody.text) })
        }
        return Gson().toJson(json)
    }

    fun toImageContent(imageBody: ImageBody): String {
        val json = JsonObject().apply {
            addProperty("tag", "img")
            add("attrs", JsonObject().apply { addProperty("src", imageBody.externalPath) })
        }
        return Gson().toJson(json)
    }

    private fun uploadImageBody(body: ImageBody): Single<UploadImageResponse> {
        val maximumSize = getMaximumSize(body.width, body.height)
        val file = getResizedImageFile(
                File(body.path), maximumSize.first, maximumSize.second)
        val requestBody = RequestBody.create(MediaType.parse(body.contentType), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return telegraphApi.uploadImage(filePart)
                .map { it[0] }
    }

    fun getMaximumSize(width: Int, height: Int): Pair<Int, Int> {
        if (width <= MAXIMUM_IMAGE_SIZE && height <= MAXIMUM_IMAGE_SIZE) {

            return width to height

        } else if (MAXIMUM_IMAGE_SIZE in height..(width - 1)) {

            val maximumWidth = MAXIMUM_IMAGE_SIZE
            val newHeight = maximumWidth * (height / width.toFloat())
            return maximumWidth to newHeight.toInt()

        } else if (MAXIMUM_IMAGE_SIZE in width..(height - 1)) {

            val maximumHeight = MAXIMUM_IMAGE_SIZE
            val newWidth = maximumHeight * (width / height.toFloat())
            return newWidth.toInt() to maximumHeight

        } else {
            return if (width > height) {

                val maximumWidth = MAXIMUM_IMAGE_SIZE
                val newHeight = maximumWidth * (height / width.toFloat())
                maximumWidth to newHeight.toInt()

            } else if (width < height) {

                val maximumHeight = MAXIMUM_IMAGE_SIZE
                val newWidth = maximumHeight * (width / height.toFloat())
                newWidth.toInt() to maximumHeight

            } else {
                return MAXIMUM_IMAGE_SIZE to MAXIMUM_IMAGE_SIZE
            }
        }
    }

    fun getResizedImageFile(file: File, width: Int, height: Int): File {
        val bitmap = imageManager
                .load(file)
                .asBitmap()
                .centerCrop()
                .into(width, height)
                .get()

        val temp = createPictureFile()
        FileOutputStream(temp).use {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            } catch (e: Exception) {
                LOG.e("TAG", Log.getStackTraceString(e))
            }
        }

        return temp
    }

    fun createPictureFile(): File {
        return with(File(getPictureFolderPath())) {
            if (!exists()) {
                mkdirs()
            }

            File.createTempFile("picture", ".jpg", this)
        }
    }

    fun getPictureFolderPath(): String {
        return "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/Hibiscus"
    }

}

interface PostRemoteRepository {

    fun saveAndGet(post: Post): Single<Post>

}