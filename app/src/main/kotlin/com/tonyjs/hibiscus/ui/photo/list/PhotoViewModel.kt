package com.tonyjs.hibiscus.ui.photo.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.tonyjs.hibiscus.data.model.Photo
import com.tonyjs.hibiscus.data.repository.photo.local.PhotoLocalRepository
import io.reactivex.Single

class PhotoViewModel(private val photoLocalRepository: PhotoLocalRepository) : ViewModel() {

    val pickEvent = MutableLiveData<Photo>()

    fun findAll(offset: Long = -1L, limit: Int = Int.MAX_VALUE): Single<List<Photo>> {
        return photoLocalRepository.findAll(offset, limit)
    }

}
