package com.tonyjs.hibiscus.data.repository

import android.content.ContentResolver
import android.content.Context
import com.bumptech.glide.Glide
import com.tonyjs.hibiscus.data.mapper.post.PostEntityMapper
import com.tonyjs.hibiscus.data.mapper.post.UserEntityMapper
import com.tonyjs.hibiscus.data.network.api.TelegraphApi
import com.tonyjs.hibiscus.data.repository.user.local.UserFromPreferencesRepository
import com.tonyjs.hibiscus.data.repository.user.local.UserLocalRepository
import com.tonyjs.hibiscus.data.repository.user.remote.UserFromTelegraphRepository
import com.tonyjs.hibiscus.data.repository.user.remote.UserRemoteRepository
import com.tonyjs.hibiscus.data.repository.photo.local.PhotoFromContentRepository
import com.tonyjs.hibiscus.data.repository.photo.local.PhotoLocalRepository
import com.tonyjs.hibiscus.data.repository.post.local.PostFromDatabaseRepository
import com.tonyjs.hibiscus.data.repository.post.local.PostLocalRepository
import com.tonyjs.hibiscus.data.repository.post.remote.PostFromTelegraphRepository
import com.tonyjs.hibiscus.data.repository.post.remote.PostRemoteRepository
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRemoteRepository(retrofit: Retrofit): UserRemoteRepository =
            UserFromTelegraphRepository(retrofit.create(TelegraphApi::class.java))

    @Provides
    @Singleton
    fun provideUserLocalRepository(entityDataStore: KotlinEntityDataStore<Persistable>,
                                   userEntityMapper: UserEntityMapper): UserLocalRepository =
            UserFromPreferencesRepository(entityDataStore, userEntityMapper)

    @Provides
    @Singleton
    fun providePostLocalRepository(entityDataStore: KotlinEntityDataStore<Persistable>,
                                   postEntityMapper: PostEntityMapper): PostLocalRepository =
            PostFromDatabaseRepository(entityDataStore, postEntityMapper)

    @Provides
    @Singleton
    fun providePostRemoteRepository(retrofit: Retrofit, context: Context): PostRemoteRepository =
            PostFromTelegraphRepository(
                    retrofit.create(TelegraphApi::class.java), Glide.with(context))

    @Provides
    @Singleton
    fun providePhotoLocalRepository(contentResolver: ContentResolver): PhotoLocalRepository =
            PhotoFromContentRepository(contentResolver)


}