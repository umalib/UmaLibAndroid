package cn.umafan.lib.android.ui.history

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.FragmentHistroyBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.db.ArtInfoDao
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.HistoryUtil
import cn.umafan.lib.android.util.SettingUtil
import com.liangguo.androidkit.app.ToastUtil

@SuppressLint("InflateParams")
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistroyBinding? = null

    private val binding get() = _binding!!

    private lateinit var _historyViewModel: HistoryViewModel

    private val historyViewModel get() = _historyViewModel

    private var daoSession: DaoSession? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]

        _binding = FragmentHistroyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViews()

        loadArticles()

        return root
    }

    private fun initViews() {
        with(binding) {
            recyclerView.adapter = historyViewModel.articleDataAdapter
            layout.apply {
                val uri = SettingUtil.getImageBackground(SettingUtil.INDEX_BG)
                if (null != uri) background = Drawable.createFromPath(uri.path)
            }
        }
    }

    private fun loadArticles() {
        val idList = HistoryUtil.getHistory()
        if (idList.isEmpty()) {
            ToastUtil.info(getString(R.string.no_data))
        }
        val handler = DataBaseHandler(activity as MyBaseActivity) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                val data = mutableListOf<ArtInfo>()
                idList.forEach { id ->
                    val art = artInfoDao.queryBuilder().where(ArtInfoDao.Properties.Id.eq(id)).unique()
                    if (null != art) data.add(art)
                }
                historyViewModel.loadArticles(data)
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onResume() {
        super.onResume()
        loadArticles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}