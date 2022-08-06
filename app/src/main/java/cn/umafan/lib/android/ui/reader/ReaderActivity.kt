package cn.umafan.lib.android.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import cn.umafan.lib.android.beans.Article
import cn.umafan.lib.android.beans.ArticleDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.ActivityReaderBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.reader.model.ReaderJSInterface
import com.itheima.view.BridgeWebView

class ReaderActivity : MyBaseActivity() {
    private var daoSession: DaoSession? = null
    private var article: Article? = null
    private lateinit var binding: ActivityReaderBinding
    private lateinit var webView: BridgeWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        loadArticle()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadArticle() {
        val handler = DataBaseHandler(this) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val articleDao = daoSession!!.articleDao
                val query = articleDao.queryBuilder()
                    .where(ArticleDao.Properties.Id.eq(intent.getIntExtra("id", 1)))
                    .build()
                article = query.unique()
                binding.toolbar.title = article?.name
                webView = binding.articleWebView
                webView.settings.javaScriptEnabled = true
                webView.loadUrl("file:///android_asset/reader/index.html")
                // 设置js与android的桥接类
                webView.addBridgeInterface(ReaderJSInterface(webView, article!!))
            }
        }
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}