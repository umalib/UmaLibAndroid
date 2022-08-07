package cn.umafan.lib.android.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.ArtInfo
import cn.umafan.lib.android.beans.ArtInfoDao
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentFavoritesBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.home.model.PageItem
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.FavoriteArticleUtil
import com.angcyo.dsladapter.DslAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liangguo.androidkit.app.ToastUtil
import org.greenrobot.greendao.query.QueryBuilder

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null

    private val binding get() = _binding!!

    private lateinit var _favoritesViewModel: FavoritesViewModel

    private val favoritesViewModel get() = _favoritesViewModel

    private var daoSession: DaoSession? = null

    private var pageLen: Int = 0

    private val mPageSelectorDialog by lazy {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_page_selector, null)
        val data = mutableListOf<PageItem>()
        favoritesViewModel.checkedList.clear()
        for (i in 0 until pageLen) {
            if (i + 1 != favoritesViewModel.currentPage.value) favoritesViewModel.checkedList.add(false)
            else favoritesViewModel.checkedList.add(true)
        }
        for (i in 1 until pageLen + 1) {
            data.add(PageItem(i, favoritesViewModel))
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
                favoritesViewModel.currentPage.postValue(favoritesViewModel.selectedPage.value)
            }
            .setNegativeButton(R.string.cancel, null)
            .setView(view)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _favoritesViewModel =
            ViewModelProvider(this)[FavoritesViewModel::class.java]

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        favoritesViewModel.currentPage.observe(viewLifecycleOwner) {
            loadArticles(
                it
            )
            binding.lastPageBtn.isEnabled = it > 1
            binding.nextPageBtn.isEnabled = it < pageLen
            binding.pageNumBtn.text = "$it/$pageLen 页"
        }

        initViews()

        return root
    }

    private fun initViews() {
        with(binding) {
            nextPageBtn.setOnClickListener {
                favoritesViewModel.currentPage.postValue(favoritesViewModel.currentPage.value!! + 1)
            }
            lastPageBtn.setOnClickListener {
                favoritesViewModel.currentPage.postValue(favoritesViewModel.currentPage.value!! - 1)
            }
            recyclerView.adapter = favoritesViewModel.articleDataAdapter
            pageNumBtn.setOnClickListener { mPageSelectorDialog.show() }
        }
    }

    private fun loadArticles(page: Int?) {
        val idList = FavoriteArticleUtil.getFavorites()
        if (idList.isEmpty()){
            ToastUtil.info(getString(R.string.no_data))
        }
        pageLen = idList.size / 10 + 1
        val handler = DataBaseHandler(activity as MyBaseActivity) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                val offset = if (page != null) {
                    (page - 1) * 10
                } else {
                    0
                }
                val list = if (offset + 10 > idList.size) idList.subList(offset, idList.size)
                else idList.subList(offset, offset + 10)
                val data = mutableListOf<ArtInfo>()
                list.forEach {
                    data.add(artInfoDao.queryBuilder().where(
                        ArtInfoDao.Properties.Id.eq(it)
                    ).build().unique())
                }
                favoritesViewModel.loadArticles(data)
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}