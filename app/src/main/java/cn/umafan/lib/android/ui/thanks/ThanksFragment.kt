package cn.umafan.lib.android.ui.thanks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.FragmentThanksBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import org.json.JSONArray

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

        with(binding){
            settingItemFeedback.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://bbs.nga.cn/read.php?tid=32535194")
                startActivity(intent)
            }
            settingItemGithub.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/umalib/UmaLibAndroid")
                startActivity(intent)
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
                val list = creatorDao.queryBuilder().build().listLazy()
                val creatorsStr = list.first().names
                val creators = creatorsStr.substring(1, creatorsStr.length - 1).split(",").map { it ->
                    it.substring(1, it.length - 1)
                }.sortedWith { a, b ->
                    a.compareTo(b)
                }.joinToString(" | ")
                binding.othersContent.text = creators
            }
        }
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}