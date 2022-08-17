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
            layout.apply {
                val uri = SettingUtil.getImageBackground(SettingUtil.INDEX_BG)
                if (null != uri) background = Drawable.createFromPath(uri.path)
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