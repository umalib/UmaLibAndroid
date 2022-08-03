package cn.umafan.lib.android.ui.home

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.ArtInfoDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentHomeBinding
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.home.model.PageItem
import cn.umafan.lib.android.ui.main.MainActivity
import com.angcyo.dsladapter.DslAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import kotlin.math.ceil

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null

    private val homeViewModel get() = _homeViewModel!!

    private var pageLen: Int = 0

    fun mPageSelectorDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_page_selector, null)
        val data = mutableListOf<PageItem>()
        homeViewModel.checkedList.clear()
        for(i in 0 until pageLen) {
            if (i + 1 != homeViewModel.currentPage.value) homeViewModel.checkedList.add(false)
            else homeViewModel.checkedList.add(true)
        }
        Log.d("fucka", "mPageSelectorDialog: ${homeViewModel.checkedList}")
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
                DialogInterface.OnClickListener { dialogInterface, i ->
                    homeViewModel.currentPage.postValue(homeViewModel.selectedPage.value)
                })
            .setNegativeButton(R.string.cancel, null)
            .setView(view)
            .create().show()
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
            pageLen = loadArticles(
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
        binding.pageNumBtn.setOnClickListener { mPageSelectorDialog() }
        loadArticles(
            homeViewModel.currentPage.value,
            arguments?.getSerializable("searchParams") as SearchBean?
        )
    }

    private fun loadArticles(page: Int?, params: SearchBean?): Int {
        val daoSession: DaoSession? = (activity as MainActivity).daoSession()
        var count: Int = 5
        if (null != daoSession) {
            val artInfoDao: ArtInfoDao = daoSession.artInfoDao
            count = ceil(artInfoDao.count().toDouble() / 10).toInt()
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
        return count
    }
}