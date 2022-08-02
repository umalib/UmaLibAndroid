package cn.umafan.lib.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.beans.ArtInfoDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentHomeBinding
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.main.MainActivity
import kotlin.math.ceil

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null

    private val homeViewModel get() = _homeViewModel!!

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
            val pageLen: Long = loadArticles(
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
        loadArticles(
            homeViewModel.currentPage.value,
            arguments?.getSerializable("searchParams") as SearchBean?
        )
    }

    private fun loadArticles(page: Int?, params: SearchBean?): Long {
        val daoSession: DaoSession? = (activity as MainActivity).daoSession()
        var count: Long = 5
        if (null != daoSession) {
            val artInfoDao: ArtInfoDao = daoSession.artInfoDao
            count = ceil(artInfoDao.count().toDouble() / 10).toLong()
            if (0L == count) {
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