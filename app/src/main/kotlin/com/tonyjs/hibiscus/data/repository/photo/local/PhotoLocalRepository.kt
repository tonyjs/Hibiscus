package com.tonyjs.hibiscus.data.repository.photo.local

import android.content.ContentResolver
import android.provider.MediaStore
import com.tonyjs.hibiscus.data.model.Photo
import io.reactivex.Single
import java.util.ArrayList

class PhotoFromContentRepository(
        private val contentResolver: ContentResolver) : PhotoLocalRepository {

    override fun findAll(offset: Long, limit: Int): Single<List<Photo>> {

        return Single.fromCallable {
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.WIDTH,
                    MediaStore.Images.ImageColumns.HEIGHT,
                    MediaStore.Images.ImageColumns.ORIENTATION,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            )

            val orderBy = String.format("%s DESC", MediaStore.Images.ImageColumns._ID)

            val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendQueryParameter("limit", limit.toString()).build()

            val hasOffset = offset > 0L
            val selection =
                    if (hasOffset) String.format("%s < ?", MediaStore.Images.ImageColumns._ID)
                    else null
            val selectionArg = if (hasOffset) arrayOf(offset.toString()) else null

            val cursor = contentResolver.query(images,
                    projection,
                    selection,
                    selectionArg,
                    orderBy
            )

            val list = ArrayList<Photo>()
            if (cursor.columnCount <= 0) {
                cursor.close()
            } else {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                    val thumbPath = getThumbnailPath(contentResolver, id)
                    val path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    val width = cursor.getInt(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH))
                    val height = cursor.getInt(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT))
                    val orientation = cursor.getInt(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
                    val mimeType = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE))

                    val image = Photo(id, path, thumbPath, width, height, orientation, mimeType)
                    list.add(image)
                }
                cursor.close()
            }

            list
        }
    }

    private fun getThumbnailPath(contentResolver: ContentResolver, id: Long): String? {
        val cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(contentResolver, id,
                MediaStore.Images.Thumbnails.MINI_KIND, arrayOf(MediaStore.Images.Thumbnails.DATA))
        if (cursor.count <= 0) {
            cursor.close()
            return null
        }

        cursor.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA))
        cursor.close()
        return path
    }

}

interface PhotoLocalRepository {

    fun findAll(offset: Long = -1L, limit: Int = Int.MAX_VALUE): Single<List<Photo>>

}