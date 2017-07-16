package com.tonyjs.hibiscus

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class AppModule(val application: Application) {

    @Provides
    fun provideContext(): Context = application

    @Provides
    fun providePreferences(): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    fun provideContentResolver(): ContentResolver =
            application.contentResolver

}