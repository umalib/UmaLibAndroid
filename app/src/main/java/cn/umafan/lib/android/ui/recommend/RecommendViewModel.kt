package cn.umafan.lib.android.ui.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.model.PageSelectorViewModel
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.ui.home.model.ArticleInfoItem
import cn.umafan.lib.android.ui.home.model.PageItem
import cn.umafan.lib.android.ui.recommend.model.RecInfo
import cn.umafan.lib.android.ui.recommend.model.RecTabItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecommendViewModel : PageSelectorViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val recData = MutableStateFlow(listOf<RecInfo>())

    val currentPage = MutableLiveData(1)

    val type = MutableLiveData(0)

    val recDataAdapter = DslAdapter()

    init {
        viewModelScope.launch {
            recData.collect {
                recDataAdapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    it.forEach {
                        adapterItems.add(RecTabItem(it, true))
                    }
                }
            }
        }
    }

    /**
     * 异步加载推荐数据
     */
    fun loadRecs(list: List<RecInfo>?) {
        viewModelScope.launch {
            var data = list
            if (null == data) {
                data = mutableListOf()
            }
            recData.emit(data)
        }
    }
}