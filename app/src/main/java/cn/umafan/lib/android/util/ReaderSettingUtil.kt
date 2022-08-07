package cn.umafan.lib.android.util

import android.content.SharedPreferences
import cn.umafan.lib.android.model.MyApplication
import org.json.JSONObject
import java.lang.Exception


object ReaderSettingUtil {

    private const val fileName = "reader_setting"

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    fun getSetting(theme: String): JSONObject {
        return JSONObject(sharedPreferences.getString(theme, "{\"fontSize\": \"normal\", \"segmentSpace\": \"wider\"}")!!)
    }

    fun setSetting(theme: String, fontSize: String, segmentSpace: String): Boolean {
        val map = mutableMapOf(Pair("fontSize", fontSize), Pair("segmentSpace", segmentSpace))
        val json = JSONObject(map as Map<*, *>?)
        return try {
            sharedPreferences.edit().putString(theme, json.toString()).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}