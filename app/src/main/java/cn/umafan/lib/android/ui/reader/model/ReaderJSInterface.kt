package cn.umafan.lib.android.ui.reader.model

import android.text.TextUtils
import cn.umafan.lib.android.beans.Article
import cn.umafan.lib.android.util.ReaderSettingUtil
import com.itheima.view.BridgeWebView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ReaderJSInterface(
    private val webView: BridgeWebView,
    private val article: Article
) {
    // 以此格式写方法
    fun getArticle(str: Array<String>) {
        println(str[0])
        //解析js callback方法
        val mJson = JSONObject(str[0])
        val callback = mJson.optString("callback") //解析js回调方法

        val json = JSONObject()
        json.put("content", article.content)
        json.put("name", article.name)
        json.put("source", article.source)
        json.put("note", article.note)
        json.put("translator", article.translator)
        json.put("author", article.author)
        val tagList = JSONArray(article.taggedList.sortedWith { a, b ->
            if (a.tag.type == b.tag.type)
                a.tag.name.compareTo(b.tag.name)
            else
                b.tag.type.compareTo(a.tag.type)
        }.map { tagged -> tagged.tag.name })
        json.put("tags", tagList)
        json.put(
            "time",
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.CHINA
            ).format(article.uploadTime.toLong() * 1000)
        )
        json.put("setting", ReaderSettingUtil.getSetting("default"))

        invokeJavaScript(callback, json.toString())
    }

    companion object {
        fun initiativeStyle(webView: BridgeWebView) {
            val callback = "renderAct"

            val json = JSONObject()
            json.put("setting", ReaderSettingUtil.getSetting("default"))

            if (TextUtils.isEmpty(callback)) return
            //调用js方法必须在主线程
            webView.post { webView.loadUrl("javascript:$callback($json)") }
        }
    }

    /**
     * 统一管理所有android调用js方法
     *
     * @param callback js回调方法名
     * @param json     传递json数据
     */
    private fun invokeJavaScript(callback: String, json: String) {
        println("callbackName: $callback  data: $json")
        if (TextUtils.isEmpty(callback)) return
        //调用js方法必须在主线程
        webView.post { webView.loadUrl("javascript:$callback($json)") }
    }
}