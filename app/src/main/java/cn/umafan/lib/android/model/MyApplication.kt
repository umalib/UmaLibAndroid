package cn.umafan.lib.android.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val DATABASE_LOADING = 0
        const val DATABASE_LOADED = 1
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}