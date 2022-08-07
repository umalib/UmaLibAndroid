package cn.umafan.lib.android.util

import android.content.SharedPreferences
import cn.umafan.lib.android.model.MyApplication


object FavoriteArticleUtil {

    private const val fileName = "favorites"

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    /**
     * 保存到收藏
     */
    fun saveFavorite(id: Int, name: String): Boolean {
        return try {
            val editor = sharedPreferences.edit()
            editor.putString(id.toString(), name)
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 取消收藏
     */
    fun cancelFavorite(id: Int): Boolean {
        return try {
            val editor = sharedPreferences.edit()
            editor.remove(id.toString())
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取所有收藏
     */
    fun getFavorites(): List<Int> {
        return sharedPreferences.all.map {
            it.key.toInt()
        }
    }

    /**
     * 验证是否存在此收藏
     */
    fun existsFavorite(id: Int, name: String): Boolean {
        return try {
            val mName = sharedPreferences.getString(id.toString(), "")!!
            mName == name
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}