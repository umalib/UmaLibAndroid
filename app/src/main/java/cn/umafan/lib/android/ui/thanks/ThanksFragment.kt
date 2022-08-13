package cn.umafan.lib.android.ui.thanks

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.databinding.FragmentThanksBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.SettingUtil

class ThanksFragment : Fragment() {
    private var _binding: FragmentThanksBinding? = null

    private var daoSession: DaoSession? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThanksBinding.inflate(inflater, container, false)

        with(binding) {
            appVersion.text = MyApplication.getVersion().name
            settingItemFeedback.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://bbs.nga.cn/read.php?tid=33041357")
                startActivity(intent)
            }
            settingItemGithub.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/umalib/UmaLibAndroid")
                startActivity(intent)
            }
            settingItemCheckUpdate.setOnClickListener {
                (activity as MainActivity).shapeLoadingDialog?.dialog?.show()
                (activity as MainActivity).mViewModel.getUpdate(activity as MainActivity, true)
            }
            layout.apply {
                val uri = SettingUtil.getImageBackground(SettingUtil.INDEX_BG)
                if (null != uri) background = Drawable.createFromPath(SettingUtil.getRealPathFromUriAboveApi19(requireContext(), uri))
            }
        }

        loadCreators()

        return binding.root
    }

    private fun loadCreators() {
        val handler = DataBaseHandler(activity as MyBaseActivity) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val creatorDao = daoSession!!.creatorDao
                val creators = creatorDao.queryBuilder().build().listLazy().first().names
                binding.othersContent.text =
                    creators.substring(2, creators.length - 2).replace("\",\"", " | ")
            }
        }
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}