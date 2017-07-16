package com.tonyjs.hibiscus.data

import com.tonyjs.hibiscus.AppModule
import com.tonyjs.hibiscus.data.database.DatabaseModule
import com.tonyjs.hibiscus.data.mapper.MapperModule
import com.tonyjs.hibiscus.data.network.NetworkModule
import com.tonyjs.hibiscus.data.repository.RepositoryModule
import com.tonyjs.hibiscus.ui.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        RepositoryModule::class,
        MapperModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        AppModule::class))
interface DataComponent {

    fun inject(viewModelFactory: ViewModelFactory)

}