package cn.umafan.lib.android.ui.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
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
import cn.umafan.lib.android.databinding.ActivityMainBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.model.db.ArtInfoDao
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.model.db.Tag
import cn.umafan.lib.android.model.db.TagDao
import cn.umafan.lib.android.ui.MainIntroActivity
import cn.umafan.lib.android.ui.main.model.CreatorSuggestionAdapter
import cn.umafan.lib.android.ui.main.model.TagSelectedItem
import cn.umafan.lib.android.ui.main.model.TagSuggestionAdapter
import cn.umafan.lib.android.ui.setting.SettingActivity
import cn.umafan.lib.android.util.FavoriteArticleUtil
import cn.umafan.lib.android.util.SettingUtil
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.className
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.liangguo.androidkit.app.ToastUtil
import com.liangguo.androidkit.app.getAppCacheSize
import com.liangguo.androidkit.app.startNewActivity
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import kotlin.system.exitProcess


@SuppressLint("InflateParams")
class MainActivity : MyBaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var _mViewModel: MainViewModel? = null
    val mViewModel get() = _mViewModel!!
    private var daoSession: DaoSession? = null

    //???????????????flag
    private var isExit = false
    private val mHandler = Handler(Looper.getMainLooper()) {
        isExit = false
        false
    }

    private var creatorList = mutableSetOf<String>()
    private var tagList = mutableSetOf<Tag>()

    /**
     * ????????????dialog
     */
    private var searchFilterView: View? = null
    private var tagTextView: MaterialAutoCompleteTextView? = null
    private var tagExceptTextView: MaterialAutoCompleteTextView? = null
    private var creatorTextView: MaterialAutoCompleteTextView? = null
    private val searchFilterDialog by lazy {
        val tags = tagList.toList()
        val tagAdapter = TagSuggestionAdapter(tags)
        val tagExceptAdapter = TagSuggestionAdapter(tags)
        val creatorAdapter = CreatorSuggestionAdapter(creatorList.toList())

        searchFilterView = LayoutInflater.from(this).inflate(R.layout.dialog_search_filter, null)

        tagTextView = searchFilterView!!.findViewById(R.id.tag_textView)
        tagTextView?.setAdapter(tagAdapter)
        tagTextView?.setOnItemClickListener { adaptorView, _, i, _ ->
            tagTextView?.setText("")
            val item = adaptorView.adapter.getItem(i) as Tag
            with(mViewModel) {
                viewModelScope.launch {
                    searchParams.value?.tags?.add(item)
                    // ??????flow?????????????????????????????????????????????????????????
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
            tagExceptTextView?.setText("")
            val item = adaptorView.adapter.getItem(i) as Tag
            with(mViewModel) {
                viewModelScope.launch {
                    searchParams.value?.exceptedTags?.add(item)
                    // ??????flow?????????????????????????????????????????????????????????
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
            this
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


    @SuppressLint("RestrictedApi")
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
                R.id.nav_home,
                R.id.nav_favorites,
                R.id.nav_history,
                R.id.nav_thanks,
                R.id.nav_setting
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarMain.toolbarLayout.title = destination.label
            binding.appBarMain.refresh.isVisible =
                !(null != destination.label && destination.label!! == getString(R.string.thanks))
        }

        with(binding) {
            // ?????????????????????
            appBarMain.searchView.setKeepQuery(true)
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
            //??????????????????
            appBarMain.refresh.setOnClickListener {
                when (navController.currentDestination?.label) {
                    getString(R.string.home) -> {
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
                    getString(R.string.my_favorites) -> {
                        navController.navigate(R.id.nav_favorites)
                    }
                    getString(R.string.history) -> {
                        navController.navigate(R.id.nav_history)
                    }
                }
            }
            appBarMain.indexBg.apply {
                try {
                    val uri = SettingUtil.getImageBackground(SettingUtil.APP_BAR_BG)
                    if (null != uri) setImageDrawable(Drawable.createFromPath(uri.path))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                        val selectedTagRecyclerView =
                            searchFilterView!!.findViewById<RecyclerView>(R.id.selected_tags_recycler_view)
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
                    if (null != searchFilterView) {
                        val selectedExceptTagRecyclerView =
                            searchFilterView!!.findViewById<RecyclerView>(R.id.selected_except_tags_recycler_view)
                        selectedExceptTagRecyclerView.adapter = DslAdapter(tagSelectedList)
                    }
                }
            }
        }

        // ????????????????????????????????????????????????????????????????????????????????????
        val dataBaseThread = DatabaseCopyThread()
        dataBaseThread.isDaemon = true
        dataBaseThread.start()

        baseViewModel.getUpdate(this, false)

        loadSearchOptions()

        val sharedPreferences = getSharedPreferences("introduction", 0)
        if (null == sharedPreferences.getString("done", null)) {
            MainIntroActivity::class.startNewActivity()
            sharedPreferences.edit().putString("done", "right").apply()
        }

        FavoriteArticleUtil.refactorFavorites(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    fun exit() {
        if (!isExit) {
            isExit = true
            ToastUtil.info("?????????????????????????????????")
            //??????handler??????????????????????????????
            mHandler.sendEmptyMessageDelayed(0, 2000)
        } else {
            finish()
            exitProcess(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val item = menu.findItem(R.id.action_search)
        binding.appBarMain.searchView.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // ???????????????
            R.id.action_search_settings -> searchFilterDialog.show()
            R.id.app_intro -> MainIntroActivity::class.startNewActivity()
            R.id.setting -> SettingActivity::class.startNewActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * ????????????
     */
    @SuppressLint("RestrictedApi")
    fun search() {
        shapeLoadingDialog?.show()
        val bundle = Bundle()
        bundle.putSerializable("searchParams", mViewModel.searchParams.value)
        while (navController.backStack.size >= 2) {
            navController.popBackStack()
        }
        navController.navigate(R.id.nav_home, bundle)
        Log.d(this.javaClass.simpleName, navController.backStack.last.destination.label.toString())
    }

    /**
     * ???????????????????????????
     */
    private fun loadSearchOptions() {
        DatabaseCopyThread.addHandler(DataBaseHandler(this) { it ->
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                with(daoSession!!) {
                    val artInfoDao: ArtInfoDao = artInfoDao
                    val tagDao: TagDao = tagDao
                    // ?????????????????????
                    artInfoDao.queryBuilder().orderDesc(ArtInfoDao.Properties.Name).listLazy()
                        .forEach {
                            if (it.author.isNotBlank()) creatorList.add(it.author)
                            if (it.translator.isNotBlank()) creatorList.add(it.translator)
                        }
                    creatorList = creatorList.toSortedSet()
                    // ??????tag
                    tagDao.queryBuilder().orderDesc(TagDao.Properties.Name).listLazy().forEach {
                        tagList.add(it)
                    }
                }
            }
        })
    }

    companion object {
        @Throws(FileNotFoundException::class)
        fun getBitmapFromUri(uri: Uri, activity: MainActivity): Bitmap {
            val parcelFileDescriptor = activity.contentResolver.openFileDescriptor(uri, "r")
            return BitmapFactory.decodeFileDescriptor(parcelFileDescriptor?.fileDescriptor)
        }
    }
}