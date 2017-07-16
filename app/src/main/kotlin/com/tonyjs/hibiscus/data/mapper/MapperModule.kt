package com.tonyjs.hibiscus.data.mapper

import com.tonyjs.hibiscus.data.mapper.post.PostEntityMapper
import com.tonyjs.hibiscus.data.mapper.post.UserEntityMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MapperModule {

    @Provides
    @Singleton
    fun provideUserEntityMapper() = UserEntityMapper()

    @Provides
    @Singleton
    fun providePostEntityMapper(userEntityMapper: UserEntityMapper) = PostEntityMapper(userEntityMapper)

}
