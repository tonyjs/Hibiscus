package com.tonyjs.hibiscus

import android.support.multidex.MultiDexApplication
import com.bumptech.glide.request.target.ViewTarget
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        setupGlide()

        if (BuildConfig.DEBUG) {
            setupLeakCanary()
            setupStetho()
        }
    }

    private fun setupGlide() {
        try {
            ViewTarget.setTagId(R.id.tag_id_for_glide)
        } catch (e: Exception) {

        }
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun setupStetho() {
        Stetho.initializeWithDefaults(this)
    }

}

