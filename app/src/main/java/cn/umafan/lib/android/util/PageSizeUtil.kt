package cn.umafan.lib.android.util

import android.content.SharedPreferences
import cn.umafan.lib.android.model.MyApplication
import org.json.JSONObject


object PageSizeUtil {
    private const val fileName = "page_size"

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    fun getSize(): Int {
        return sharedPreferences.getInt(
            "size",
            20
        )
    }

    fun setSize(size: Int): Boolean {
        return try {
            sharedPreferences.edit().putInt("size", size).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}