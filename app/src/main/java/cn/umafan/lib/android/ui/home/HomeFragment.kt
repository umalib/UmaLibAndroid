package cn.umafan.lib.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.ArtInfo
import cn.umafan.lib.android.beans.ArtInfoDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentHomeBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.home.model.PageItem
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import com.angcyo.dsladapter.DslAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.greenrobot.greendao.query.QueryBuilder

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
    }

    /**
     * 从数据库中查询内容
     * @param page 查询页数
     * @param params 查询主要参数
     */
    @SuppressLint("SetTextI18n")
    private fun loadArticles(page: Int?, params: SearchBean?) {
        val handler = DataBaseHandler(activity as MainActivity) {
            daoSession = it.obj as DaoSession
            var count = 5L
            if (null != daoSession) {
                val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                val offset = if (page != null) {
                    (page - 1) * 10
                } else {
                    0
                }
                println("search-params: $params")
                val query: QueryBuilder<ArtInfo>
                if (null != params) {
                    query = artInfoDao.queryBuilder()
                    val keywordCond = query.or(
                        ArtInfoDao.Properties.Name.like("%${params.keyword}%"),
                        ArtInfoDao.Properties.Note.like("%${params.keyword}%")
                    )
                    val creatorCond = query.or(
                        ArtInfoDao.Properties.Author.eq(params.creator),
                        ArtInfoDao.Properties.Translator.eq(params.creator)
                    )
                    val cond = query.and(keywordCond, creatorCond)
                    query.where(
                        cond
                    )
                        .offset(offset)
                        .limit(10).orderDesc(ArtInfoDao.Properties.UploadTime)
                    count = query.count()
                } else {
                    query = artInfoDao.queryBuilder().offset(offset)
                        .limit(10).orderDesc(ArtInfoDao.Properties.UploadTime)
                    count = artInfoDao.count()
                }
                if (0L == count || 0L != count % 10L) {
                    count = count / 10L + 1
                } else {
                    count /= 10
                }
                val list = query.build().listLazy()
                homeViewModel.loadArticles(list)
                list.close()
            } else {
                homeViewModel.loadArticles(null)
            }
            pageLen = count.toInt()
            with(homeViewModel.currentPage.value) {
                binding.lastPageBtn.isEnabled = this!! > 1
                binding.nextPageBtn.isEnabled = this < pageLen
                binding.pageNumBtn.text = "$this/$pageLen 页"
            }
        }
        DatabaseCopyThread.addHandler(handler)
    }
}