package cn.umafan.lib.android.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivityReaderBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.db.Article
import cn.umafan.lib.android.model.db.ArticleDao
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.reader.model.ReaderJSInterface
import cn.umafan.lib.android.util.FavoriteArticleUtil
import cn.umafan.lib.android.util.HistoryUtil
import cn.umafan.lib.android.util.ReaderSettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import com.itheima.view.BridgeWebView
import com.liangguo.androidkit.app.ToastUtil

class ReaderActivity : MyBaseActivity() {
    private var daoSession: DaoSession? = null
    private var article: Article? = null
    private lateinit var binding: ActivityReaderBinding
    private var _mViewModel: ReaderViewModel? = null
    private val mViewModel get() = _mViewModel!!
    private lateinit var webView: BridgeWebView
    private var readerJSInterface: ReaderJSInterface? = null

    private var readerSettingView: View? = null
    private var fontSizeSlider: Slider? = null
    private var segmentSpaceSlider: Slider? = null
    private var textPreview1: MaterialTextView? = null
    private var textPreview2: MaterialTextView? = null
    private val readerSettingDialog by lazy {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        ).setTitle(getString(R.string.reader_setting))
            .setView(readerSettingView)
            .setPositiveButton(R.string.confirm) { _, _ ->
                if (!ReaderSettingUtil.setSetting(
                        "default",
                        mViewModel.fontSize.value!!,
                        mViewModel.segmentSpace.value!!
                    )
                )
                    ToastUtil.error(getString(R.string.reader_setting_fail))
                else ReaderJSInterface.initiativeStyle(webView)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mViewModel =
            ViewModelProvider(this)[ReaderViewModel::class.java]

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initView()
        loadArticle()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        readerSettingView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_reader_setting, null)
        fontSizeSlider = readerSettingView?.findViewById(R.id.font_size_slider)
        fontSizeSlider?.value = when (mViewModel.fontSize.value) {
            "small" -> 1F
            "large" -> 3F
            else -> 2F
        }
        segmentSpaceSlider = readerSettingView?.findViewById(R.id.segment_space_slider)
        segmentSpaceSlider?.value = when (mViewModel.segmentSpace.value) {
            "wider" -> 2F
            else -> 1F
        }
        textPreview1 = readerSettingView?.findViewById(R.id.text_preview_1)
        textPreview2 = readerSettingView?.findViewById(R.id.text_preview_2)
        fontSizeSlider?.addOnChangeListener { _, value, _ ->
            when (value.toInt()) {
                1 -> {
                    textPreview1!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_TitleMedium
                    )
                    textPreview2!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_TitleMedium
                    )
                    mViewModel.fontSize.value = "small"
                }
                2 -> {
                    textPreview1!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_TitleLarge
                    )
                    textPreview2!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_TitleLarge
                    )
                    mViewModel.fontSize.value = "normal"
                }
                3 -> {
                    textPreview1!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_DisplaySmall
                    )
                    textPreview2!!.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_Material3_DisplaySmall
                    )
                    mViewModel.fontSize.value = "large"
                }
            }
        }
        segmentSpaceSlider?.addOnChangeListener { _, value, _ ->
            when (value.toInt()) {
                1 -> {
                    textPreview1!!.setPadding(5)
                    textPreview2!!.setPadding(5)
                    mViewModel.segmentSpace.value = "normal"
                }
                2 -> {
                    textPreview1!!.setPadding(12)
                    textPreview2!!.setPadding(12)
                    mViewModel.segmentSpace.value = "wider"
                }
            }
        }
        with(binding) {
            readerSetting.setOnClickListener {
                readerSettingDialog.show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadArticle() {
        val handler = DataBaseHandler(this) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val articleDao = daoSession!!.articleDao
                val query = articleDao.queryBuilder()
                    .where(
                        ArticleDao.Properties.Id
                            .eq(intent.getIntExtra("id", 1))
                    )
                    .build()
                article = query.unique()
                binding.toolbar.title = article?.name

                initCollectBtn()

                webView = binding.articleWebView
                webView.settings.javaScriptEnabled = true
                webView.loadUrl("file:///android_asset/reader/index.html")
                // 设置js与android的桥接类
                readerJSInterface = ReaderJSInterface(webView, article!!)
                webView.addBridgeInterface(readerJSInterface)

                HistoryUtil.saveHistory(article!!)
            }
        }
        DatabaseCopyThread.addHandler(handler)
    }

    private fun initCollectBtn() {
        mViewModel.collected.observe(this) {
            if (FavoriteArticleUtil.existsFavorite(article!!)) {
                binding.collect.setImageResource(R.drawable.ic_baseline_star_24)
                binding.collect.setOnClickListener {
                    if (FavoriteArticleUtil.cancelFavorite(
                            article!!
                        )
                    ) {
                        ToastUtil.success(getString(R.string.cancel_collect_success))
                        binding.collect.setImageResource(R.drawable.ic_baseline_star_outline_24)
                        mViewModel.collected.value = false
                    } else ToastUtil.error(getString(R.string.cancel_collect_fail))
                }
            } else {
                binding.collect.setOnClickListener {
                    if (FavoriteArticleUtil.saveFavorite(
                            article!!
                        )
                    ) {
                        ToastUtil.success(getString(R.string.collect_success))
                        binding.collect.setImageResource(R.drawable.ic_baseline_star_24)
                        mViewModel.collected.value = true
                    } else ToastUtil.error(getString(R.string.collect_fail))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}