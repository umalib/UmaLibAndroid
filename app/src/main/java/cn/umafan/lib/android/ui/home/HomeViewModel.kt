package cn.umafan.lib.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.PageSelectorViewModel
import cn.umafan.lib.android.ui.home.model.ArticleInfoItem
import cn.umafan.lib.android.ui.home.model.PageItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : PageSelectorViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val pageLen = MutableLiveData(1)

    private val articleData = MutableStateFlow(listOf<ArtInfo>())

    val currentPage = MutableLiveData(1)

    val articleDataAdapter = DslAdapter()

    val pageSelectorAdapter = DslAdapter()

    val pageData = MutableStateFlow(listOf<Int>())

    init {
        viewModelScope.launch {
            articleData.collect {
                articleDataAdapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    it.forEach {
                        adapterItems.add(ArticleInfoItem(it))
                    }
                }
            }
        }
        viewModelScope.launch {
            pageData.collect {
                pageSelectorAdapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    it.forEach {
                        adapterItems.add(PageItem(it, this@HomeViewModel))
                    }
                }
                pageSelectorAdapter.notifyDataChanged()
            }
        }
    }

    /**
     * 异步加载文章
     */
    fun loadArticles(list: List<ArtInfo>?) {
        viewModelScope.launch {
            var data = list
            if (null == data) {
                data = mutableListOf()
            }
            articleData.emit(data)
        }
    }
}