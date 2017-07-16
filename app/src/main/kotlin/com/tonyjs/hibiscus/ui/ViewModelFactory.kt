package com.tonyjs.hibiscus.ui

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tonyjs.hibiscus.AppModule
import com.tonyjs.hibiscus.data.DaggerDataComponent
import com.tonyjs.hibiscus.data.mapper.MapperModule
import com.tonyjs.hibiscus.data.network.NetworkModule
import com.tonyjs.hibiscus.data.repository.RepositoryModule
import com.tonyjs.hibiscus.data.repository.user.local.UserLocalRepository
import com.tonyjs.hibiscus.data.repository.user.remote.UserRemoteRepository
import com.tonyjs.hibiscus.data.repository.photo.local.PhotoLocalRepository
import com.tonyjs.hibiscus.data.repository.post.local.PostLocalRepository
import com.tonyjs.hibiscus.data.repository.post.remote.PostRemoteRepository
import com.tonyjs.hibiscus.ui.message.MessageViewModel
import com.tonyjs.hibiscus.ui.navigation.NavigationViewModel
import com.tonyjs.hibiscus.ui.photo.list.PhotoViewModel
import com.tonyjs.hibiscus.ui.post.PostViewModel
import com.tonyjs.hibiscus.ui.user.UserViewModel
import javax.inject.Inject

class ViewModelFactory private constructor(val application: Application) : ViewModelProvider.Factory {

    companion object {
        @JvmField var instance: ViewModelFactory? = null

        fun from(application: Application): ViewModelFactory {
            return if (instance != null) instance!!
            else {
                instance = ViewModelFactory(application)
                instance!!
            }
        }
    }

    init {
        DaggerDataComponent.builder()
                .appModule(AppModule(application))
                .networkModule(NetworkModule())
                .mapperModule(MapperModule())
                .repositoryModule(RepositoryModule())
                .build()
                .inject(this)
    }

    @Inject
    lateinit var userRemoteRepository: UserRemoteRepository

    @Inject
    lateinit var userLocalRepository: UserLocalRepository

    @Inject
    lateinit var postLocalRepository: PostLocalRepository

    @Inject
    lateinit var postRemoteRepository: PostRemoteRepository

    @Inject
    lateinit var photoLocalRepository: PhotoLocalRepository

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val viewModel = if (modelClass.isAssignableFrom(NavigationViewModel::class.java)) {
            NavigationViewModel()

        } else if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            UserViewModel(userRemoteRepository, userLocalRepository)

        } else if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            PostViewModel(postLocalRepository, postRemoteRepository)

        } else if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            PhotoViewModel(photoLocalRepository)

        } else if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            MessageViewModel()

        } else {
            object : ViewModel() {}
        }
        return viewModel as T
    }
}
