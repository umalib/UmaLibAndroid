package cn.umafan.lib.android.util

import android.content.SharedPreferences
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.db.Article
import org.json.JSONArray
import org.json.JSONObject

object HistoryUtil {
    private const val fileName = "history"
    private const val maxLength = 20

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    /**
     * 保存到历史记录
     */
    fun saveHistory(article: Article): Boolean {
        return try {
            val editor = sharedPreferences.edit()
            val originData = JSONArray(sharedPreferences.getString(fileName, "[]")!!)
            for (i in 0 until originData.length()) {
                if (originData.getJSONObject(i).getInt("id") == article.id.toInt()) {
                    val data = originData.getJSONObject(i)
                    originData.remove(i)
                    originData.put(data)
                    editor.putString(fileName, originData.toString())
                    editor.apply()
                    return true
                }
            }
            if (originData.length() >= maxLength) {
                originData.remove(0)
            }
            val data = JSONObject()
            data.put("id", article.id.toInt())
            data.put("name", article.name)
            data.put("author", article.author)
            data.put("translator", article.translator)
            val tagList = JSONArray(article.taggedList.sortedWith { a, b ->
                if (a.tag.type == b.tag.type)
                    a.tag.name.compareTo(b.tag.name)
                else
                    b.tag.type.compareTo(a.tag.type)
            }.map { tagged -> tagged.tag.name })
            data.put("tags", tagList)
            originData.put(data)
            editor.putString(fileName, originData.toString())
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取历史记录
     */
    fun getHistory(): List<Int> {
        val originData = JSONArray(sharedPreferences.getString(fileName, "[]"))
        val data = mutableListOf<Int>()
        for (i in 0 until originData.length()) {
            data.add(originData.getJSONObject(originData.length() - i - 1).getInt("id"))
        }
        return data
    }
}