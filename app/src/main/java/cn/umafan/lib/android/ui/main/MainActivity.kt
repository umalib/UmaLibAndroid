package cn.umafan.lib.android.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.*
import cn.umafan.lib.android.databinding.ActivityMainBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.main.model.TagSelectedItem
import cn.umafan.lib.android.ui.main.model.TagSuggestionAdapter
import com.angcyo.dsladapter.DslAdapter
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@SuppressLint("InflateParams")
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var _mViewModel: MainViewModel? = null
    private val mViewModel get() = _mViewModel!!
    private var daoSession: DaoSession? = null

    private var creatorList = mutableSetOf<String>()
    private var tagList = mutableSetOf<Tag>()

    /**
     * 搜索选项dialog
     */
    private var searchFilterView: View? = null
    private var tagTextView: MaterialAutoCompleteTextView? = null
    private var tagExceptTextView: MaterialAutoCompleteTextView? = null
    private var creatorTextView: MaterialAutoCompleteTextView? = null
    private val searchFilterDialog by lazy {
        val tags = tagList.toList()
        val tagAdapter = TagSuggestionAdapter(tags)
        val tagExceptAdapter = TagSuggestionAdapter(tags)
        val creatorAdapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_spinner_dropdown_item,
            creatorList.toTypedArray()
        )

        searchFilterView = LayoutInflater.from(this).inflate(R.layout.dialog_search_filter, null)

        tagTextView = searchFilterView!!.findViewById(R.id.tag_textView)
        tagTextView?.setAdapter(tagAdapter)
        tagTextView?.setOnItemClickListener { adaptorView, _, i, _ ->
            val item = tags.find { tag -> tag.name == adaptorView.adapter.getItem(i) }
            with(mViewModel) {
                viewModelScope.launch {
                    searchParams.value?.tags?.add(item!!)
                    // 为使flow数据得到相应，这里必须使用一个新的对象
                    val tmp = mutableSetOf<Tag>()
                    searchParams.value?.tags?.forEach {
                        tmp.add(it)
                    }
                    selectedTags.emit(tmp)
                }
            }
        }
        tagExceptTextView = searchFilterView!!.findViewById(R.id.tag_except_textView)
        tagExceptTextView?.setAdapter(tagExceptAdapter)
        tagExceptTextView?.setOnItemClickListener { adaptorView, _, i, _ ->
            val item = tags.find { tag -> tag.name == adaptorView.adapter.getItem(i) }
            with(mViewModel) {
                viewModelScope.launch {
                    searchParams.value?.exceptedTags?.add(item!!)
                    // 为使flow数据得到相应，这里必须使用一个新的对象
                    val tmp = mutableSetOf<Tag>()
                    searchParams.value?.exceptedTags?.forEach {
                        tmp.add(it)
                    }
                    selectedExceptTags.emit(tmp)
                }
            }
        }

        creatorTextView =
            searchFilterView!!.findViewById(R.id.creator_textView)
        creatorTextView?.setAdapter(creatorAdapter)

        MaterialAlertDialogBuilder(
            this@MainActivity,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(R.string.search_settings)
            .setView(searchFilterView)
            .setPositiveButton(R.string.confirm) { _, _ ->
                mViewModel.searchParams.value?.creator = creatorTextView?.text.toString()
                search()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    /**
     * 数据库操作进度dialog
     */
    private var mDataBaseLoadingProgressView: View? = null
    private var mDataBaseLoadingProgressIndicator: LinearProgressIndicator? = null
    private var mDataBaseLoadingProgressNum: AppCompatTextView? = null
    private val mDataBaseLoadingProgressDialog by lazy {
        mDataBaseLoadingProgressView =
            LayoutInflater.from(this).inflate(R.layout.dialog_loading_database, null)
        with(mDataBaseLoadingProgressView) {
            mDataBaseLoadingProgressIndicator = this?.findViewById(R.id.progress_indicator)
            mDataBaseLoadingProgressNum = this?.findViewById(R.id.progress_num)
        }

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(getString(R.string.prepare_database))
            .setView(mDataBaseLoadingProgressView)
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_favorites, R.id.nav_thanks
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarMain.toolbarLayout.title = destination.label
        }

        with(binding) {
            // 搜索框输入监听
            appBarMain.searchView.setOnQueryTextListener(object :
                SimpleSearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
                override fun onQueryTextCleared(): Boolean {
                    return false
                }
                override fun onQueryTextSubmit(query: String): Boolean {
                    mViewModel.searchParams.value?.keyword = query
                    search()
                    return false
                }
            })
            //重置搜索选项
            appBarMain.refresh.setOnClickListener {
                with(mViewModel) {
                    viewModelScope.launch {
                        searchParams.value = SearchBean()
                        selectedTags.emit(mutableSetOf())
                        selectedExceptTags.emit(mutableSetOf())
                    }
                }
                tagTextView?.setText("")
                tagExceptTextView?.setText("")
                creatorTextView?.setText("")
                search()
            }
        }

        with(mViewModel) {
            viewModelScope.launch {
                selectedTags.collect {
                    val tagSelectedList = mutableListOf<TagSelectedItem>()
                    it.forEach { tag ->
                        tagSelectedList.add(TagSelectedItem(tag, mViewModel, true))
                    }
                    if (null != searchFilterView) {
                        val selectedTagRecyclerView = searchFilterView!!.findViewById<RecyclerView>(R.id.selected_tags_recycler_view)
                        selectedTagRecyclerView.adapter = DslAdapter(tagSelectedList)
                    }
                }
            }
            viewModelScope.launch {
                selectedExceptTags.collect {
                    val tagSelectedList = mutableListOf<TagSelectedItem>()
                    it.forEach { tag ->
                        tagSelectedList.add(TagSelectedItem(tag, mViewModel, false))
                    }
                    Log.d("fuck", ": $tagSelectedList")
                    if (null != searchFilterView) {
                        val selectedExceptTagRecyclerView = searchFilterView!!.findViewById<RecyclerView>(R.id.selected_except_tags_recycler_view)
                        selectedExceptTagRecyclerView.adapter = DslAdapter(tagSelectedList)
                    }
                }
            }
        }

        // 新建一个守护线程，每个数据库操作任务自动进入队列排队处理
        val dataBaseThread = DatabaseCopyThread()
        dataBaseThread.isDaemon = true
        dataBaseThread.start()

        loadSearchOptions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val item = menu.findItem(R.id.action_search)
        binding.appBarMain.searchView.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 搜索过滤项
            R.id.action_search_settings -> {
                searchFilterDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * 执行搜索
     */
    fun search() {
        val bundle = Bundle()
        bundle.putSerializable("searchParams", mViewModel.searchParams.value)
        navController.navigate(R.id.nav_home, bundle)
    }

    /**
     * 用于展示数据库加载进度条
     */
    @SuppressLint("SetTextI18n")
    fun dataBaseLoadingDialog(progress: Double) {
        if (progress < 100.0) {
            mDataBaseLoadingProgressIndicator?.progress = progress.toInt()
            mDataBaseLoadingProgressNum?.text = "${String.format("%.2f", progress)}%"
            mDataBaseLoadingProgressDialog.show()
        } else mDataBaseLoadingProgressDialog.hide()
    }

    /**
     * 加载可用的搜索选项
     */
    private fun loadSearchOptions() {
        DatabaseCopyThread.addHandler(DataBaseHandler(this) { it ->
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                with(daoSession!!) {
                    val artInfoDao: ArtInfoDao = artInfoDao
                    val tagDao: TagDao = tagDao
                    // 获取创作者列表
                    artInfoDao.queryBuilder().orderDesc(ArtInfoDao.Properties.Name).listLazy().forEach {
                        if (it.author.isNotBlank()) creatorList.add(it.author)
                        if (it.translator.isNotBlank()) creatorList.add(it.translator)
                    }
                    creatorList = creatorList.toSortedSet()
                    // 获取tag
                    tagDao.queryBuilder().orderDesc(TagDao.Properties.Name).listLazy().forEach {
                        tagList.add(it)
                    }
                }
            }
        })
    }
}