package cn.umafan.lib.android.ui.reader.model

import android.text.TextUtils
import cn.umafan.lib.android.beans.Article
import com.itheima.view.BridgeWebView
import org.json.JSONObject

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
//        json.put("source", article.source)
//        json.put("note", article.note)
//        json.put("translator", article.translator)
//        json.put("author", article.author)
//        json.put("tags", article.taggedList)
//        json.put("time", article.uploadTime)

        invokeJavaScript(callback, json.toString())
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