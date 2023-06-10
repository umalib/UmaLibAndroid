package cn.umafan.lib.android.ui.recommend

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.FragmentRecommendBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.model.db.Dict
import cn.umafan.lib.android.model.db.DictDao
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.liangguo.androidkit.app.ToastUtil
import org.greenrobot.greendao.query.QueryBuilder
import kotlin.math.log

val typeMap = listOf(
    listOf(1, 2),
    listOf(3, 4),
    listOf(5),
    listOf(6)
)

@SuppressLint("InflateParams")
class RecommendFragment : Fragment() {

    private var _binding: FragmentRecommendBinding? = null

    private val binding get() = _binding!!

    private var _recommendViewModel: RecommendViewModel? = null

    private val recommendViewModel get() = _recommendViewModel!!

    private var daoSession: DaoSession? = null

    private var isShowing = false

    @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _recommendViewModel =
            ViewModelProvider(this)[RecommendViewModel::class.java]

        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView()

        with(recommendViewModel) {
            type.observe(viewLifecycleOwner) {
                loadRec(it)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        with(binding) {
            layout.apply {
                val uri = SettingUtil.getImageBackground(SettingUtil.INDEX_BG)
                if (null != uri) background = Drawable.createFromPath(uri.path)
            }
            recTab.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    recommendViewModel.type.value = tab?.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    /**
     * 查询推荐详情
     */
    @SuppressLint("SetTextI18n")
    private fun loadRec(type: Int) {
        Log.d("RecommendFragment", "loadRec: $type")
        val handler = DataBaseHandler(activity as MainActivity) {
            daoSession = it.obj as DaoSession
            val count: Long
            if (null != daoSession) {
                val dictDao: DictDao = daoSession!!.dictDao
                val query: QueryBuilder<Dict> = dictDao.queryBuilder()
                count = query.count()

                if (count == 0L) {
                    ToastUtil.info(getString(R.string.no_data))
                }
                val list = query.build().listLazy()

//                recommendViewModel.loadArticles(list)
                list.close()
            } else {
                recommendViewModel.loadArticles(null)
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }

    /**
     * 查询解释字典
     */
    @SuppressLint("SetTextI18n")
    private fun loadDict() {
        val handler = DataBaseHandler(activity as MainActivity) {
            daoSession = it.obj as DaoSession
            val count: Long
            if (null != daoSession) {
                val dictDao: DictDao = daoSession!!.dictDao
                val query: QueryBuilder<Dict> = dictDao.queryBuilder()
                count = query.count()

                if (count == 0L) {
                    ToastUtil.info(getString(R.string.no_data))
                }
                val list = query.build().listLazy()

//                recommendViewModel.loadArticles(list)
                list.close()
            } else {
                recommendViewModel.loadArticles(null)
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }
}