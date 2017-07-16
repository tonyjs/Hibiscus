package com.tonyjs.hibiscus.data.database

import android.content.Context
import com.tonyjs.hibiscus.data.database.entity.Models
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): KotlinEntityDataStore<Persistable> {
        return KotlinEntityDataStore(DatabaseSource(context, Models.DEFAULT, 2).configuration)
    }

}