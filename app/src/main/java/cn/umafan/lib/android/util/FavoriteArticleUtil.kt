package cn.umafan.lib.android.util

import android.content.SharedPreferences
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.db.ArtInfoDao
import cn.umafan.lib.android.model.db.Article
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import org.json.JSONArray
import org.json.JSONObject


object FavoriteArticleUtil {

    private const val fileName = "favorites"

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    /**
     * 保存到收藏
     */
    fun saveFavorite(article: Article): Boolean {
        return try {
            val originData = JSONArray(sharedPreferences.getString(fileName, "[]")!!)
            val editor = sharedPreferences.edit()
            val data = JSONObject()
            data.put("name", article.name)
            data.put("author", article.author)
            data.put("translator", article.translator)
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
     * 取消收藏
     */
    fun cancelFavorite(article: Article): Boolean {
        return try {
            val editor = sharedPreferences.edit()
            val originData = JSONArray(sharedPreferences.getString(fileName, "[]")!!)
            for (i in 0 until originData.length()) {
                val obj = originData.getJSONObject(i)
                if (obj.getString("name") == article.name && obj.getString("author") == article.author && obj.getString(
                        "translator"
                    ) == article.translator
                ) {
                    originData.remove(i)
                    break
                }
            }
            editor.putString(fileName, originData.toString())
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
    fun getFavorites(): JSONArray {
        return JSONArray(sharedPreferences.getString(fileName, "[]")!!)
    }

    /**
     * 验证是否存在此收藏
     */
    fun existsFavorite(article: Article): Boolean {
        return try {
            val originData = JSONArray(sharedPreferences.getString(fileName, "[]")!!)
            for (i in 0 until originData.length()) {
                val obj = originData.getJSONObject(i)
                if (obj.getString("name") == article.name && obj.getString("author") == article.author && obj.getString(
                        "translator"
                    ) == article.translator
                ) return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 重构收藏夹结构
     */
    fun refactorFavorites(activity: MyBaseActivity): Boolean {
        return try {
            if (sharedPreferences.getString("refactor", "") != "done") {
                activity.shapeLoadingDialog?.show()
                val handler = DataBaseHandler(activity) {
                    val daoSession = it.obj as DaoSession
                    if (null != daoSession) {
                        val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                        val data = JSONArray()
                        sharedPreferences.all.forEach { (id, _) ->
                            val art = artInfoDao.queryBuilder().where(ArtInfoDao.Properties.Id.eq(id.toInt())).unique()
                            val json = JSONObject()
                            json.put("name", art.name)
                            json.put("author", art.author)
                            json.put("translator", art.translator)
                            data.put(json)
                        }
                        sharedPreferences.edit().putString(fileName, data.toString()).putString("refactor", "done").apply()
                    }
                }
                DatabaseCopyThread.addHandler(handler)
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}