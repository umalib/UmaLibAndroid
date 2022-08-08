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

        fun getVersion(): VersionBean {
            val v = context.packageManager.getPackageInfo(context.packageName, 0)
            return VersionBean(
                v.versionCode,
                v.versionName
            )

        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}

data class VersionBean(
    val code: Int,
    val name: String
)