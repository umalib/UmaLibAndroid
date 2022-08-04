package cn.umafan.lib.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.ArtInfoDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentHomeBinding
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.home.model.PageItem
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import com.angcyo.dsladapter.DslAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator

@SuppressLint("InflateParams")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null

    private val homeViewModel get() = _homeViewModel!!

    private var pageLen: Int = 0

    private var daoSession: DaoSession? = null

    private val mPageSelectorDialog by lazy {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_page_selector, null)
        val data = mutableListOf<PageItem>()
        homeViewModel.checkedList.clear()
        for (i in 0 until pageLen) {
            if (i + 1 != homeViewModel.currentPage.value) homeViewModel.checkedList.add(false)
            else homeViewModel.checkedList.add(true)
        }
        for (i in 1 until pageLen + 1) {
            data.add(PageItem(i, homeViewModel))
        }
        view.findViewById<RecyclerView>(R.id.page_recycler_view)?.adapter = DslAdapter(data)
        MaterialAlertDialogBuilder(
            requireActivity(),
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("${getString(R.string.select)}${getString(R.string.page_num)}")
            .setPositiveButton(
                R.string.confirm,
            ) { _, _ ->
                homeViewModel.currentPage.postValue(homeViewModel.selectedPage.value)
            }
            .setNegativeButton(R.string.cancel, null)
            .setView(view)
            .create()
    }

    private var mDataBaseLoadingProgressView: View? = null

    private var mDataBaseLoadingProgressIndicator: LinearProgressIndicator? = null

    private var mDataBaseLoadingProgressNum: AppCompatTextView? = null

    private val mDataBaseLoadingProgressDialog by lazy {
        mDataBaseLoadingProgressView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_loading_database, null)
        with(mDataBaseLoadingProgressView) {
            mDataBaseLoadingProgressIndicator = this?.findViewById(R.id.progress_indicator)
            mDataBaseLoadingProgressNum = this?.findViewById(R.id.progress_num)
        }

        MaterialAlertDialogBuilder(
            activity as MainActivity,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(getString(R.string.prepare_database))
            .setView(mDataBaseLoadingProgressView)
            .create()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView()

        with(binding) {
            nextPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! + 1)
            }
            lastPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! - 1)
            }
        }

        homeViewModel.currentPage.observe(viewLifecycleOwner) {
            loadArticles(
                it,
                arguments?.getSerializable("searchParams") as SearchBean?
            )
            binding.lastPageBtn.isEnabled = it > 1
            binding.nextPageBtn.isEnabled = it < pageLen
            binding.pageNumBtn.text = "$it/$pageLen 页"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.recyclerView.adapter = homeViewModel.articleDataAdapter
        binding.pageNumBtn.setOnClickListener { mPageSelectorDialog.show() }
        loadArticles(
            homeViewModel.currentPage.value,
            arguments?.getSerializable("searchParams") as SearchBean?
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadArticles(page: Int?, params: SearchBean?) {
        val handler = Handler(Looper.getMainLooper()) {
            when (it.what) {
                MyApplication.DATABASE_LOADING -> {
                    mDataBaseLoadingProgressIndicator?.progress = (it.obj as Double).toInt()
                    mDataBaseLoadingProgressNum?.text = "${String.format("%.2f", it.obj)}%"
                    mDataBaseLoadingProgressDialog.show()
                }
                MyApplication.DATABASE_LOADED -> {
                    mDataBaseLoadingProgressDialog.hide()
                    daoSession = it.obj as DaoSession
                    var count = 5
                    if (null != daoSession) {
                        val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                        count = kotlin.math.ceil(artInfoDao.count().toDouble() / 10).toInt()
                        if (0 == count) {
                            count = 1
                        }
                        var offset = 0
                        if (page != null) {
                            offset = (page - 1) * 10
                        }
                        val query = artInfoDao.queryBuilder().offset(offset)
                            .limit(10).orderDesc(ArtInfoDao.Properties.UploadTime).build()
                        val list = query.listLazy()
                        homeViewModel.loadArticles(list)
                        list.close()
                    } else {
                        homeViewModel.loadArticles(null)
                    }
                    pageLen = count
                    with(homeViewModel.currentPage.value) {
                        binding.lastPageBtn.isEnabled = this!! > 1
                        binding.nextPageBtn.isEnabled = this < pageLen
                        binding.pageNumBtn.text = "$this/$pageLen 页"
                    }
                }
            }
            false
        }
        DatabaseCopyThread(handler).start()
    }
}